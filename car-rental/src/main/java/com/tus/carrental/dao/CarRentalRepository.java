package com.tus.carrental.dao;

import com.tus.carrental.model.CarRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRentalRepository extends JpaRepository<CarRental, Long> {
  // Custom query methods can be defined here, if needed
}
