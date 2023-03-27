package com.tus.flights.service;

import com.tus.flights.dao.FlightRepository;
import com.tus.flights.model.Flight;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlightService {

  @Autowired
  private FlightRepository flightRepository;

  public List<Flight> findAll() {
    return flightRepository.findAll();
  }

  public Optional<Flight> findById(Long id) {
    return flightRepository.findById(id);
  }

  public Flight save(Flight flight) {
    return flightRepository.save(flight);
  }

  public void deleteById(Long id) {
    flightRepository.deleteById(id);
  }

  // Add any custom business logic methods here, if needed

}
