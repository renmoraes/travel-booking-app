package com.tus.booking.dao;

import com.tus.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  // Custom query methods can be defined here, if needed
}