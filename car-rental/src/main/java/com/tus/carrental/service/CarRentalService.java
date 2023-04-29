package com.tus.carrental.service;

import com.tus.carrental.dao.CarRentalRepository;
import com.tus.carrental.model.CarRental;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarRentalService {
  @Autowired
  private CarRentalRepository carRentalRepository;

  private static final Logger logger = LoggerFactory.getLogger(CarRentalService.class);

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

  public void setCarAvailable(Long carRentalId, boolean availability) {
    CarRental carRental = carRentalRepository.findById(carRentalId).orElseThrow(() -> new RuntimeException("Car rental not found"));
    carRental.setAvailable(availability);
    carRentalRepository.save(carRental);
  }

  public boolean isCarAvailable(Long carRentalId) {
    CarRental carRental = carRentalRepository.findById(carRentalId).orElseThrow(() -> new RuntimeException("Car rental not found"));
    return  carRental.isAvailable();
  }

  public BigDecimal calculateRentalCost(CarRental carRental, LocalDateTime startDate, LocalDateTime endDate) {
    Duration rentalDuration = Duration.between(startDate, endDate);
    long rentalDays = rentalDuration.toDays();
    return carRental.getPrice().multiply(BigDecimal.valueOf(rentalDays));
  }

  public BigDecimal applyDiscount( BigDecimal rentalCost, double discountPercentage) {
    if (discountPercentage < 0 || discountPercentage > 100) {
      throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
    }

    BigDecimal discountAmount = rentalCost.multiply(BigDecimal.valueOf(discountPercentage)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    return rentalCost.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
  }

  public List<CarRental> findAvailableCarRentals(String location, String carType, BigDecimal minPrice, BigDecimal maxPrice) {
    List<CarRental> allCarRentals = carRentalRepository.findAll();

    Stream<CarRental> filteredCarRentals = allCarRentals.stream()
        .filter(carRental -> location == null || carRental.getLocation().equalsIgnoreCase(location))
        .peek(carRental -> logger.debug("Filtered by location: id={}, location={}, carType={}, price={}", carRental.getId(), carRental.getLocation(), carRental.getCarType(), carRental.getPrice()))
        .filter(carRental -> carType == null || carRental.getCarType().equalsIgnoreCase(carType))
        .peek(carRental -> logger.debug("Filtered by carType: id={}, location={}, carType={}, price={}", carRental.getId(), carRental.getLocation(), carRental.getCarType(), carRental.getPrice()))
        .filter(carRental -> minPrice == null || carRental.getPrice().compareTo(minPrice) >= 0)
        .peek(carRental -> logger.debug("Filtered by minPrice: id={}, location={}, carType={}, price={}", carRental.getId(), carRental.getLocation(), carRental.getCarType(), carRental.getPrice()))
        .filter(carRental -> maxPrice == null || carRental.getPrice().compareTo(maxPrice) <= 0)
        .peek(carRental -> logger.debug("Filtered by maxPrice: id={}, location={}, carType={}, price={}", carRental.getId(), carRental.getLocation(), carRental.getCarType(), carRental.getPrice()))
        .filter(CarRental::isAvailable)
        .peek(carRental -> logger.debug("Filtered by availability: id={}, location={}, carType={}, price={}", carRental.getId(), carRental.getLocation(), carRental.getCarType(), carRental.getPrice()));

    return filteredCarRentals.collect(Collectors.toList());
  }

  public BigDecimal calculateTotalRentalCost(CarRental carRental, LocalDateTime startDate, LocalDateTime endDate, BigDecimal additionalFees, BigDecimal taxRate, BigDecimal discountRate) {
    BigDecimal rentalCost = calculateRentalCost(carRental, startDate, endDate);

    // Set default values for taxRate and discountRate if not provided
    taxRate = taxRate != null ? taxRate : BigDecimal.ZERO;
    discountRate = discountRate != null ? discountRate : BigDecimal.ZERO;

    BigDecimal discountedRentalCost = applyDiscount(rentalCost, discountRate.doubleValue());
    BigDecimal totalFees = additionalFees != null ? additionalFees : BigDecimal.ZERO;
    BigDecimal taxAmount = discountedRentalCost.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    return discountedRentalCost.add(totalFees).add(taxAmount).setScale(2, RoundingMode.HALF_UP);
  }
}
