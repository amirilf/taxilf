package com.taxilf.core.model;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.taxilf.core.model.entity.Driver;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.entity.Vehicle;
import com.taxilf.core.model.entity.VehicleSubtype;
import com.taxilf.core.model.entity.VehicleType;
import com.taxilf.core.model.repository.DriverRepository;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.model.repository.VehicleSubtypeRepository;
import com.taxilf.core.model.repository.VehicleTypeRepository;

import java.util.Arrays;
import java.util.Random;

@Component
@Order(1)
public class InitialData implements CommandLineRunner {

    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleSubtypeRepository vehicleSubtypeRepository;

    private final Random random = new Random();

    InitialData(PassengerRepository passengerRepository, DriverRepository driverRepository, VehicleTypeRepository vehicleTypeRepository, VehicleSubtypeRepository vehicleSubtypeRepository){
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.vehicleSubtypeRepository = vehicleSubtypeRepository;
    }

    private void p(Object o){
        System.out.println(o);
    }

    @Override
    public void run(String... args) throws Exception {
        p("loading...");
        loadPassengers();
        loadVehiclesAndDrivers();
        p("completed...");
    }


    private void loadVehiclesAndDrivers(){

        // vehicle-types
        VehicleType vt1 = VehicleType.builder().name("Motor").build();
        VehicleType vt2 = VehicleType.builder().name("Car").build();
        VehicleType vt3 = VehicleType.builder().name("Truck").build();
        vehicleTypeRepository.saveAll(Arrays.asList(vt1, vt2, vt3));

        // vehicle-subtypes
        VehicleSubtype[] listOfVehicleSubtypes = new VehicleSubtype[] {
            
            // motors
            VehicleSubtype.builder().name("Honda").vehicleType(vt1).build(),
            VehicleSubtype.builder().name("Yamaha").vehicleType(vt1).build(),
            VehicleSubtype.builder().name("Ducati").vehicleType(vt1).build(),
        
            // cars
            VehicleSubtype.builder().name("BMW").vehicleType(vt2).build(),
            VehicleSubtype.builder().name("Toyota").vehicleType(vt2).build(),
            VehicleSubtype.builder().name("Ford").vehicleType(vt2).build(),
        
            // trucks
            VehicleSubtype.builder().name("Neisan").vehicleType(vt3).build(),
            VehicleSubtype.builder().name("Venet").vehicleType(vt3).build()  
        
        };
        vehicleSubtypeRepository.saveAll(Arrays.asList(listOfVehicleSubtypes));
        
        // drivers & vehicles
        for (int i = 1; i <= 20; i++) {
            
            Vehicle v = Vehicle.builder()
                .vehicleSubtype(listOfVehicleSubtypes[random.nextInt(0,8)])
                .model(random.nextInt(2000,2024))
                .plate(String.valueOf(1000 + i))
                .build();
            
            Driver d = Driver.builder()
                .name("D" + i)
                .phone(String.valueOf(100 + i))
                .vehicle(v)
                .build();

            v.setDriver(d);

            // will create both since we used cascade
            driverRepository.save(d);
        }
    }

    private void loadPassengers(){
        Passenger p1 = Passenger.builder().name("P1").phone("100").build();
        Passenger p2 = Passenger.builder().name("P2").phone("200").build();
        Passenger p3 = Passenger.builder().name("P3").phone("300").build();
        Passenger p4 = Passenger.builder().name("P4").phone("400").build();
        Passenger p5 = Passenger.builder().name("P5").phone("500").build();
        passengerRepository.saveAll(Arrays.asList(p1,p2,p3,p4,p5));
    }
}
