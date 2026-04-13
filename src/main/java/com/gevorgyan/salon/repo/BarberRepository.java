package com.gevorgyan.salon.repo;

import com.gevorgyan.salon.domain.Barber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarberRepository extends JpaRepository<Barber, Long> {}

