package com.tus.booking.controller;

import com.tus.booking.model.Booking;
import com.tus.booking.service.BookingService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
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

  @PostMapping
  public ResponseEntity<Booking> save(@RequestBody Booking booking) {
    Booking savedBooking = bookingService.save(booking);
    return ResponseEntity.ok(savedBooking);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Booking> update(@PathVariable Long id, @RequestBody Booking booking) {
    Optional<Booking> bookingOptional = bookingService.findById(id);
    if (!bookingOptional.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    booking.setId(id);
    Booking updatedBooking = bookingService.save(booking);
    return ResponseEntity.ok(updatedBooking);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    Optional<Booking> bookingOptional = bookingService.findById(id);
    if (!bookingOptional.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    bookingService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // Add any custom API endpoints here, if needed

}
