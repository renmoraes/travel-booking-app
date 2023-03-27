package com.tus.hotel.controller;

import com.tus.hotel.model.Hotel;
import com.tus.hotel.service.HotelService;
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
@RequestMapping("/api/hotels")
public class HotelController {

  @Autowired
  private HotelService hotelService;

  @GetMapping
  public ResponseEntity<List<Hotel>> findAll() {
    List<Hotel> hotels = hotelService.findAll();
    return ResponseEntity.ok(hotels);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Hotel> findById(@PathVariable Long id) {
    Optional<Hotel> hotelOptional = hotelService.findById(id);
    return hotelOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Hotel> save(@RequestBody Hotel hotel) {
    Hotel savedHotel = hotelService.save(hotel);
    return ResponseEntity.ok(savedHotel);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Hotel> update(@PathVariable Long id, @RequestBody Hotel hotel) {
    Optional<Hotel> hotelOptional = hotelService.findById(id);
    if (!hotelOptional.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    hotel.setId(id);
    Hotel updatedHotel = hotelService.save(hotel);
    return ResponseEntity.ok(updatedHotel);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    Optional<Hotel> hotelOptional = hotelService.findById(id);
    if (!hotelOptional.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    hotelService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // Add any custom API endpoints here, if needed

}
