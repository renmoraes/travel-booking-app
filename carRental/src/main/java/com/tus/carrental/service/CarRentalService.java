package com.tus.carrental.service;

import com.tus.carrental.dao.CarRentalRepository;
import com.tus.carrental.model.CarRental;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarRentalService {
  @Autowired
  private CarRentalRepository carRentalRepository;

  public List<CarRental> findAll() {
    return carRentalRepository.findAll();
  }

  public Optional<CarRental> findById(Long id) {
    return carRentalRepository.findById(id);
  }

  public CarRental save(CarRental carRental) {
    return carRentalRepository.save(carRental);
  }

  public void deleteById(Long id) {
    carRentalRepository.deleteById(id);
  }
}
