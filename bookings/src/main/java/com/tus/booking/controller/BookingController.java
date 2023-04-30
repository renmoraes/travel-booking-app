package com.tus.booking.controller;

import com.tus.booking.error.ErrorResponse;
import com.tus.booking.model.Booking;
import com.tus.booking.model.Booking.BookingStatus;
import com.tus.booking.service.BookingService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

  @Autowired
  private BookingService bookingService;

  private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

  @GetMapping
  public ResponseEntity<List<Booking>> findAll() {
    LOGGER.info("Received a request to retrieve all bookings");
    List<Booking> bookings = bookingService.findAll();
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Booking> findById(@PathVariable Long id) {
    LOGGER.info("Received a request to retrieve booking with id {}", id);
    Optional<Booking> bookingOptional = bookingService.findById(id);
    return bookingOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/delay/{id}")
  public ResponseEntity<Booking> findByWithDelay(@PathVariable Long id,@RequestParam(defaultValue = "0") int delayMillis) {
    LOGGER.info("Received a request to retrieve booking with id {} with delay {}", id, delayMillis);
    Optional<Booking> bookingOptional = null;
    try {
      bookingOptional = bookingService.findByIdWithDelay(id, delayMillis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return bookingOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/car")
  public ResponseEntity<?> bookCar(@RequestHeader(value = "Authorization") String authorizationHeader,
      @RequestParam Long carRentalId,
      @RequestParam Long userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    LOGGER.info("Received a request to book car with carRentalId {}, userId {}, startDate {}, endDate {}", carRentalId, userId, startDate, endDate);
    // Check if the car is available
    boolean isCarAvailable = bookingService.isCarAvailable(carRentalId, authorizationHeader);
    if (!isCarAvailable) {
      return ResponseEntity.badRequest().body(new ErrorResponse("The car is not available for the specified dates"));
    }

    // Create the booking
    Booking booking = new Booking();
    booking.setUserId(userId);
    booking.setCarRentalId(carRentalId);
    booking.setBookingStatus(BookingStatus.CONFIRMED);
    booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
    booking.setBookingDate(LocalDateTime.now());
    booking.setStartDate(startDate);
    booking.setEndDate(endDate);
    BigDecimal totalPrice = bookingService.getTotalLocationPrice(carRentalId, startDate, endDate, authorizationHeader);
    booking.setTotalPrice(totalPrice);
    Booking savedBooking = bookingService.save(booking);

    // Mark the car as unavailable
    bookingService.setCarUnavailable(carRentalId, authorizationHeader);
    return ResponseEntity.ok(savedBooking);
  }

  @PutMapping("/{bookingId}/cancel")
  public ResponseEntity<?> cancelBooking(@RequestHeader(value = "Authorization") String authorizationHeader,
      @PathVariable Long bookingId) {
    Booking booking = bookingService.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found"));
    LOGGER.info("Cancel booking with id: {}", bookingId);

    if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
      return ResponseEntity.badRequest().body(new ErrorResponse("Only bookings with 'CONFIRMED' status can be cancelled"));
    }

    booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
    booking.setEndDate(LocalDateTime.now());
    bookingService.save(booking);

    // Set the car as available
    Long carRentalId = booking.getCarRentalId();
    bookingService.setCarAvailable(carRentalId, authorizationHeader);

    return ResponseEntity.ok().body("Booking canceled");
  }

  @PutMapping("/{id}")
  public ResponseEntity<Booking> update(@PathVariable Long id, @RequestBody Booking booking) {
    LOGGER.info("Update booking with id: {}", id);
    Optional<Booking> bookingOptional = bookingService.findById(id);
    if (bookingOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    booking.setId(id);
    Booking updatedBooking = bookingService.save(booking);
    return ResponseEntity.ok(updatedBooking);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    LOGGER.info("Delete booking with id: {}", id);
    Optional<Booking> bookingOptional = bookingService.findById(id);
    if (bookingOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    bookingService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("car/availability/{carRentalId}")
  public ResponseEntity<Boolean> checkCarAvailability(@RequestHeader(value = "Authorization") String authorizationHeader,
      @PathVariable Long carRentalId) {
    LOGGER.info("Check car availability for car rental id: {}", carRentalId);
    boolean isAvailable = bookingService.isCarAvailable(carRentalId, authorizationHeader);
    LOGGER.info("Car availability for car rental id: {} is {}", carRentalId, isAvailable);
    return ResponseEntity.ok(isAvailable);
  }

}
