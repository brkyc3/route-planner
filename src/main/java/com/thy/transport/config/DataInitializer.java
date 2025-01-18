package com.thy.transport.config;

import com.thy.transport.model.Location;
import com.thy.transport.model.Transportation;
import com.thy.transport.model.TransportationType;
import com.thy.transport.model.User;
import com.thy.transport.repository.LocationRepository;
import com.thy.transport.repository.TransportationRepository;
import com.thy.transport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private static final int BATCH_SIZE = 1000;

    private final UserRepository userRepository;
    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;
    private final ForkJoinPool customForkJoinPool;

    @Value("${run-dataload}")
    private boolean RUN_DATA_LOAD;

    @Override
    public void run(String... args) throws ExecutionException, InterruptedException {
        if (!RUN_DATA_LOAD) {
            return;
        }
        log.info("creating users");
        createUsers();
        log.info("creating locations");
        List<Location> locations = createLocations();
        log.info("created {} locations",locations.size());

        log.info("creating transportations");
        createTransportations(locations);

    }

    private void createTransportations(List<Location> locations) {
        List<Transportation> transportations = Collections.synchronizedList(new ArrayList<>());
        List<TransportationType> transportationTypes = Arrays.asList(
            TransportationType.FLIGHT, TransportationType.BUS, 
            TransportationType.UBER, TransportationType.SUBWAY
        );

        // Generate all transportations first
        locations.forEach(origin -> {
            int numberOfTransportationPerLocation = RANDOM.nextInt(150, 200);
            for (int j = 0; j < numberOfTransportationPerLocation; j++) {
                Location destination = locations.get(RANDOM.nextInt(locations.size()));
                Transportation transportation = new Transportation();
                transportation.setOriginLocation(origin);
                transportation.setDestinationLocation(destination);
                transportation.setOperatingDays("1,2,3,4,5,6,7");
                transportation.setTransportationType(
                    transportationTypes.get(RANDOM.nextInt(transportationTypes.size()))
                );
                transportations.add(transportation);
            }
        });

        // Save in parallel batches
        List<List<Transportation>> batches = partition(new ArrayList<>(transportations), BATCH_SIZE);
        
        try {
            customForkJoinPool.submit(() ->
                batches.parallelStream()
                    .map(ArrayList::new) // Create new ArrayList to avoid concurrent modification
                    .forEach(batch -> {
                        try {
                            transportationRepository.saveAll(batch);
                        } catch (Exception e) {
                            log.error("Error saving transportation batch: ", e);
                        }
                    })
            ).get(); // Wait for completion
        } catch (Exception e) {
            log.error("Error in parallel transportation save: ", e);
        }
    }

    private List<Location> createLocations() throws ExecutionException, InterruptedException {
        Set<String> locationCodes = ConcurrentHashMap.newKeySet();
        List<Location> locations = Collections.synchronizedList(new ArrayList<>());
        
        // Generate locations first
        for (int i = 0; locationCodes.size() < 10000; i++) {
            String locationCode = generateRandomString(3);
            if (locationCodes.add(locationCode)) {
                Location location = new Location();
                location.setLocationCode(locationCode);
                location.setCity("City" + i);
                location.setCountry("TR");
                location.setName(locationCode);
                locations.add(location);
            }
        }

        List<Location> savedLocations = Collections.synchronizedList(new ArrayList<>());
        List<List<Location>> batches = partition(new ArrayList<>(locations), BATCH_SIZE);

        try {
            customForkJoinPool.submit(() ->
                batches.parallelStream()
                    .map(ArrayList::new) // Create new ArrayList to avoid concurrent modification
                    .forEach(batch -> {
                        try {
                            List<Location> saved = locationRepository.saveAll(batch);
                            savedLocations.addAll(saved);
                        } catch (Exception e) {
                            log.error("Error saving location batch: ", e);
                        }
                    })
            ).get(); // Wait for completion
        } catch (Exception e) {
            log.error("Error in parallel location save: ", e);
        }

        return savedLocations;
    }

    private static <T> List<List<T>> partition(List<T> list, int batchSize) {
        int totalSize = list.size();
        return IntStream.range(0, (totalSize + batchSize - 1) / batchSize)
            .mapToObj(i -> list.subList(
                i * batchSize, 
                Math.min((i + 1) * batchSize, totalSize)
            ))
            .map(ArrayList::new) // Create new ArrayList for each partition
            .collect(Collectors.toList());
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

} 