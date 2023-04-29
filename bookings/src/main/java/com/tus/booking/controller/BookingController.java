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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

  @Autowired
  private BookingService bookingService;

  @GetMapping
  public ResponseEntity<List<Booking>> findAll() {
    List<Booking> bookings = bookingService.findAll();
    return ResponseEntity.ok(bookings);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Booking> findById(@PathVariable Long id) {
    Optional<Booking> bookingOptional = bookingService.findById(id);
    return bookingOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/delay/{id}")
  public ResponseEntity<Booking> findByWithDelay(@PathVariable Long id,@RequestParam(defaultValue = "0") int delayMillis) {
    Optional<Booking> bookingOptional = null;
    try {
      bookingOptional = bookingService.findByIdWithDelay(id, delayMillis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return bookingOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/car")
  public ResponseEntity<?> bookCar(@RequestParam Long carRentalId,
      @RequestParam Long userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    // Check if the car is available
    boolean isCarAvailable = bookingService.isCarAvailable(carRentalId);
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
    BigDecimal totalPrice = bookingService.getTotalLocationPrice(carRentalId, startDate, endDate);
    booking.setTotalPrice(totalPrice);
    Booking savedBooking = bookingService.save(booking);

    // Mark the car as unavailable
    bookingService.setCarUnavailable(carRentalId);
    return ResponseEntity.ok(savedBooking);
  }

  @PutMapping("/{bookingId}/cancel")
  public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
    Booking booking = bookingService.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found"));

    if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
      return ResponseEntity.badRequest().body(new ErrorResponse("Only bookings with 'CONFIRMED' status can be cancelled"));
    }

    booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
    booking.setEndDate(LocalDateTime.now());
    bookingService.save(booking);

    // Set the car as available
    Long carRentalId = booking.getCarRentalId();
    bookingService.setCarAvailable(carRentalId);

    return ResponseEntity.ok().body("Booking canceled");
  }

  @PutMapping("/{id}")
  public ResponseEntity<Booking> update(@PathVariable Long id, @RequestBody Booking booking) {
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
    Optional<Booking> bookingOptional = bookingService.findById(id);
    if (bookingOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    bookingService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("car/availability/{carRentalId}")
  public ResponseEntity<Boolean> checkCarAvailability(@PathVariable Long carRentalId) {
    boolean isAvailable = bookingService.isCarAvailable(carRentalId);
    return ResponseEntity.ok(isAvailable);
  }

}
