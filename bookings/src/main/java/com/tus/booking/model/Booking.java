package com.tus.booking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="bookings")
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  @Setter
  private Long id;

  @Column(name = "user_id", nullable = false)
  @Getter
  @Setter
  private Long userId;

  @Column(name = "flight_id")
  @Getter
  @Setter
  private Long flightId;

  @Column(name = "hotel_id")
  @Getter
  @Setter
  private Long hotelId;

  @Column(name = "car_rental_id")
  @Getter
  @Setter
  private Long carRentalId;

  @Enumerated(EnumType.STRING)
  @Column(name = "booking_status", nullable = false)
  @Getter
  @Setter
  private BookingStatus bookingStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false)
  @Getter
  @Setter
  private PaymentStatus paymentStatus;

  @Column(name = "start_date", nullable = false)
  @Getter
  @Setter
  private LocalDateTime startDate;

  @Column(name = "end_date", nullable = false)
  @Getter
  @Setter
  private LocalDateTime endDate;

  @Column(name = "booking_date", nullable = false)
  @Getter
  @Setter
  private LocalDateTime bookingDate;

  @Column(name = "total_price", nullable = false)
  @Getter
  @Setter
  private BigDecimal totalPrice;



  public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
  }

  public enum PaymentStatus {
    PENDING,
    PAID,
    REFUNDED
  }

}
