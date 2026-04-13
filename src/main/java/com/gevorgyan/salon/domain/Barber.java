package com.gevorgyan.salon.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Barber {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fullName;

  @Column(length = 2000)
  private String bio;

  private String photoUrl;

  private String email;

  protected Barber() {}

  public Barber(String fullName, String bio, String photoUrl) {
    this.fullName = fullName;
    this.bio = bio;
    this.photoUrl = photoUrl;
  }

  public Long getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Barber barber)) return false;
    return id != null && Objects.equals(id, barber.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}

