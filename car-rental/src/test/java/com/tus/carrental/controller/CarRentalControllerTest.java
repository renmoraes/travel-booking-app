package com.tus.carrental.controller;

import static com.tus.carrental.CarRentalBaseUtils.carRentalList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import com.tus.carrental.dao.CarRentalRepository;
import com.tus.carrental.model.CarRental;
import com.tus.carrental.service.CarRentalService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CarRentalControllerTest {

  @Mock
  private CarRentalService carRentalService;

  @MockBean
  private RestTemplate restTemplate;

  @InjectMocks
  private CarRentalController carRentalController;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CarRentalRepository carRentalRepository;


  @Test
  public void testFindAvailableCarRentals() throws Exception {

    List<CarRental> savedCarRetails = carRentalRepository.saveAll(carRentalList());
    // Define search criteria
    String location = "London, UK";
    String carType = "Sedan";
    BigDecimal minPrice = BigDecimal.valueOf(30);
    BigDecimal maxPrice = BigDecimal.valueOf(60);
    LocalDateTime startDate = LocalDateTime.now().plusDays(2);
    LocalDateTime endDate = LocalDateTime.now().plusDays(5);

    // Mock the response from the external API
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.OK);
    doReturn(responseEntity).when(restTemplate).getForEntity(anyString(), eq(Boolean.class));
    ReflectionTestUtils.setField(carRentalService, "restTemplate", restTemplate);

    // Define the expected JSON response
    String expectedJson = "["
        + "{"
        + "\"id\":1,"
        + "\"location\":\"London, UK\","
        + "\"carType\":\"Sedan\","
        + "\"price\":50.0"
        + "}"
        + "]";

    // Make GET request to /api/carrentals/available with search parameters
    mockMvc.perform(MockMvcRequestBuilders.get("/api/carrentals/available")
            .param("location", location)
            .param("carType", carType)
            .param("minPrice", minPrice.toString())
            .param("maxPrice", maxPrice.toString())
            .param("startDate", startDate.toString())
            .param("endDate", endDate.toString()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(expectedJson)); // Expected JSON array of CarRental objects
  }

  @Test
  public void testFindAvailableCarRentalsNoContent() throws Exception {

    List<CarRental> savedCarRetails = carRentalRepository.saveAll(carRentalList());
    // Define search criteria
    String location = "Not valid location";
    String carType = "Sedan";
    BigDecimal minPrice = BigDecimal.valueOf(30);
    BigDecimal maxPrice = BigDecimal.valueOf(60);
    LocalDateTime startDate = LocalDateTime.now().plusDays(2);
    LocalDateTime endDate = LocalDateTime.now().plusDays(5);

    // Mock the response from the external API
    ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(true, HttpStatus.OK);
    doReturn(responseEntity).when(restTemplate).getForEntity(anyString(), eq(Boolean.class));
    ReflectionTestUtils.setField(carRentalService, "restTemplate", restTemplate);

    // Make GET request to /api/carrentals/available with search parameters
    mockMvc.perform(MockMvcRequestBuilders.get("/api/carrentals/available")
            .param("location", location)
            .param("carType", carType)
            .param("minPrice", minPrice.toString())
            .param("maxPrice", maxPrice.toString())
            .param("startDate", startDate.toString())
            .param("endDate", endDate.toString()))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
