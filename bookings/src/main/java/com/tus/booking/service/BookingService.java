package com.tus.booking.service;

import com.tus.booking.dao.BookingRepository;
import com.tus.booking.model.Booking;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BookingService {

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private RestTemplate restTemplate;
  @Getter
  private final HttpHeaders headers = new HttpHeaders();

  private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

  public List<Booking> findAll() {
    return bookingRepository.findAll();
  }

  public Optional<Booking> findById(Long id) {
    return bookingRepository.findById(id);
  }

  public Optional<Booking> findByIdWithDelay(Long id, int delayMillis) throws InterruptedException {
    Thread.sleep(delayMillis);
    return bookingRepository.findById(id);
  }

  private Optional<Booking> findByIdFallback(Long id, Throwable e) {
    LOGGER.error("Error fetching booking with id {}, error message: {}", id, e.getMessage());
    return Optional.empty();
  }

  public Booking save(Booking booking) {
    return bookingRepository.save(booking);
  }

  public void deleteById(Long id) {
    bookingRepository.deleteById(id);
  }

  private void setAuthorizationHeader(String authHeader){
    this.headers.set(HttpHeaders.AUTHORIZATION,authHeader);
  }

  public boolean isCarAvailable(Long carRentalId, String authHeader) {
    String url = "http://api-gateway/api/v1/carrentals/" + carRentalId + "/availability";
    setAuthorizationHeader(authHeader);
    ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<String>(getHeaders()), Boolean.class);
    return Boolean.TRUE.equals(response.getBody());
  }

  public void setCarUnavailable(Long carRentalId, String authHeader) {
    setAuthorizationHeader(authHeader);
    String url = "http://api-gateway/api/v1/carrentals/" + carRentalId + "/available?setAvailability=false";
    restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<String>(getHeaders()), Void.class);
  }

  public void setCarAvailable(Long carRentalId, String authHeader) {
    setAuthorizationHeader(authHeader);
    String url = "http://api-gateway/api/v1/carrentals/" + carRentalId + "/available?setAvailability=true";
    restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<String>(getHeaders()), Void.class);
  }

  public BigDecimal getTotalLocationPrice(Long carRentalId, LocalDateTime startDate,
      LocalDateTime endDate, String authHeader) {
    setAuthorizationHeader(authHeader);
    String url = "http://car-rental-service/api/v1/carrentals/" + carRentalId
        + "/totalcost?startDate=" + startDate + "&endDate=" + endDate + "&additionalFees=20&taxRate=10&discountRate=5";
    ResponseEntity<BigDecimal> response =  restTemplate.exchange(url, HttpMethod.GET,
        new HttpEntity<String>(getHeaders()), BigDecimal.class);
    return response.getBody();
  }
}