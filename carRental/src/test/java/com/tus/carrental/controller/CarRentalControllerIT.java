package com.tus.carrental.controller;

import static com.tus.carrental.CarRentalBaseUtils.carRentalList;
import static com.tus.carrental.CarRentalBaseUtils.createCarRental;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.carrental.dao.CarRentalRepository;
import com.tus.carrental.model.CarRental;
import com.tus.carrental.service.CarRentalService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CarRentalControllerIT {

  private static TestRestTemplate restTemplate;
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CarRentalRepository carRentalRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @LocalServerPort
  private int port;

  @BeforeAll
  public static void setUp() {
    restTemplate = new TestRestTemplate();
  }

  @BeforeEach
  public void cleanDb() {
    // Clean db before start tests
    carRentalRepository.deleteAll();
  }

  @Test
  public void testFindAllCarsNoContent() {

    ResponseEntity<ArrayList> response = restTemplate.getForEntity(
        "http://localhost:" + port + "/api/carrentals", ArrayList.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  public void testSaveCarRental() {
    CarRental newCarRental = createCarRental();
    ResponseEntity<CarRental> response = restTemplate.postForEntity(
        "http://localhost:" + port + "/api/carrentals", newCarRental, CarRental.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().getId()).isNotNull();
    assertThat(response.getBody().getCarType()).isEqualTo(newCarRental.getCarType());
    assertThat(response.getBody().getPrice()).isEqualTo(newCarRental.getPrice());
    assertThat(response.getBody().getLocation()).isEqualTo(newCarRental.getLocation());
    assertThat(response.getBody().getRentalCompany()).isEqualTo(newCarRental.getRentalCompany());
  }

  @Test
  void testFindAllCarRentals() throws Exception {
    // Create some test data
    List<CarRental> allCarRentals = carRentalList();
    carRentalRepository.saveAll(allCarRentals);

    ResponseEntity<List<CarRental>> response = restTemplate.exchange(
        "http://localhost:" + port + "/api/carrentals", HttpMethod.GET, null,
        new ParameterizedTypeReference<List<CarRental>>() {
        });

    List<CarRental> carRentalListResponse = response.getBody();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(allCarRentals.get(0).getRentalCompany()).isEqualTo(
        carRentalListResponse.get(0).getRentalCompany());
    assertThat(allCarRentals.get(0).getCarType()).isEqualTo(
        carRentalListResponse.get(0).getCarType());
    assertThat(allCarRentals.get(0).getPrice()).isEqualTo(carRentalListResponse.get(0).getPrice());
    assertThat(allCarRentals.get(0).getLocation()).isEqualTo(
        carRentalListResponse.get(0).getLocation());

    assertThat(allCarRentals.get(1).getRentalCompany()).isEqualTo(
        carRentalListResponse.get(1).getRentalCompany());
    assertThat(allCarRentals.get(1).getCarType()).isEqualTo(
        carRentalListResponse.get(1).getCarType());
    assertThat(allCarRentals.get(1).getPrice()).isEqualTo(carRentalListResponse.get(1).getPrice());
    assertThat(allCarRentals.get(1).getLocation()).isEqualTo(
        carRentalListResponse.get(1).getLocation());
  }

  @Test
  public void testGetCarRentalById() {
    CarRental carRental = carRentalRepository.save(createCarRental());
    ResponseEntity<CarRental> response = restTemplate.getForEntity(
        "http://localhost:" + port + "/api/carrentals/" + carRental.getId(), CarRental.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(carRental.getId());
    assertThat(response.getBody().getLocation()).isEqualTo(carRental.getLocation());
    assertThat(response.getBody().getPrice()).isEqualTo(carRental.getPrice());
    assertThat(response.getBody().getCarType()).isEqualTo(carRental.getCarType());
    assertThat(response.getBody().getRentalCompany()).isEqualTo(carRental.getRentalCompany());
  }

  @Test
  public void testUpdateCarRental() {
    CarRental carRental = carRentalRepository.save(createCarRental());
    Long id = carRental.getId();

    carRental.setLocation("New Location");
    carRental.setPrice(BigDecimal.valueOf(200.00));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<CarRental> entity = new HttpEntity<>(carRental, headers);

    ResponseEntity<CarRental> response = restTemplate.exchange(
        "http://localhost:" + port + "/api/carrentals/" + id, HttpMethod.PUT, entity,
        CarRental.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getId()).isEqualTo(id);
    assertThat(response.getBody().getLocation()).isEqualTo(carRental.getLocation());
    assertThat(response.getBody().getPrice()).isEqualTo(carRental.getPrice());
  }

  @Test
  public void testUpdateCarRentalNotFound() {

    Long id = 125L;
    // Create a new car rental object to update
    CarRental carRentalToUpdate = createCarRental();
    carRentalRepository.save(carRentalToUpdate);

    // Make a PUT request to update the car rental object
    ResponseEntity<CarRental> response = restTemplate.exchange(
        "http://localhost:" + port + "/api/carrentals/" + id, HttpMethod.PUT,
        new HttpEntity<>(carRentalToUpdate), CarRental.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void testGetCarRentalByIdNotFound() {
    Long id = 125L;
    carRentalRepository.save(createCarRental());
    ResponseEntity<CarRental> response = restTemplate.getForEntity(
        "http://localhost:" + port + "/api/carrentals/" + id, CarRental.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNull();
  }

  @Test
  public void testDeleteCarRental() {
    CarRental carRental = carRentalRepository.save(createCarRental());
    ResponseEntity<Void> response = restTemplate.exchange(
        "http://localhost:" + port + "/api/carrentals/" + carRental.getId(), HttpMethod.DELETE, null,
        Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }


  @Test
  public void testDeleteCarRentalNotFound() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "http://localhost:" + port + "/api/carrentals/1", HttpMethod.DELETE, null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  public void testGetTotalRentalCost() {
    // Create a car rental object
    CarRental carRental = carRentalRepository.save(createCarRental());

    // Set up the request parameters
    LocalDateTime startDate = LocalDateTime.now().plusDays(1);
    LocalDateTime endDate = LocalDateTime.now().plusDays(7);
    BigDecimal additionalFees = BigDecimal.valueOf(20.00);
    BigDecimal taxRate = BigDecimal.valueOf(10.00);
    BigDecimal discountRate = BigDecimal.valueOf(5.00);

    CarRentalService carRentalService = new CarRentalService();
    BigDecimal expectedTotalRental = carRentalService.calculateTotalRentalCost(carRental,
        startDate, endDate, additionalFees, taxRate, discountRate);

    // Send the request
    ResponseEntity<BigDecimal> response = restTemplate.getForEntity(
        "http://localhost:" + port + "/api/carrentals/" + carRental.getId()
            + "/totalcost" +
            "?startDate=" + startDate +
            "&endDate=" + endDate +
            "&additionalFees=" + additionalFees +
            "&taxRate=" + taxRate +
            "&discountRate=" + discountRate, BigDecimal.class);

    // Verify the response
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedTotalRental);
  }

}
