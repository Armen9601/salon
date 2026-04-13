package com.gevorgyan.salon.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class ServiceOption {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code; // identifier (admin can create new codes)

  @Column(nullable = false)
  private String nameEn;

  @Column(nullable = false)
  private String nameRu;

  @Column(nullable = false)
  private String nameHy;

  @Column(nullable = false)
  private int durationMinutes;

  @Column(nullable = false)
  private int priceAmd;

  protected ServiceOption() {}

  public ServiceOption(String code, String nameEn, String nameRu, String nameHy, int durationMinutes, int priceAmd) {
    this.code = code;
    this.nameEn = nameEn;
    this.nameRu = nameRu;
    this.nameHy = nameHy;
    this.durationMinutes = durationMinutes;
    this.priceAmd = priceAmd;
  }

  public Long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getNameRu() {
    return nameRu;
  }

  public void setNameRu(String nameRu) {
    this.nameRu = nameRu;
  }

  public String getNameHy() {
    return nameHy;
  }

  public void setNameHy(String nameHy) {
    this.nameHy = nameHy;
  }

  public int getDurationMinutes() {
    return durationMinutes;
  }

  public void setDurationMinutes(int durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  public int getPriceAmd() {
    return priceAmd;
  }

  public void setPriceAmd(int priceAmd) {
    this.priceAmd = priceAmd;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ServiceOption that)) return false;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

