package com.tus.gateway.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

  @GetMapping (value = "/car-rental-fallback")
  public  ResponseEntity<Object> carRental() {
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("reason", "Gateway Timeout!");
    response.put("message","Car Rental API is taking too long to respond or is down. Please try again later");
    return new ResponseEntity<>(
        response,
        HttpStatus.GATEWAY_TIMEOUT
    );
  }
  @GetMapping (value = "/bookings-fallback")
  public  ResponseEntity<Object> bookings() {
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("reason", "Gateway Timeout!");
    response.put("message", "Bookings API is taking too long to respond or is down. Please try again later");
    return new ResponseEntity<>(
        response,
        HttpStatus.GATEWAY_TIMEOUT
    );

  }

}
