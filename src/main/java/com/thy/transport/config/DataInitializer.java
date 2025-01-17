package com.thy.transport.config;

import com.thy.transport.model.Location;
import com.thy.transport.model.Transportation;
import com.thy.transport.model.TransportationType;
import com.thy.transport.model.User;
import com.thy.transport.repository.LocationRepository;
import com.thy.transport.repository.TransportationRepository;
import com.thy.transport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;


    @Value("${run-dataload}")
    private boolean RUN_DATA_LOAD;

    @Override
    public void run(String... args) {
        if(!RUN_DATA_LOAD){
            return;
        }
        createUsers();

        List<Location> locations = createLocations();

        createTransportations(locations);

    }

    private void createTransportations(List<Location> locations) {
        ArrayList<Transportation> transportations = new ArrayList<>();
        List<TransportationType> transportationTypes = Arrays.asList(TransportationType.FLIGHT,TransportationType.BUS,TransportationType.UBER,TransportationType.SUBWAY);
        for(Location origin : locations){
            System.out.println("origin");
            int numberOfTransportationPerLocation = RANDOM.nextInt(10, 50);
            for(int j = 0 ; j< numberOfTransportationPerLocation;j++){
                Location destination = locations.get(RANDOM.nextInt(locations.size()));
                System.out.println("destination");

                Transportation transportation = new Transportation();
                transportation.setOriginLocation(origin);
                transportation.setDestinationLocation(destination);
                transportation.setOperatingDays("1,2,3,4,5,6,7");
                transportation.setTransportationType(transportationTypes.get(RANDOM.nextInt(transportationTypes.size())));
                transportations.add(transportation);
            }
        }
        System.out.println("transportations size "+ transportations.size());
        transportationRepository.saveAll(transportations);
    }

    private List<Location> createLocations() {
        HashSet<String> locationsKes = new HashSet<>();
        ArrayList<Location> locations = new ArrayList<>();
        for(int i =0;locationsKes.size()<10000; i++){
            String locationCode = generateRandomString(3);
            if(locationsKes.add(locationCode)){
                Location location = new Location();
                location.setLocationCode(locationCode);
                location.setCity("City" +i);
                location.setCountry("TR");
                location.setName(locationCode);
                locations.add(location);
            }
        }
        return locationRepository.saveAll(locations);
    }

    private void createUsers() {
        // Only add if no users exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole("ADMIN");
            userRepository.save(adminUser);
        }

        if (userRepository.findByUsername("burak").isEmpty()) {
            User regularUser = new User();
            regularUser.setUsername("burak");
            regularUser.setPassword(passwordEncoder.encode("yazici"));
            regularUser.setRole("USER");
            userRepository.save(regularUser);
        }

        if (userRepository.findByUsername("test").isEmpty()) {
            User regularUser = new User();
            regularUser.setUsername("test");
            regularUser.setPassword(passwordEncoder.encode("test"));
            regularUser.setRole("TEST");
            userRepository.save(regularUser);
        }
    }

    // Define the character set to use for generating the random string
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int length = 10; // Specify the desired length
        String randomString = generateRandomString(length);
        System.out.println("Random String: " + randomString);
    }
} 