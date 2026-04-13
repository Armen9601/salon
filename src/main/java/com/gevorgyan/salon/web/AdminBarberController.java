package com.gevorgyan.salon.web;

import com.gevorgyan.salon.domain.Barber;
import com.gevorgyan.salon.repo.BarberRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminBarberController {

  private final BarberRepository barberRepository;

  public AdminBarberController(BarberRepository barberRepository) {
    this.barberRepository = barberRepository;
  }

  public record BarberForm(
      @NotBlank @Size(max = 255) String fullName,
      @Size(max = 2000) String bio,
      @Size(max = 2000) String photoUrl,
      @Size(max = 255) String email
  ) {}

  @GetMapping("/admin/barbers")
  public String list(Model model) {
    model.addAttribute("barbers", barberRepository.findAll());
    return "admin/barbers";
  }

  @GetMapping("/admin/barbers/new")
  public String createForm(Model model) {
    model.addAttribute("form", new BarberForm("", "", "", ""));
    return "admin/barber-form";
  }

  @PostMapping("/admin/barbers/new")
  public String create(@ModelAttribute("form") @Valid BarberForm form, BindingResult binding) {
    if (binding.hasErrors()) return "admin/barber-form";
    Barber b = new Barber(form.fullName(), form.bio(), form.photoUrl());
    b.setEmail(form.email());
    barberRepository.save(b);
    return "redirect:/admin/barbers";
  }

  @GetMapping("/admin/barbers/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    Barber b = barberRepository.findById(id).orElseThrow();
    model.addAttribute("barberId", id);
    model.addAttribute("form", new BarberForm(
        b.getFullName(),
        b.getBio() == null ? "" : b.getBio(),
        b.getPhotoUrl() == null ? "" : b.getPhotoUrl(),
        b.getEmail() == null ? "" : b.getEmail()
    ));
    return "admin/barber-form";
  }

  @PostMapping("/admin/barbers/{id}/edit")
  public String edit(@PathVariable Long id, @ModelAttribute("form") @Valid BarberForm form,
      BindingResult binding, Model model) {
    if (binding.hasErrors()) {
      model.addAttribute("barberId", id);
      return "admin/barber-form";
    }
    Barber b = barberRepository.findById(id).orElseThrow();
    b.setFullName(form.fullName());
    b.setBio(form.bio());
    b.setPhotoUrl(form.photoUrl());
    b.setEmail(form.email());
    barberRepository.save(b);
    return "redirect:/admin/barbers";
  }

  @PostMapping("/admin/barbers/delete")
  public String delete(@RequestParam Long id) {
    barberRepository.deleteById(id);
    return "redirect:/admin/barbers";
  }
}

