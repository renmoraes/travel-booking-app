package com.tus.booking.service;

import com.tus.booking.dao.BookingRepository;
import com.tus.booking.model.Booking;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

  @Autowired
  private BookingRepository bookingRepository;

  public List<Booking> findAll() {
    return bookingRepository.findAll();
  }

  public Optional<Booking> findById(Long id) {
    return bookingRepository.findById(id);
  }

  public Booking save(Booking booking) {
    return bookingRepository.save(booking);
  }

  public void deleteById(Long id) {
    bookingRepository.deleteById(id);
  }

  // Add any custom business logic methods here, if needed

}