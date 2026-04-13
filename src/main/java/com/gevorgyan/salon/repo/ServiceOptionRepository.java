package com.gevorgyan.salon.repo;

import com.gevorgyan.salon.domain.ServiceOption;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceOptionRepository extends JpaRepository<ServiceOption, Long> {
  Optional<ServiceOption> findByCode(String code);
}

