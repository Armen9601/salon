package com.gevorgyan.salon.web;

import com.gevorgyan.salon.repo.BarberRepository;
import com.gevorgyan.salon.repo.ServiceOptionRepository;
import com.gevorgyan.salon.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookingController {

  private final BookingService bookingService;
  private final BarberRepository barberRepository;
  private final ServiceOptionRepository serviceOptionRepository;

  public BookingController(BookingService bookingService,
      BarberRepository barberRepository,
      ServiceOptionRepository serviceOptionRepository) {
    this.bookingService = bookingService;
    this.barberRepository = barberRepository;
    this.serviceOptionRepository = serviceOptionRepository;
  }

  public record BookingRequest(
      @NotNull Long barberId,
      @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
      @NotEmpty Set<Long> serviceIds,
      @NotBlank
      @Size(min = 2, max = 60)
      @Pattern(regexp = "^[\\p{L} .'-]+$", message = "{validation.nameLettersOnly}")
      String customerName,
      @NotBlank
      @Pattern(regexp = "^\\d{5,15}$", message = "{validation.phoneDigitsOnly}")
      String customerPhone
  ) {}

  @GetMapping("/barbers/{barberId}/slots")
  public String slots(@PathVariable Long barberId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(name = "serviceIds", required = false) Set<Long> serviceIds,
      Model model) {

    if (serviceIds == null || serviceIds.isEmpty()) {
      return "redirect:/barbers/" + barberId + "?date=" + date + "&services=required";
    }
    if (!bookingService.isValidServiceSelection(serviceIds)) {
      return "redirect:/barbers/" + barberId + "?date=" + date + "&services=conflict";
    }

    List<BookingService.Slot> slots = bookingService.findFreeSlots(barberId, date, serviceIds);
    model.addAttribute("barber", barberRepository.findById(barberId).orElseThrow());
    model.addAttribute("date", date);
    model.addAttribute("services", serviceOptionRepository.findAllById(serviceIds));
    model.addAttribute("quote", bookingService.quote(serviceIds));
    model.addAttribute("slots", slots);
    model.addAttribute("serviceIds", serviceIds);
    return "public/slots";
  }

  @GetMapping("/book")
  public String bookForm(@RequestParam Long barberId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
      @RequestParam(name = "serviceIds") Set<Long> serviceIds,
      Model model) {

    model.addAttribute("barber", barberRepository.findById(barberId).orElseThrow());
    model.addAttribute("services", serviceOptionRepository.findAllById(serviceIds));
    model.addAttribute("quote", bookingService.quote(serviceIds));
    model.addAttribute("req", new BookingRequest(barberId, date, time, serviceIds, "", ""));
    return "public/book";
  }

  @PostMapping("/book")
  public String bookSubmit(@ModelAttribute("req") @Valid BookingRequest req,
      BindingResult binding,
      Model model) {
    if (binding.hasErrors()) {
      model.addAttribute("barber", barberRepository.findById(req.barberId()).orElseThrow());
      model.addAttribute("services", serviceOptionRepository.findAllById(req.serviceIds()));
      model.addAttribute("quote", bookingService.quote(req.serviceIds()));
      return "public/book";
    }

    try {
      var booking = bookingService.createBooking(
          req.barberId(), req.date(), req.time(), req.serviceIds(), req.customerName(), req.customerPhone());
      return "redirect:/book/success?id=" + booking.getId();
    } catch (RuntimeException ex) {
      if ("SERVICE_CONFLICT".equals(ex.getMessage())) {
        model.addAttribute("error", "SERVICE_CONFLICT");
      } else {
        model.addAttribute("error", ex.getMessage());
      }
      model.addAttribute("barber", barberRepository.findById(req.barberId()).orElseThrow());
      model.addAttribute("services", serviceOptionRepository.findAllById(req.serviceIds()));
      model.addAttribute("quote", bookingService.quote(req.serviceIds()));
      return "public/book";
    }
  }

  @GetMapping("/book/success")
  public String success(@RequestParam Long id, Model model) {
    model.addAttribute("bookingId", id);
    return "public/success";
  }
}

