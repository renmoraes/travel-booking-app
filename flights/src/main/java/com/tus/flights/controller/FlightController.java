package com.tus.flights.controller;

import com.tus.flights.model.Flight;
import com.tus.flights.service.FlightService;
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
@RequestMapping("/api/flights")
public class FlightController {

  @Autowired
  private FlightService flightService;

  @GetMapping
  public ResponseEntity<List<Flight>> findAll() {
    List<Flight> flights = flightService.findAll();
    return ResponseEntity.ok(flights);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Flight> findById(@PathVariable Long id) {
    Optional<Flight> flightOptional = flightService.findById(id);
    return flightOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Flight> save(@RequestBody Flight flight) {
    Flight savedFlight = flightService.save(flight);
    return ResponseEntity.ok(savedFlight);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Flight> update(@PathVariable Long id, @RequestBody Flight flight) {
    Optional<Flight> flightOptional = flightService.findById(id);
    if (!flightOptional.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    flight.setId(id);
    Flight updatedFlight = flightService.save(flight);
    return ResponseEntity.ok(updatedFlight);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    Optional<Flight> flightOptional = flightService.findById(id);
    if (!flightOptional.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    flightService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // Add any custom API endpoints here, if needed

}
