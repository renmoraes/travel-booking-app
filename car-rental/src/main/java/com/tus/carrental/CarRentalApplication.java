package com.tus.carrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CarRentalApplication {

  public static void main(String[] args) {
    SpringApplication.run(CarRentalApplication.class, args);
  }

}
