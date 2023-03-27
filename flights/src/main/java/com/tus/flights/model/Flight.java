package com.tus.flights.model;


import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="flights")
public class Flight {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  @Getter
  @Setter
  private Long id;

  @Column(name = "airline", nullable = false)
  @Getter
  @Setter
  private String airline;

  @Column(name = "origin", nullable = false)
  @Getter
  @Setter
  private String origin;

  @Column(name = "destination", nullable = false)
  @Getter
  @Setter
  private String destination;

  @Column(name = "departure_time", nullable = false)
  @Getter
  @Setter
  private LocalTime departureTime;

  @Column(name = "departure_date", nullable = false)
  @Getter
  @Setter
  private LocalDate departureDate;

  @Column(name = "arrival_time", nullable = false)
  @Getter
  @Setter
  private Time arrivalTime;

  @Column(name = "arrival_date", nullable = false)
  @Getter
  @Setter
  private LocalDate arrivalDate;

  @Column(name = "duration_minutes", nullable = false)
  @Getter
  @Setter
  private int durationMinutes;

  @Column(name = "price", nullable = false)
  @Getter
  @Setter
  private BigDecimal price;

}
