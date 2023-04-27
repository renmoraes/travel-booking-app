package com.tus.carrental.service;

import static com.tus.carrental.CarRentalBaseUtils.carRentalList;
import static com.tus.carrental.CarRentalBaseUtils.createCarRental;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.tus.carrental.dao.CarRentalRepository;
import com.tus.carrental.model.CarRental;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class CarRentalServiceTest {

  @MockBean
  private CarRentalRepository carRentalRepository;

  @InjectMocks
  private CarRentalService carRentalService;

  CarRental carRental;

  @BeforeEach
  public void setUp() throws Exception {
    MockitoAnnotations.openMocks(this);
    carRental = createCarRental();
  }

  @Test
  public void testFindAll() {
    List<CarRental> expected = carRentalList();
    when(carRentalRepository.findAll()).thenReturn(expected);

    List<CarRental> result = carRentalService.findAll();
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void testFindById() {
    CarRental expected = createCarRental();

    when(carRentalRepository.findById(expected.getId())).thenReturn(Optional.of(expected));

    Optional<CarRental> result = carRentalService.findById(expected.getId());

    assertThat(result).isEqualTo(Optional.of(expected));
  }

  @Test
  public void testSave() {
    CarRental expected = createCarRental();

    when(carRentalRepository.save(expected)).thenReturn(expected);

    CarRental actual = carRentalService.save(expected);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testDeleteById() {
    // Set up the mock CarRentalRepository to return the mock object when findById is called
    Optional<CarRental> optionalCarRental = Optional.of(carRental);
    doNothing().when(carRentalRepository).deleteById(any(Long.class));
    carRentalService.deleteById(carRental.getId());
    verify(carRentalRepository, times(1)).deleteById(carRental.getId());
  }

  @Test
  public void testCalculateRentalCost() {
    LocalDateTime startDate = LocalDateTime.of(2023, 4, 1, 10, 0);
    LocalDateTime endDate = LocalDateTime.of(2023, 4, 3, 18, 0);

    BigDecimal actual = carRentalService.calculateRentalCost(carRental, startDate, endDate);
    BigDecimal expected = new BigDecimal("100.00");

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void testApplyDiscount() {
    BigDecimal rentalCost = BigDecimal.valueOf(100.00);
    double discountPercentage = 20.0;
    BigDecimal expectedDiscountedCost = BigDecimal.valueOf(80.00)
        .setScale(2,RoundingMode.HALF_UP);

    BigDecimal actualDiscountedCost = carRentalService.applyDiscount(rentalCost, discountPercentage);

    assertEquals(expectedDiscountedCost, actualDiscountedCost);
  }

  @Test
  void testApplyDiscountWithInvalidDiscountPercentage() {
    BigDecimal rentalCost = BigDecimal.valueOf(100.00);
    final double negativeDiscountPercentage = -20.0;

    assertThrows(IllegalArgumentException.class, () -> {
      carRentalService.applyDiscount(rentalCost, negativeDiscountPercentage);
    });

    final double invalidDiscountPercentage = 120.0;
    assertThrows(IllegalArgumentException.class, () -> {
      carRentalService.applyDiscount(rentalCost, invalidDiscountPercentage);
    });
  }

  @Test
  public void testFindAvailableCarRentals() {
    List<CarRental> allCarRentals = carRentalList();

    when(carRentalRepository.findAll()).thenReturn(allCarRentals);
    // Set up mock objects
    RestTemplate restTemplateMock = mock(RestTemplate.class);
    ResponseEntity<Boolean> responseEntity = ResponseEntity.ok(true);
    doReturn(responseEntity).when(restTemplateMock).getForEntity(anyString(), eq(Boolean.class));
    // Inject the mock RestTemplate into the CarRentalService instance
    ReflectionTestUtils.setField(carRentalService, "restTemplate", restTemplateMock);

    List<CarRental> availableCarRentals = carRentalService.findAvailableCarRentals("London, UK", "Sedan", BigDecimal.valueOf(40.00), BigDecimal.valueOf(60.00));

    assertEquals(1, availableCarRentals.size());
    assertEquals(allCarRentals.get(0).getId(), availableCarRentals.get(0).getId());
    assertEquals(allCarRentals.get(0).getRentalCompany(), availableCarRentals.get(0).getRentalCompany());
    assertEquals(allCarRentals.get(0).getCarType(), availableCarRentals.get(0).getCarType());
    assertEquals(allCarRentals.get(0).getLocation(), availableCarRentals.get(0).getLocation());
    assertEquals(allCarRentals.get(0).getPrice(), availableCarRentals.get(0).getPrice());
  }

  @Test
  public void testCarUnavailable() {
    List<CarRental> allCarRentals = carRentalList();

    LocalDateTime startDate = LocalDateTime.now().plusDays(1);
    LocalDateTime endDate = LocalDateTime.now().plusDays(7);

    when(carRentalRepository.findAll()).thenReturn(allCarRentals);
    // Set up mock objects
    RestTemplate restTemplateMock = mock(RestTemplate.class);
    ResponseEntity<Boolean> responseEntity = ResponseEntity.ok(false);
    doReturn(responseEntity).when(restTemplateMock).getForEntity(anyString(), eq(Boolean.class));
    // Inject the mock RestTemplate into the CarRentalService instance
    ReflectionTestUtils.setField(carRentalService, "restTemplate", restTemplateMock);

    boolean carAvailable = carRentalService.isCarAvailable(allCarRentals.get(0).getId());
    assertFalse(carAvailable, "expect car to be unavailable ");
  }

  @Test
  public void testCalculateTotalRentalCost() {
    // Set up test data
    CarRental carRental = new CarRental();
    carRental.setPrice(BigDecimal.valueOf(100));

    LocalDateTime startDate = LocalDateTime.now().plusDays(1);
    LocalDateTime endDate = LocalDateTime.now().plusDays(7);

    BigDecimal additionalFees = BigDecimal.valueOf(50);
    BigDecimal taxRate = BigDecimal.valueOf(10);
    BigDecimal discountRate = BigDecimal.valueOf(20);

    // Call the method under test
    BigDecimal totalCost = carRentalService.calculateTotalRentalCost(carRental, startDate, endDate, additionalFees, taxRate, discountRate);

    // Calculate expected values
    BigDecimal rentalCost = carRentalService.calculateRentalCost(carRental,startDate,endDate);
    BigDecimal discountedRentalCost = carRentalService.applyDiscount(rentalCost, 20);
    BigDecimal taxAmount = discountedRentalCost.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    BigDecimal expectedTotalCost = discountedRentalCost.add(additionalFees).add(taxAmount).setScale(2, RoundingMode.HALF_UP);

    // Check that the result is correct
    assertEquals(expectedTotalCost, totalCost);
  }
}