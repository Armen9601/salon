package com.gevorgyan.salon.service;

import com.gevorgyan.salon.domain.Availability;
import com.gevorgyan.salon.domain.Barber;
import com.gevorgyan.salon.domain.Booking;
import com.gevorgyan.salon.domain.BusyTime;
import com.gevorgyan.salon.domain.ServiceOption;
import com.gevorgyan.salon.repo.AvailabilityRepository;
import com.gevorgyan.salon.repo.BarberRepository;
import com.gevorgyan.salon.repo.BookingRepository;
import com.gevorgyan.salon.repo.BusyTimeRepository;
import com.gevorgyan.salon.repo.ServiceOptionRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
  private static final Duration SLOT_STEP = Duration.ofMinutes(15);

  private final BarberRepository barberRepository;
  private final ServiceOptionRepository serviceOptionRepository;
  private final AvailabilityRepository availabilityRepository;
  private final BookingRepository bookingRepository;
  private final BusyTimeRepository busyTimeRepository;
  private final BusinessHoursService businessHoursService;
  private final MailService mailService;

  public BookingService(BarberRepository barberRepository,
      ServiceOptionRepository serviceOptionRepository,
      AvailabilityRepository availabilityRepository,
      BookingRepository bookingRepository,
      BusyTimeRepository busyTimeRepository,
      BusinessHoursService businessHoursService,
      MailService mailService) {
    this.barberRepository = barberRepository;
    this.serviceOptionRepository = serviceOptionRepository;
    this.availabilityRepository = availabilityRepository;
    this.bookingRepository = bookingRepository;
    this.busyTimeRepository = busyTimeRepository;
    this.businessHoursService = businessHoursService;
    this.mailService = mailService;
  }

  public record Slot(LocalDateTime startAt, LocalDateTime endAt) {}

  public record Quote(int totalDurationMinutes, int totalPriceAmd) {}

  private record Range(LocalDateTime startAt, LocalDateTime endAt) {}

  public boolean isValidServiceSelection(Set<Long> serviceIds) {
    List<ServiceOption> services = serviceOptionRepository.findAllById(serviceIds);
    boolean hasCombo = services.stream().anyMatch(s -> "HAIRCUT_BEARD".equalsIgnoreCase(s.getCode()));
    if (!hasCombo) return true;
    boolean hasHaircut = services.stream().anyMatch(s -> "HAIRCUT".equalsIgnoreCase(s.getCode()));
    boolean hasBeard = services.stream().anyMatch(s -> "BEARD".equalsIgnoreCase(s.getCode()));
    return !(hasHaircut || hasBeard);
  }

  public Quote quote(Set<Long> serviceIds) {
    List<ServiceOption> services = serviceOptionRepository.findAllById(serviceIds);
    int minutes = services.stream().mapToInt(ServiceOption::getDurationMinutes).sum();
    int price = services.stream().mapToInt(ServiceOption::getPriceAmd).sum();
    return new Quote(minutes, price);
  }

  public List<Slot> findFreeSlots(Long barberId, LocalDate date, Set<Long> serviceIds) {
    if (!isValidServiceSelection(serviceIds)) {
      throw new IllegalArgumentException("SERVICE_CONFLICT");
    }
    Quote quote = quote(serviceIds);
    if (quote.totalDurationMinutes() <= 0) return List.of();

    LocalDateTime from = date.atStartOfDay();
    LocalDateTime to = date.plusDays(1).atStartOfDay();

    List<Availability> avail = availabilityRepository.findOverlappingRanges(barberId, from, to);
    List<Booking> bookings = bookingRepository.findOverlappingBookings(barberId, from, to);
    List<BusyTime> busy = busyTimeRepository.findOverlappingBusyTimes(barberId, from, to);

    List<Range> ranges = avail.isEmpty()
        ? List.of(new Range(date.atTime(businessHoursService.open()), date.atTime(businessHoursService.close())))
        : avail.stream().map(a -> new Range(a.getStartAt(), a.getEndAt())).toList();

    Duration dur = Duration.ofMinutes(quote.totalDurationMinutes());
    List<Slot> out = new ArrayList<>();

    for (Range a : ranges) {
      LocalDateTime cursor = roundUpToStep(a.startAt(), SLOT_STEP);
      while (!cursor.plus(dur).isAfter(a.endAt())) {
        LocalDateTime end = cursor.plus(dur);
        if (!overlapsAny(cursor, end, bookings) && !overlapsAnyBusy(cursor, end, busy)) {
          out.add(new Slot(cursor, end));
        }
        cursor = cursor.plus(SLOT_STEP);
      }
    }
    return out;
  }

  @Transactional
  public Booking createBooking(Long barberId, LocalDate date, LocalTime startTime,
      Set<Long> serviceIds, String customerName, String customerPhone) {

    if (!isValidServiceSelection(serviceIds)) {
      throw new IllegalArgumentException("SERVICE_CONFLICT");
    }
    Barber barber = barberRepository.findById(barberId)
        .orElseThrow(() -> new IllegalArgumentException("Barber not found"));

    Set<ServiceOption> services = new HashSet<>(serviceOptionRepository.findAllById(serviceIds));
    Quote quote = quote(serviceIds);
    if (quote.totalDurationMinutes() <= 0) throw new IllegalArgumentException("No services selected");

    LocalDateTime startAt = date.atTime(startTime);
    LocalDateTime endAt = startAt.plusMinutes(quote.totalDurationMinutes());

    // Must be inside availability
    LocalDateTime dayFrom = date.atStartOfDay();
    LocalDateTime dayTo = date.plusDays(1).atStartOfDay();
    List<Availability> avail = availabilityRepository.findOverlappingRanges(barberId, dayFrom, dayTo);
    List<Range> ranges = avail.isEmpty()
        ? List.of(new Range(date.atTime(businessHoursService.open()), date.atTime(businessHoursService.close())))
        : avail.stream().map(a -> new Range(a.getStartAt(), a.getEndAt())).toList();

    boolean inside = ranges.stream().anyMatch(a -> !startAt.isBefore(a.startAt()) && !endAt.isAfter(a.endAt()));
    if (!inside) throw new IllegalStateException("Selected time not available");

    // Prevent double booking (overlap)
    List<Booking> overlapping = bookingRepository.findOverlappingBookings(barberId, startAt, endAt);
    if (!overlapping.isEmpty()) throw new IllegalStateException("Already booked");

    List<BusyTime> overlappingBusy = busyTimeRepository.findOverlappingBusyTimes(barberId, startAt, endAt);
    if (!overlappingBusy.isEmpty()) throw new IllegalStateException("Already booked");

    Booking booking = new Booking(barber, startAt, endAt, customerName, customerPhone, services);
    Booking saved = bookingRepository.save(booking);
    mailService.sendBookingToBarber(saved);
    return saved;
  }

  @Transactional
  public BusyTime addBusyTime(Long barberId, LocalDate date, LocalTime start, LocalTime end, String reason) {
    if (!end.isAfter(start)) throw new IllegalArgumentException("End must be after start");
    Barber barber = barberRepository.findById(barberId)
        .orElseThrow(() -> new IllegalArgumentException("Barber not found"));
    BusyTime bt = new BusyTime(barber, date.atTime(start), date.atTime(end), reason);
    return busyTimeRepository.save(bt);
  }

  @Transactional
  public void deleteBusyTime(Long busyTimeId) {
    busyTimeRepository.deleteById(busyTimeId);
  }

  @Transactional
  public Availability addAvailability(Long barberId, LocalDate date, LocalTime start, LocalTime end) {
    if (!end.isAfter(start)) throw new IllegalArgumentException("End must be after start");
    Barber barber = barberRepository.findById(barberId)
        .orElseThrow(() -> new IllegalArgumentException("Barber not found"));
    Availability a = new Availability(barber, date.atTime(start), date.atTime(end));
    return availabilityRepository.save(a);
  }

  @Transactional
  public void deleteAvailability(Long availabilityId) {
    availabilityRepository.deleteById(availabilityId);
  }

  private static boolean overlapsAny(LocalDateTime start, LocalDateTime end, List<Booking> bookings) {
    for (Booking b : bookings) {
      if (end.isAfter(b.getStartAt()) && start.isBefore(b.getEndAt())) return true;
    }
    return false;
  }

  private static boolean overlapsAnyBusy(LocalDateTime start, LocalDateTime end, List<BusyTime> busy) {
    for (BusyTime b : busy) {
      if (end.isAfter(b.getStartAt()) && start.isBefore(b.getEndAt())) return true;
    }
    return false;
  }

  private static LocalDateTime roundUpToStep(LocalDateTime t, Duration step) {
    long minutes = t.getMinute();
    long stepMin = step.toMinutes();
    long rounded = ((minutes + stepMin - 1) / stepMin) * stepMin;
    if (rounded == minutes) return t.withSecond(0).withNano(0);
    int add = (int) (rounded - minutes);
    return t.plusMinutes(add).withSecond(0).withNano(0);
  }
}

