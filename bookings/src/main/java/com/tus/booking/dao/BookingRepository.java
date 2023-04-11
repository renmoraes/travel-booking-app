package com.tus.booking.dao;

import com.tus.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Query("SELECT b FROM Booking b WHERE b.carRentalId = :carRentalId AND ((b.startDate BETWEEN :startDate AND :endDate) OR (b.endDate BETWEEN :startDate AND :endDate) OR (b.startDate < :startDate AND b.endDate > :endDate))")
  List<Booking> findOverlappingBookings(@Param("carRentalId") Long carRentalId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}