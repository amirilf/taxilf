package com.taxilf.core.model;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.taxilf.core.model.entity.Driver;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.entity.PersonalLocation;
import com.taxilf.core.model.entity.User;
import com.taxilf.core.model.entity.Vehicle;
import com.taxilf.core.model.entity.VehicleSubtype;
import com.taxilf.core.model.entity.VehicleType;
import com.taxilf.core.model.entity.Wallet;
import com.taxilf.core.model.enums.Role;
import com.taxilf.core.model.repository.DriverRepository;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.model.repository.UserRepository;
import com.taxilf.core.model.repository.VehicleSubtypeRepository;
import com.taxilf.core.model.repository.VehicleTypeRepository;
import com.taxilf.core.utility.GeometryUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Order(1)
public class InitialData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleSubtypeRepository vehicleSubtypeRepository;

    private static final Random random = new Random();

    InitialData(UserRepository userRepository, PassengerRepository passengerRepository, DriverRepository driverRepository, VehicleTypeRepository vehicleTypeRepository, VehicleSubtypeRepository vehicleSubtypeRepository){
        this.userRepository = userRepository;
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
            
            Wallet wallet = Wallet.builder().build();
            User user = User.builder()
                .name("D" + i)
                .phone("10" + i)
                .location(GeometryUtils.randomPointInMashhad())
                .role(Role.DRIVER)
                .wallet(wallet)
                .build();
            wallet.setUser(user);
            userRepository.save(user);
        
            Vehicle v = Vehicle.builder()
                .vehicleSubtype(listOfVehicleSubtypes[random.nextInt(0,8)])
                .model(random.nextInt(2000,2024))
                .plate(String.valueOf(1000 + i))
                .build();
            Driver d = Driver.builder()
                .vehicle(v)
                .user(user)
                .build();
            v.setDriver(d);

            driverRepository.save(d); // vehicle will be save too (cascade)              
        }
    }

    private void loadPassengers(){

        for (int i = 1; i <= 10; i++) {

            Wallet wallet = Wallet.builder().build();
            User user = User.builder()
                .name("P" + i)
                .phone("20" + i)
                .location(GeometryUtils.randomPointInMashhad())
                .role(Role.PASSENGER)
                .wallet(wallet)
                .build();
            wallet.setUser(user);
            userRepository.save(user); // will also save Wallet

            Passenger passenger = Passenger.builder()
                .user(user)
                .build();

            // personal locations
            List<PersonalLocation> locations = new ArrayList<>();
            for (int j = 1; j <= 4; j++) {
                PersonalLocation location = PersonalLocation.builder()
                    .name("l" + j + "p" + i)
                    .location(GeometryUtils.randomPointInMashhad())
                    .passenger(passenger)
                    .build();
                locations.add(location);
            }
            
            passenger.setPersonalLocations(locations);
            passengerRepository.save(passenger); // will also save pls
        }
    }
}
