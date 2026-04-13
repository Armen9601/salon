package com.gevorgyan.salon.web;

import com.gevorgyan.salon.domain.ServiceOption;
import com.gevorgyan.salon.repo.ServiceOptionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
public class AdminServiceController {

  private final ServiceOptionRepository serviceOptionRepository;

  public AdminServiceController(ServiceOptionRepository serviceOptionRepository) {
    this.serviceOptionRepository = serviceOptionRepository;
  }

  public record ServiceForm(
      @NotBlank @Size(max = 64) String code,
      @NotBlank @Size(max = 255) String nameEn,
      @NotBlank @Size(max = 255) String nameRu,
      @NotBlank @Size(max = 255) String nameHy,
      @Min(5) int durationMinutes,
      @Min(0) int priceAmd
  ) {}

  @GetMapping("/admin/services")
  public String list(Model model) {
    model.addAttribute("services", serviceOptionRepository.findAll());
    return "admin/services";
  }

  @GetMapping("/admin/services/new")
  public String createForm(Model model) {
    model.addAttribute("form", new ServiceForm("", "", "", "", 30, 0));
    return "admin/service-form";
  }

  @PostMapping("/admin/services/new")
  public String create(@ModelAttribute("form") @Valid ServiceForm form, BindingResult binding) {
    if (binding.hasErrors()) return "admin/service-form";
    ServiceOption s = new ServiceOption(
        form.code().trim().toUpperCase(),
        form.nameEn().trim(),
        form.nameRu().trim(),
        form.nameHy().trim(),
        form.durationMinutes(),
        form.priceAmd()
    );
    serviceOptionRepository.save(s);
    return "redirect:/admin/services";
  }

  @GetMapping("/admin/services/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    ServiceOption s = serviceOptionRepository.findById(id).orElseThrow();
    model.addAttribute("serviceId", id);
    model.addAttribute("form", new ServiceForm(
        s.getCode(),
        s.getNameEn(),
        s.getNameRu(),
        s.getNameHy(),
        s.getDurationMinutes(),
        s.getPriceAmd()
    ));
    return "admin/service-form";
  }

  @PostMapping("/admin/services/{id}/edit")
  public String edit(@PathVariable Long id, @ModelAttribute("form") @Valid ServiceForm form,
      BindingResult binding, Model model) {
    if (binding.hasErrors()) {
      model.addAttribute("serviceId", id);
      return "admin/service-form";
    }
    ServiceOption s = serviceOptionRepository.findById(id).orElseThrow();
    s.setCode(form.code().trim().toUpperCase());
    s.setNameEn(form.nameEn().trim());
    s.setNameRu(form.nameRu().trim());
    s.setNameHy(form.nameHy().trim());
    s.setDurationMinutes(form.durationMinutes());
    s.setPriceAmd(form.priceAmd());
    serviceOptionRepository.save(s);
    return "redirect:/admin/services";
  }

  @PostMapping("/admin/services/delete")
  public String delete(@RequestParam Long id) {
    serviceOptionRepository.deleteById(id);
    return "redirect:/admin/services";
  }
}

