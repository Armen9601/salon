package com.gevorgyan.salon.service;

import com.gevorgyan.salon.domain.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
  private static final Logger log = LoggerFactory.getLogger(MailService.class);

  private final JavaMailSender mailSender;
  private final String from;

  public MailService(JavaMailSender mailSender,
      @Value("${app.mail.from:no-reply@gevorgyans-salon.local}") String from) {
    this.mailSender = mailSender;
    this.from = "gevorgyanarman525@gmail.com";
  }

  public void sendBookingToBarber(Booking booking) {
    String to = booking.getBarber().getEmail();
    if (to == null || to.isBlank()) return;

    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(from);
    msg.setTo(to);
    msg.setSubject("New booking: " + booking.getCustomerName());
    msg.setText("""
        New booking received.

        Barber: %s
        When: %s - %s
        Customer: %s
        Phone: %s
        """.formatted(
        booking.getBarber().getFullName(),
        booking.getStartAt(),
        booking.getEndAt(),
        booking.getCustomerName(),
        booking.getCustomerPhone()
    ));

    try {
      mailSender.send(msg);
    } catch (Exception e) {
      log.warn("Failed to send booking email to barber: {}", e.getMessage());
    }
  }
}

