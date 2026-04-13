package com.gevorgyan.salon.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(indexes = {
    @Index(name = "idx_busy_barber_start", columnList = "barber_id,startAt"),
})
public class BusyTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Barber barber;

  @Column(nullable = false)
  private LocalDateTime startAt;

  @Column(nullable = false)
  private LocalDateTime endAt;

  @Column(length = 255)
  private String reason;

  protected BusyTime() {}

  public BusyTime(Barber barber, LocalDateTime startAt, LocalDateTime endAt, String reason) {
    this.barber = barber;
    this.startAt = startAt;
    this.endAt = endAt;
    this.reason = reason;
  }

  public Long getId() {
    return id;
  }

  public Barber getBarber() {
    return barber;
  }

  public LocalDateTime getStartAt() {
    return startAt;
  }

  public LocalDateTime getEndAt() {
    return endAt;
  }

  public String getReason() {
    return reason;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BusyTime busyTime)) return false;
    return id != null && Objects.equals(id, busyTime.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

