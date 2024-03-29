package com.tus.carrental.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "car_rentals")
public class CarRental {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  @Setter
  private Long id;

  @Column(name = "rental_company", nullable = false)
  @Getter
  @Setter
  private String rentalCompany;

  @Column(name = "location", nullable = false)
  @Getter
  @Setter
  private String location;

  @Column(name = "car_type", nullable = false)
  @Getter
  @Setter
  private String carType;

  @Column(name = "price", nullable = false)
  @Setter
  private BigDecimal price;

  @Column(name = "available", nullable = false)
  @Getter
  @Setter
  private boolean available = true;

  public BigDecimal getPrice() {
    return price.setScale(2, RoundingMode.HALF_UP);
  }
}
