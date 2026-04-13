package com.gevorgyan.salon.config;

import com.gevorgyan.salon.domain.Barber;
import com.gevorgyan.salon.domain.ServiceOption;
import com.gevorgyan.salon.repo.BarberRepository;
import com.gevorgyan.salon.repo.ServiceOptionRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

  @Bean
  CommandLineRunner seed(BarberRepository barberRepository, ServiceOptionRepository serviceOptionRepository) {
    return args -> {
      if (barberRepository.count() == 0) {
        barberRepository.saveAll(List.of(
            barber("Gevorg Gevorgyan", "Senior barber • fades • classic style", "gevorg@example.com"),
            barber("Arman Hakobyan", "Beard specialist • modern cuts", "arman@example.com"),
            barber("Karen Mkrtchyan", "Fast & clean • appointments available", "karen@example.com")
        ));
      }

      if (serviceOptionRepository.count() == 0) {
        serviceOptionRepository.saveAll(List.of(
            new ServiceOption("HAIRCUT", "Haircut", "Стрижка", "Սանրվածք", 30, 5000),
            new ServiceOption("BEARD", "Beard trim", "Борода", "Մորուս", 20, 4000),
            new ServiceOption("HAIRCUT_BEARD", "Haircut + beard", "Стрижка + борода", "Սանրվածք + մորուս", 50, 8500)
        ));
      }
    };
  }

  private static Barber barber(String name, String bio, String email) {
    Barber b = new Barber(name, bio, null);
    b.setEmail(email);
    return b;
  }
}

