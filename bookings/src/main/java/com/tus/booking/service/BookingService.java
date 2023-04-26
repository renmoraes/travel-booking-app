package com.tus.booking.service;

import com.tus.booking.dao.BookingRepository;
import com.tus.booking.model.Booking;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BookingService {

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${car_rentals.module.base-url}")
  private String carRentalModuleBaseUrl;

  public List<Booking> findAll() {
    return bookingRepository.findAll();
  }

  public Optional<Booking> findById(Long id) {
    return bookingRepository.findById(id);
  }

  public Booking save(Booking booking) {
    return bookingRepository.save(booking);
  }

  public void deleteById(Long id) {
    bookingRepository.deleteById(id);
  }

  public boolean isCarAvailable(Long carRentalId) {
    String url =  carRentalModuleBaseUrl + "api/v1/carrentals/" + carRentalId + "/availability";
    ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
    return Boolean.TRUE.equals(response.getBody());
  }

  public void setCarUnavailable(Long carRentalId) {
    String url = carRentalModuleBaseUrl + "api/v1/carrentals/" + carRentalId + "/available?setAvailability=false";
    restTemplate.put(url, null);
  }

  public void setCarAvailable(Long carRentalId) {
    String url = carRentalModuleBaseUrl + "api/v1/carrentals/" + carRentalId + "/available?setAvailability=true";
    restTemplate.put(url, null);
  }

  public BigDecimal getTotalLocationPrice(Long carRentalId, LocalDateTime startDate, LocalDateTime endDate) {
    String url = carRentalModuleBaseUrl + "api/v1/carrentals/" + carRentalId
        + "/totalcost?startDate=" + startDate + "&endDate=" + endDate + "&additionalFees=20&taxRate=10&discountRate=5";
    ResponseEntity<BigDecimal> response = restTemplate.getForEntity(url, BigDecimal.class);
    return response.getBody();
  }
}