package com.thy.transport.service;

import com.thy.transport.dto.request.RouteSearchRequest;
import com.thy.transport.dto.response.RouteResponse;
import com.thy.transport.dto.response.RouteSegment;
import com.thy.transport.mapper.LocationMapper;
import com.thy.transport.mapper.TransportationMapper;
import com.thy.transport.model.Transportation;
import com.thy.transport.model.TransportationType;
import com.thy.transport.repository.TransportationRepository;
import com.thy.transport.service.dto.TransportationDto;
import com.thy.transport.util.RouteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {
    private final TransportationRepository transportationRepository;
    private final LocationMapper locationMapper;
    private final TransportationMapper transportationMapper;

    public List<RouteResponse> searchRoutes(RouteSearchRequest request) {
        List<RouteResponse> allRoutes = new ArrayList<>();
        int dayOfWeek = request.getTravelDate().getDayOfWeek().getValue();

        // Cache for origin -> routes mapping (only for current search)
        Map<String, List<Transportation>> searchCache = new HashMap<>();

        findDirectRoutes(request, dayOfWeek, allRoutes, searchCache);
        findTwoLegRoutes(request, dayOfWeek, allRoutes, searchCache);
        findThreeLegRoutes(request, dayOfWeek, allRoutes, searchCache);

        return allRoutes;
    }

    private List<TransportationDto> getRoutesFromOrigin(String originCode, Map<String, List<Transportation>> searchCache) {
        List<Transportation> transportation = searchCache.computeIfAbsent(originCode, transportationRepository::findByOriginCode);
        return transportation.stream().map(transportationMapper::toDto).collect(Collectors.toList());
    }

    private void findDirectRoutes(RouteSearchRequest request, int dayOfWeek,
            List<RouteResponse> allRoutes, Map<String, List<Transportation>> searchCache) {
        
        List<TransportationDto> directRoutes = getRoutesFromOrigin(request.getOriginLocationCode(), searchCache)
                .stream()
                .filter(t -> t.getDestinationLocation().getLocationCode().equals(request.getDestinationLocationCode()))
                .filter(t -> TransportationType.FLIGHT.equals(t.getTransportationType()))
                .collect(Collectors.toList());

        allRoutes.addAll(createRouteResponses(directRoutes, dayOfWeek));
    }

    private void findTwoLegRoutes(RouteSearchRequest request, int dayOfWeek,
            List<RouteResponse> allRoutes, Map<String, List<Transportation>> searchCache) {
        
        List<TransportationDto> firstLegRoutes = getRoutesFromOrigin(request.getOriginLocationCode(), searchCache);

        for (TransportationDto firstLeg : firstLegRoutes) {
            if (!firstLeg.getOperatingDays().contains(dayOfWeek)) {
                continue;
            }

            String intermediateLocation = firstLeg.getDestinationLocation().getLocationCode();
            List<TransportationDto> secondLegRoutes = getRoutesFromOrigin(intermediateLocation, searchCache)
                    .stream()
                    .filter(t -> t.getDestinationLocation().getLocationCode().equals(request.getDestinationLocationCode()))
                    .toList();

            for (TransportationDto secondLeg : secondLegRoutes) {
                if (isValidConnection(secondLeg, dayOfWeek, firstLeg)) {
                    List<TransportationDto> route = Arrays.asList(firstLeg, secondLeg);
                    if (RouteValidator.isValidRoutePattern(route)) {
                        RouteResponse response = createTwoLegRoute(firstLeg, secondLeg);
                        allRoutes.add(response);
                    }
                }
            }
        }
    }

    private void findThreeLegRoutes(RouteSearchRequest request, int dayOfWeek,
            List<RouteResponse> allRoutes, Map<String, List<Transportation>> searchCache) {
        
        List<TransportationDto> firstLegRoutes = getRoutesFromOrigin(request.getOriginLocationCode(), searchCache);

        for (TransportationDto firstLeg : firstLegRoutes) {
            if (!firstLeg.getOperatingDays().contains(dayOfWeek)) {
                continue;
            }

            String firstDestination = firstLeg.getDestinationLocation().getLocationCode();
            List<TransportationDto> secondLegRoutes = getRoutesFromOrigin(firstDestination, searchCache);

            for (TransportationDto secondLeg : secondLegRoutes) {
                if (!isValidConnection(secondLeg, dayOfWeek, firstLeg) || 
                    secondLeg.getDestinationLocation().getLocationCode().equals(request.getDestinationLocationCode())) {
                    continue;
                }

                String secondDestination = secondLeg.getDestinationLocation().getLocationCode();
                List<TransportationDto> thirdLegRoutes = getRoutesFromOrigin(secondDestination, searchCache)
                        .stream()
                        .filter(t -> t.getDestinationLocation().getLocationCode().equals(request.getDestinationLocationCode()))
                        .collect(Collectors.toList());

                for (TransportationDto thirdLeg : thirdLegRoutes) {
                    if (isValidConnection(thirdLeg, dayOfWeek, secondLeg)) {
                        List<TransportationDto> route = Arrays.asList(firstLeg, secondLeg, thirdLeg);
                        if (RouteValidator.isValidRoutePattern(route)) {
                            RouteResponse response = createThreeLegRoute(firstLeg, secondLeg, thirdLeg);
                            allRoutes.add(response);
                        }
                    }
                }
            }
        }
    }

    private boolean isValidConnection(TransportationDto leg, int dayOfWeek, TransportationDto previousLeg) {
        return leg.getOperatingDays().contains(dayOfWeek) &&
               !leg.getDestinationLocation().getLocationCode()
                   .equals(previousLeg.getOriginLocation().getLocationCode());
    }

    private List<RouteResponse> createRouteResponses(List<TransportationDto> routes, int dayOfWeek) {
        return routes.stream()
                .filter(transportation -> transportation.getOperatingDays().contains(dayOfWeek))
                .map(transportation -> {
                    RouteResponse response = new RouteResponse();
                    response.setSegments(List.of(createRouteSegment(transportation)));
                    response.setTotalLegs(1);
                    return response;
                })
                .collect(Collectors.toList());
    }

    private RouteResponse createTwoLegRoute(TransportationDto firstLeg, TransportationDto secondLeg) {
        RouteResponse response = new RouteResponse();
        response.setSegments(Arrays.asList(
                createRouteSegment(firstLeg),
                createRouteSegment(secondLeg)
        ));
        response.setTotalLegs(2);
        return response;
    }

    private RouteResponse createThreeLegRoute(TransportationDto firstLeg, TransportationDto secondLeg,
            TransportationDto thirdLeg) {
        RouteResponse response = new RouteResponse();
        response.setSegments(Arrays.asList(
                createRouteSegment(firstLeg),
                createRouteSegment(secondLeg),
                createRouteSegment(thirdLeg)
        ));
        response.setTotalLegs(3);
        return response;
    }

    private RouteSegment createRouteSegment(TransportationDto transportation) {
        RouteSegment segment = new RouteSegment();
        segment.setOriginLocation(locationMapper.toResponse(transportation.getOriginLocation()));
        segment.setDestinationLocation(locationMapper.toResponse(transportation.getDestinationLocation()));
        segment.setTransportationType(transportation.getTransportationType());
        return segment;
    }
} 