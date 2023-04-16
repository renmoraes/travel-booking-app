package com.tus.carrental.controller;

import com.tus.carrental.model.CarRental;
import com.tus.carrental.service.CarRentalService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/carrentals")
public class CarRentalController {

  @Autowired
  private CarRentalService carRentalService;

  @GetMapping
  public ResponseEntity<List<CarRental>> findAll() {
    List<CarRental> carRentals = carRentalService.findAll();
    if (carRentals.size()==0){
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(carRentals);
    }
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
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCarRental);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CarRental> update(@PathVariable Long id, @RequestBody CarRental carRental) {
    Optional<CarRental> carRentalOptional = carRentalService.findById(id);
    if (carRentalOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    carRental.setId(id);
    CarRental updatedCarRental = carRentalService.save(carRental);
    return ResponseEntity.ok(updatedCarRental);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    Optional<CarRental> carRentalOptional = carRentalService.findById(id);
    if (carRentalOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    carRentalService.deleteById(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/available")
  public ResponseEntity<List<CarRental>> findAvailableCarRentals(
      @RequestParam(required = false) String location,
      @RequestParam(required = false) String carType,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam String startDate,
      @RequestParam String endDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
    LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

    List<CarRental> availableCarRentals = carRentalService.findAvailableCarRentals(location, carType, minPrice, maxPrice, startDateTime, endDateTime);
    if (availableCarRentals.isEmpty()){
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(availableCarRentals);
  }

  @GetMapping("/{carRentalId}/totalcost")
  public ResponseEntity<BigDecimal> getTotalRentalCost(
      @PathVariable Long carRentalId,
      @RequestParam String startDate,
      @RequestParam String endDate,
      @RequestParam(required = false) BigDecimal additionalFees,
      @RequestParam(required = false) BigDecimal taxRate,
      @RequestParam(required = false) BigDecimal discountRate) {
    Optional<CarRental> carRentalOpt = carRentalService.findById(carRentalId);
    if (carRentalOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    CarRental carRental = carRentalOpt.get();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
    LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);

    BigDecimal totalCost = carRentalService.calculateTotalRentalCost(carRental, startDateTime, endDateTime, additionalFees, taxRate, discountRate);
    return ResponseEntity.ok(totalCost);
  }


}
