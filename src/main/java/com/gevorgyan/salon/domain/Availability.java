package com.gevorgyan.salon.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(indexes = {
    @Index(name = "idx_avail_barber_start", columnList = "barber_id,startAt"),
})
public class Availability {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Barber barber;

  @Column(nullable = false)
  private LocalDateTime startAt;

  @Column(nullable = false)
  private LocalDateTime endAt;

  protected Availability() {}

  public Availability(Barber barber, LocalDateTime startAt, LocalDateTime endAt) {
    this.barber = barber;
    this.startAt = startAt;
    this.endAt = endAt;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Availability that)) return false;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

