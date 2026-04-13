package com.gevorgyan.salon.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(indexes = {
    @Index(name = "idx_booking_barber_start", columnList = "barber_id,startAt"),
})
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Barber barber;

  @Column(nullable = false)
  private LocalDateTime startAt;

  @Column(nullable = false)
  private LocalDateTime endAt;

  @Column(nullable = false)
  private String customerName;

  @Column(nullable = false)
  private String customerPhone;

  @ManyToMany
  @JoinTable(
      name = "booking_service",
      joinColumns = @JoinColumn(name = "booking_id"),
      inverseJoinColumns = @JoinColumn(name = "service_option_id"))
  private Set<ServiceOption> services = new HashSet<>();

  protected Booking() {}

  public Booking(Barber barber, LocalDateTime startAt, LocalDateTime endAt, String customerName,
      String customerPhone, Set<ServiceOption> services) {
    this.barber = barber;
    this.startAt = startAt;
    this.endAt = endAt;
    this.customerName = customerName;
    this.customerPhone = customerPhone;
    if (services != null) this.services = services;
  }

  public Long getId() {
    return id;
  }

  public Barber getBarber() {
    return barber;
  }

  public void setBarber(Barber barber) {
    this.barber = barber;
  }

  public LocalDateTime getStartAt() {
    return startAt;
  }

  public void setStartAt(LocalDateTime startAt) {
    this.startAt = startAt;
  }

  public LocalDateTime getEndAt() {
    return endAt;
  }

  public void setEndAt(LocalDateTime endAt) {
    this.endAt = endAt;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerPhone() {
    return customerPhone;
  }

  public void setCustomerPhone(String customerPhone) {
    this.customerPhone = customerPhone;
  }

  public Set<ServiceOption> getServices() {
    return services;
  }

  public void setServices(Set<ServiceOption> services) {
    this.services = services;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Booking booking)) return false;
    return id != null && Objects.equals(id, booking.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

