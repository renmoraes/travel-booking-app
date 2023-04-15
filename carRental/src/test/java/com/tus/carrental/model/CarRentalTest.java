package com.tus.carrental.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CarRentalTest {

  @Mock
  private CarRental carRental;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    carRental = new CarRental();
  }

  @Test
  void testGetSetId() {
    carRental.setId(1L);
    assertEquals(1L, carRental.getId());
  }

  @Test
  void testGetSetRentalCompany() {
    carRental.setRentalCompany("Hertz");
    assertEquals("Hertz", carRental.getRentalCompany());
  }

  @Test
  void testGetSetLocation() {
    carRental.setLocation("New York City");
    assertEquals("New York City", carRental.getLocation());
  }

  @Test
  void testGetSetCarType() {
    carRental.setCarType("Sedan");
    assertEquals("Sedan", carRental.getCarType());
  }

  @Test
  void testGetSetPrice() {
    BigDecimal price = new BigDecimal("50.00");
    carRental.setPrice(price);
    assertEquals(price, carRental.getPrice());
  }

}
