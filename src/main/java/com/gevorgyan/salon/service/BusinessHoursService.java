package com.gevorgyan.salon.service;

import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BusinessHoursService {

  private final LocalTime open;
  private final LocalTime close;

  public BusinessHoursService(
      @Value("${app.hours.open:10:00}") LocalTime open,
      @Value("${app.hours.close:20:00}") LocalTime close) {
    this.open = open;
    this.close = close;
  }

  public LocalTime open() {
    return open;
  }

  public LocalTime close() {
    return close;
  }
}

