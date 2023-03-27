package com.tus.hotel.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hotels")
public class Hotel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  @Setter
  private Long id;

  @Column(name = "hotel_name", nullable = false)
  @Getter
  @Setter
  private String hotelName;

  @Column(name = "location", nullable = false)
  @Getter
  @Setter
  private String location;

  @Column(name = "rating")
  @Getter
  @Setter
  private Double rating;

  @Column(name = "room_type", nullable = false)
  @Getter
  @Setter
  private String roomType;

  @Column(name = "price", nullable = false)
  @Getter
  @Setter
  private BigDecimal price;
}
