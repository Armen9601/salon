package com.gevorgyan.salon.config;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeZoneConfig {

  private final String timeZoneId;

  public TimeZoneConfig(@Value("${app.timezone:Asia/Yerevan}") String timeZoneId) {
    this.timeZoneId = timeZoneId;
  }

  @PostConstruct
  void init() {
    TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId));
  }
}

