package com.thy.transport.service;

import com.thy.transport.config.Constants;
import com.thy.transport.dto.request.RouteSearchRequest;
import com.thy.transport.dto.response.RouteResponse;
import com.thy.transport.dto.response.RouteSegment;
import com.thy.transport.mapper.LocationMapper;
import com.thy.transport.mapper.TransportationMapper;
import com.thy.transport.model.TransportationType;
import com.thy.transport.repository.TransportationRepository;
import com.thy.transport.service.dto.TransportationDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {
    private final TransportationRepository transportationRepository;
    private final LocationMapper locationMapper;
    private final TransportationMapper transportationMapper;
    private final ForkJoinPool customForkJoinPool = new ForkJoinPool(100);
    private final CacheService cacheService;

    @Cacheable(value = Constants.RedisCacheNames.ROUTES, key = "#request.originLocationCode + '_' + #request.destinationLocationCode + '_' + #request.travelDate.dayOfWeek")
    public List<RouteResponse> searchRoutes(RouteSearchRequest request) {
        int dayOfWeek = request.getTravelDate().getDayOfWeek().getValue();
        String origin = request.getOriginLocationCode();
        String destination = request.getDestinationLocationCode();

        log.info("finding routes");
        List<RouteResponse> validRoutes = findRoutes(origin, destination, dayOfWeek);
        log.info("found {} valid routes", validRoutes.size());

        return validRoutes;
    }

    private List<RouteResponse> findRoutes(String origin, String destination, int dayOfWeek) {
        List<RouteResponse> validRoutes = Collections.synchronizedList(new ArrayList<>());
        Queue<GraphNode> locationsToProcess = new ConcurrentLinkedQueue<>();

        // Add initial location with empty path
        locationsToProcess.offer(new GraphNode(origin, new ArrayList<>()));

        Map<String, List<TransportationDto>> destinationTransportationsCache = transportationRepository.findNonFlightsByDestinationCode(destination)
                .stream()
                .map(transportationMapper::toDto).filter(route -> route.getOperatingDays().contains(dayOfWeek))
                .collect(Collectors.groupingBy(transportationDto -> transportationDto.getOriginLocation().getLocationCode()));
        // Process up to 3 levels deep
        int currentDepth = 0;
        while (currentDepth < 3 && !locationsToProcess.isEmpty()) {
            AtomicLong totalCalls = new AtomicLong(0);
            AtomicLong totalTime = new AtomicLong(0);
            AtomicLong skippedQueries = new AtomicLong(0);

            log.info("Processing level {}", currentDepth);
            Set<GraphNode> nextLevelLocations = ConcurrentHashMap.newKeySet();
            final int finalCurrentDepth = currentDepth;

            customForkJoinPool.submit(() -> locationsToProcess.parallelStream().forEach(node -> {
                String location = node.location;
                List<TransportationDto> currentPath = node.path;

                // Check if this path could potentially lead to a valid route
                List<TransportationType> types = currentPath.stream().map(TransportationDto::getTransportationType).collect(Collectors.toList());

                if (!couldLeadToValidPath(types)) {
                    skippedQueries.incrementAndGet();
                    return;
                }

                long startTime = System.currentTimeMillis();
                List<TransportationDto> routes;

                if (finalCurrentDepth < 2) {
                    routes = transportationRepository.findByOriginCode(location).stream()
                            .map(transportationMapper::toDto)
                            .filter(route -> route.getOperatingDays().contains(dayOfWeek))
                            .collect(Collectors.toList());
                } else {
                    routes = destinationTransportationsCache.getOrDefault(location,Collections.emptyList());
                }

                long endTime = System.currentTimeMillis();
                totalCalls.incrementAndGet();
                totalTime.addAndGet(endTime - startTime);

                // Process routes and build valid paths
                routes.forEach(route -> {
                    List<TransportationDto> newPath = new ArrayList<>(currentPath);
                    newPath.add(route);

                    String nextLocation = route.getDestinationLocation().getLocationCode();

                    // If destination reached, validate and add to results
                    if (nextLocation.equals(destination)) {
                        List<TransportationType> pathTypes = newPath.stream().map(TransportationDto::getTransportationType).collect(Collectors.toList());

                        if (isValidTransportationPattern(pathTypes)) {
                            validRoutes.add(createRouteResponse(newPath));
                        }
                    } else if (newPath.size() < 3) {
                        // Add to next level if not at max depth
                        nextLevelLocations.add(new GraphNode(nextLocation, newPath));
                    }
                });
            })).join();

            locationsToProcess.clear();
            locationsToProcess.addAll(nextLevelLocations);
            currentDepth++;

            long averageProcessingTime = ((long) totalTime.get()) / (totalCalls.get() == 0 ? 1 : totalCalls.get());
            log.info("Level {} stats - Total calls: {}, Skipped queries: {}, Average time: {}ms", currentDepth, totalCalls.get(), skippedQueries.get(), averageProcessingTime);
        }

        cacheService.logCacheStats();
        return validRoutes;
    }

    @Data
    private static class GraphNode {
        final String location;
        final List<TransportationDto> path;

        GraphNode(String location, List<TransportationDto> path) {
            this.location = location;
            this.path = path;
        }
    }

    private boolean isValidTransportationPattern(List<TransportationType> path) {
        // Single leg must be a flight
        if (path.size() == 1) {
            return TransportationType.FLIGHT.equals(path.get(0));
        }

        // Two legs: must be flight->other or other->flight
        if (path.size() == 2) {
            boolean firstIsFlight = TransportationType.FLIGHT.equals(path.get(0));
            boolean secondIsFlight = TransportationType.FLIGHT.equals(path.get(1));
            return firstIsFlight != secondIsFlight; // XOR - one must be flight, one must not be
        }

        // Three legs: must be other->flight->other
        return !TransportationType.FLIGHT.equals(path.get(0)) && TransportationType.FLIGHT.equals(path.get(1)) && !TransportationType.FLIGHT.equals(path.get(2));
    }

    private boolean couldLeadToValidPath(List<TransportationType> currentPath) {
        if (currentPath.size() == 2) {
            return TransportationType.FLIGHT.equals(currentPath.get(1)) && !TransportationType.FLIGHT.equals(currentPath.get(0));
        } else {
            return true;
        }
    }

    private RouteSegment createRouteSegment(TransportationDto transportation) {
        RouteSegment segment = new RouteSegment();
        segment.setOriginLocation(locationMapper.toResponse(transportation.getOriginLocation()));
        segment.setDestinationLocation(locationMapper.toResponse(transportation.getDestinationLocation()));
        segment.setTransportationType(transportation.getTransportationType());
        return segment;
    }

    private RouteResponse createRouteResponse(List<TransportationDto> path) {
        RouteResponse response = new RouteResponse();
        response.setSegments(path.stream().map(this::createRouteSegment).collect(Collectors.toList()));
        response.setTotalLegs(path.size());
        return response;
    }
} 