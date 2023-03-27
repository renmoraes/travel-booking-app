package com.tus.carrental.controller;

import com.tus.carrental.model.CarRental;
import com.tus.carrental.service.CarRentalService;
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
@RequestMapping("/api/carrentals")
public class CarRentalController {

  @Autowired
  private CarRentalService carRentalService;

  @GetMapping
  public ResponseEntity<List<CarRental>> findAll() {
    List<CarRental> carRentals = carRentalService.findAll();
    return ResponseEntity.ok(carRentals);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CarRental> findById(@PathVariable Long id) {
    Optional<CarRental> carRentalOptional = carRentalService.findById(id);
    return carRentalOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<CarRental> save(@RequestBody CarRental carRental) {
    CarRental savedCarRental = carRentalService.save(carRental);
    return ResponseEntity.ok(savedCarRental);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CarRental> update(@PathVariable Long id, @RequestBody CarRental carRental) {
    Optional<CarRental> carRentalOptional = carRentalService.findById(id);
    if (!carRentalOptional.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    carRental.setId(id);
    CarRental updatedCarRental = carRentalService.save(carRental);
    return ResponseEntity.ok(updatedCarRental);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    Optional<CarRental> carRentalOptional = carRentalService.findById(id);
    if (!carRentalOptional.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    carRentalService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // Add any custom API endpoints here, if needed

}
