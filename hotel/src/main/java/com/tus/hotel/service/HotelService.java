package com.tus.hotel.service;

import com.tus.hotel.dao.HotelRepository;
import com.tus.hotel.model.Hotel;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotelService {

  @Autowired
  private HotelRepository hotelRepository;

  public List<Hotel> findAll() {
    return hotelRepository.findAll();
  }

  public Optional<Hotel> findById(Long id) {
    return hotelRepository.findById(id);
  }

  public Hotel save(Hotel hotel) {
    return hotelRepository.save(hotel);
  }

  public void deleteById(Long id) {
    hotelRepository.deleteById(id);
  }

  // Add any custom business logic methods here, if needed

}
