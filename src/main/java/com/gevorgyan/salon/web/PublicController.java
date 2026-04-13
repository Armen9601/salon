package com.gevorgyan.salon.web;

import com.gevorgyan.salon.repo.BarberRepository;
import com.gevorgyan.salon.repo.ServiceOptionRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PublicController {

  private final BarberRepository barberRepository;
  private final ServiceOptionRepository serviceOptionRepository;

  public PublicController(BarberRepository barberRepository,
      ServiceOptionRepository serviceOptionRepository) {
    this.barberRepository = barberRepository;
    this.serviceOptionRepository = serviceOptionRepository;
  }

  @GetMapping("/")
  public String home() {
    return "public/home";
  }

  @GetMapping("/barbers")
  public String barbers(Model model) {
    model.addAttribute("barbers", barberRepository.findAll());
    return "public/barbers";
  }

  @GetMapping("/barbers/{id}")
  public String barber(@PathVariable Long id,
      @RequestParam(value = "date", required = false) LocalDate date,
      @RequestParam(value = "services", required = false) String services,
      Model model) {
    model.addAttribute("barber", barberRepository.findById(id).orElseThrow());
    model.addAttribute("services", serviceOptionRepository.findAll());
    model.addAttribute("date", date != null ? date : LocalDate.now());
    model.addAttribute("servicesRequired", "required".equals(services));
    model.addAttribute("servicesConflict", "conflict".equals(services));
    return "public/barber";
  }
}

