package com.tus.carrental;

import com.tus.carrental.model.CarRental;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.agent.builder.AgentBuilder.LambdaInstrumentationStrategy;

public class CarRentalBaseUtils {

  public static CarRental createCarRental(){
    CarRental carRental = new CarRental();
    carRental.setId(1L);
    carRental.setRentalCompany("Europcar");
    carRental.setCarType("Sedan");
    carRental.setLocation("London, UK");
    carRental.setPrice(BigDecimal.valueOf(50.00));

    return carRental;
  }

  public static List<CarRental> carRentalList(){
    List<CarRental> carRentalList = new ArrayList<>();

    CarRental carRental = new CarRental();
    carRental.setId(1L);
    carRental.setRentalCompany("Europcar");
    carRental.setCarType("Sedan");
    carRental.setLocation("London, UK");
    carRental.setPrice(BigDecimal.valueOf(50.00));

    CarRental carRental2 = new CarRental();
    carRental2.setId(2L);
    carRental2.setRentalCompany("Hertz");
    carRental2.setCarType("SUV");
    carRental2.setLocation("New York, USA");
    carRental2.setPrice(BigDecimal.valueOf(80.00));

    CarRental carRental3 = new CarRental();
    carRental3.setId(3L);
    carRental3.setRentalCompany("Avis");
    carRental3.setCarType("Compact");
    carRental3.setLocation("Paris, France");
    carRental3.setPrice(BigDecimal.valueOf(40.00));

    carRentalList.add(carRental);
    carRentalList.add(carRental2);
    carRentalList.add(carRental3);

    return  carRentalList;
  }

}
