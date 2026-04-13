package com.gevorgyan.salon.web;

import com.gevorgyan.salon.repo.AvailabilityRepository;
import com.gevorgyan.salon.repo.BarberRepository;
import com.gevorgyan.salon.repo.BookingRepository;
import com.gevorgyan.salon.repo.BusyTimeRepository;
import com.gevorgyan.salon.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

  private final BarberRepository barberRepository;
  private final BookingRepository bookingRepository;
  private final AvailabilityRepository availabilityRepository;
  private final BusyTimeRepository busyTimeRepository;
  private final BookingService bookingService;

  public AdminController(BarberRepository barberRepository,
      BookingRepository bookingRepository,
      AvailabilityRepository availabilityRepository,
      BusyTimeRepository busyTimeRepository,
      BookingService bookingService) {
    this.barberRepository = barberRepository;
    this.bookingRepository = bookingRepository;
    this.availabilityRepository = availabilityRepository;
    this.busyTimeRepository = busyTimeRepository;
    this.bookingService = bookingService;
  }

  public record AvailabilityRequest(
      @NotNull Long barberId,
      @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
      @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end
  ) {}

  public record BusyTimeRequest(
      @NotNull Long barberId,
      @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
      @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end,
      String reason
  ) {}

  @GetMapping("/admin/login")
  public String login() {
    return "admin/login";
  }

  @GetMapping("/admin")
  public String dashboard(Model model) {
    model.addAttribute("barbers", barberRepository.findAll());
    model.addAttribute("bookings", bookingRepository.findAllOrderByStartDesc());
    model.addAttribute("availability", availabilityRepository.findAll());
    model.addAttribute("busyTimes", busyTimeRepository.findAll());
    model.addAttribute("availReq", new AvailabilityRequest(null, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(18, 0)));
    model.addAttribute("busyReq", new BusyTimeRequest(null, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), ""));
    return "admin/dashboard";
  }

  @PostMapping("/admin/availability")
  public String addAvailability(@ModelAttribute("availReq") @Valid AvailabilityRequest req,
      BindingResult binding,
      Model model) {
    if (binding.hasErrors()) {
      return dashboard(model);
    }
    bookingService.addAvailability(req.barberId(), req.date(), req.start(), req.end());
    return "redirect:/admin";
  }

  @PostMapping("/admin/availability/delete")
  public String deleteAvailability(@RequestParam Long id) {
    bookingService.deleteAvailability(id);
    return "redirect:/admin";
  }

  @PostMapping("/admin/busy")
  public String addBusy(@ModelAttribute("busyReq") @Valid BusyTimeRequest req,
      BindingResult binding,
      Model model) {
    if (binding.hasErrors()) {
      return dashboard(model);
    }
    bookingService.addBusyTime(req.barberId(), req.date(), req.start(), req.end(), req.reason());
    return "redirect:/admin";
  }

  @PostMapping("/admin/busy/delete")
  public String deleteBusy(@RequestParam Long id) {
    bookingService.deleteBusyTime(id);
    return "redirect:/admin";
  }
}

