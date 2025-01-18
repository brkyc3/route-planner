package com.thy.transport.service;

import com.thy.transport.dto.request.RouteSearchRequest;
import com.thy.transport.dto.response.RouteResponse;
import com.thy.transport.mapper.LocationMapper;
import com.thy.transport.mapper.TransportationMapper;
import com.thy.transport.model.Location;
import com.thy.transport.model.Transportation;
import com.thy.transport.model.TransportationType;
import com.thy.transport.repository.TransportationRepository;
import com.thy.transport.service.dto.TransportationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private TransportationRepository transportationRepository;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private TransportationMapper transportationMapper;

    @Mock
    private CacheService cacheService;


    @InjectMocks
    private RouteService routeService;

    private Location locationA;
    private Location locationB;
    private Location locationC;
    private Location locationD;
    private Location locationE;

    @BeforeEach
    void setUp() {

        locationA = new Location();
        locationA.setLocationCode("A");
        locationB = new Location();
        locationB.setLocationCode("B");
        locationC = new Location();
        locationC.setLocationCode("C");
        locationD = new Location();
        locationD.setLocationCode("D");
        locationE = new Location();
        locationE.setLocationCode("E");

    }

    @Test
    void shouldFindDirectFlightRoute() {
        // Arrange
        RouteSearchRequest request = createRouteRequest("B", "C", LocalDate.of(2024, 3, 1));

        Transportation flightBC = createTransportation(locationB, locationC, TransportationType.FLIGHT);

        TransportationDto flightBCDto = createTransportationDto(locationB, locationC, TransportationType.FLIGHT);

        when(transportationRepository.findByOriginCode("B")).thenReturn(List.of(flightBC));
        when(transportationMapper.toDto(flightBC)).thenReturn(flightBCDto);

        // Act
        List<RouteResponse> routes = routeService.searchRoutes(request);

        // Assert
        assertThat(routes).hasSize(1);
        assertThat(routes.get(0).getTotalLegs()).isEqualTo(1);
        assertThat(routes.get(0).getSegments().get(0).getTransportationType()).isEqualTo(TransportationType.FLIGHT);
    }

    @Test
    void shouldFindTwoLegRoute() {
        // Arrange
        RouteSearchRequest request = createRouteRequest("A", "C", LocalDate.of(2024, 3, 1));

        Transportation uberAB = createTransportation(locationA, locationB, TransportationType.UBER);
        Transportation flightBC = createTransportation(locationB, locationC, TransportationType.FLIGHT);

        TransportationDto uberABDto = createTransportationDto(locationA, locationB, TransportationType.UBER);
        TransportationDto flightBCDto = createTransportationDto(locationB, locationC, TransportationType.FLIGHT);

        when(transportationRepository.findByOriginCode("A")).thenReturn(List.of(uberAB));
        when(transportationMapper.toDto(uberAB)).thenReturn(uberABDto);

        when(transportationRepository.findByOriginCode("B")).thenReturn(List.of(flightBC));
        when(transportationMapper.toDto(flightBC)).thenReturn(flightBCDto);

        // Act
        List<RouteResponse> routes = routeService.searchRoutes(request);

        // Assert
        assertThat(routes).hasSize(1);
        assertThat(routes.get(0).getTotalLegs()).isEqualTo(2);
        assertThat(routes.get(0).getSegments().get(0).getTransportationType()).isEqualTo(TransportationType.UBER);
        assertThat(routes.get(0).getSegments().get(1).getTransportationType()).isEqualTo(TransportationType.FLIGHT);
    }

    @Test
    void shouldFindThreeLegRoute() {
        // Arrange
        RouteSearchRequest request = createRouteRequest("A", "D", LocalDate.of(2024, 3, 1));

        // Set up transportation entities
        Transportation uberAB = createTransportation(locationA, locationB, TransportationType.UBER);
        Transportation flightBC = createTransportation(locationB, locationC, TransportationType.FLIGHT);
        Transportation busCD = createTransportation(locationC, locationD, TransportationType.BUS);

        // Set up transportation DTOs
        TransportationDto uberABDto = createTransportationDto(locationA, locationB, TransportationType.UBER);
        TransportationDto flightBCDto = createTransportationDto(locationB, locationC, TransportationType.FLIGHT);
        TransportationDto busCDDto = createTransportationDto(locationC, locationD, TransportationType.BUS);

        when(transportationRepository.findByOriginCode("A")).thenReturn(List.of(uberAB));
        when(transportationMapper.toDto(uberAB)).thenReturn(uberABDto);

        when(transportationRepository.findByOriginCode("B")).thenReturn(List.of(flightBC));
        when(transportationMapper.toDto(flightBC)).thenReturn(flightBCDto);

        when(transportationRepository.findNonFlightsByDestinationCode("D")).thenReturn(List.of(busCD));
        when(transportationMapper.toDto(busCD)).thenReturn(busCDDto);

        // Act
        List<RouteResponse> routes = routeService.searchRoutes(request);

        // Assert
        assertThat(routes).hasSize(1);
        assertThat(routes.get(0).getTotalLegs()).isEqualTo(3);
        assertThat(routes.get(0).getSegments().get(0).getTransportationType()).isEqualTo(TransportationType.UBER);
        assertThat(routes.get(0).getSegments().get(1).getTransportationType()).isEqualTo(TransportationType.FLIGHT);
        assertThat(routes.get(0).getSegments().get(2).getTransportationType()).isEqualTo(TransportationType.BUS);
    }

    @Test
    void shouldReturnEmptyListWhenNoRoutesFound() {
        // Arrange
        RouteSearchRequest request = createRouteRequest("A", "E", LocalDate.of(2024, 3, 1));

        // Set up transportation entities
        Transportation uberAB = createTransportation(locationA, locationB, TransportationType.UBER);
        Transportation flightBC = createTransportation(locationB, locationC, TransportationType.FLIGHT);
        Transportation busCD = createTransportation(locationB, locationC, TransportationType.BUS);

        // Set up transportation DTOs
        TransportationDto uberABDto = createTransportationDto(locationA, locationB, TransportationType.UBER);
        TransportationDto flightBCDto = createTransportationDto(locationB, locationC, TransportationType.FLIGHT);
        TransportationDto busCDDto = createTransportationDto(locationC, locationD, TransportationType.BUS);

        when(transportationRepository.findByOriginCode("A")).thenReturn(List.of(uberAB));
        when(transportationMapper.toDto(uberAB)).thenReturn(uberABDto);

        when(transportationRepository.findByOriginCode("B")).thenReturn(List.of(flightBC));
        when(transportationMapper.toDto(flightBC)).thenReturn(flightBCDto);

        when(transportationRepository.findNonFlightsByDestinationCode("E")).thenReturn(Collections.emptyList());

        // Act
        List<RouteResponse> routes = routeService.searchRoutes(request);

        // Assert
        assertThat(routes).isEmpty();
    }

    @Test
    void shouldNotFindRouteWithNoFlight() {
        // Arrange
        RouteSearchRequest request = createRouteRequest("A", "D", LocalDate.of(2024, 3, 1));

        // Set up transportation entities
        Transportation uberAB = createTransportation(locationA, locationB, TransportationType.UBER);
        Transportation subwayBC = createTransportation(locationB, locationC, TransportationType.SUBWAY);
        Transportation busCD = createTransportation(locationB, locationC, TransportationType.BUS);

        // Set up transportation DTOs
        TransportationDto uberABDto = createTransportationDto(locationA, locationB, TransportationType.UBER);
        TransportationDto subwayBCDto = createTransportationDto(locationB, locationC, TransportationType.SUBWAY);
        TransportationDto busCDDto = createTransportationDto(locationC, locationD, TransportationType.BUS);

        when(transportationRepository.findByOriginCode("A")).thenReturn(List.of(uberAB));
        when(transportationMapper.toDto(uberAB)).thenReturn(uberABDto);

        when(transportationRepository.findByOriginCode("B")).thenReturn(List.of(subwayBC));
        when(transportationMapper.toDto(subwayBC)).thenReturn(subwayBCDto);

        when(transportationRepository.findNonFlightsByDestinationCode("D")).thenReturn(List.of(busCD));
        when(transportationMapper.toDto(busCD)).thenReturn(busCDDto);

        // Act
        List<RouteResponse> routes = routeService.searchRoutes(request);

        // Assert
        assertThat(routes).isEmpty();
    }

    @Test
    void shouldNotFindRouteWithMultipleFlights() {
        // Arrange
        RouteSearchRequest request = createRouteRequest("A", "D", LocalDate.of(2024, 3, 1));

        // Set up transportation entities
        Transportation uberAB = createTransportation(locationA, locationB, TransportationType.UBER);
        Transportation flightBC = createTransportation(locationB, locationC, TransportationType.FLIGHT);
        Transportation flightCD = createTransportation(locationC, locationD, TransportationType.FLIGHT);

        // Set up transportation DTOs
        TransportationDto uberABDto = createTransportationDto(locationA, locationB, TransportationType.UBER);
        TransportationDto flightBCDto = createTransportationDto(locationB, locationC, TransportationType.FLIGHT);
        TransportationDto flightCDDto = createTransportationDto(locationC, locationD, TransportationType.FLIGHT);

        when(transportationRepository.findByOriginCode("A")).thenReturn(List.of(uberAB));
        when(transportationMapper.toDto(uberAB)).thenReturn(uberABDto);

        when(transportationRepository.findByOriginCode("B")).thenReturn(List.of(flightBC));
        when(transportationMapper.toDto(flightBC)).thenReturn(flightBCDto);

        when(transportationRepository.findNonFlightsByDestinationCode("D")).thenReturn(List.of(flightCD));
        when(transportationMapper.toDto(flightCD)).thenReturn(flightCDDto);

        // Act
        List<RouteResponse> routes = routeService.searchRoutes(request);

        // Assert
        assertThat(routes).isEmpty();
    }

    @Test
    void shouldNotFindRouteWithMultiplePreFlightTransfers() {
        // Arrange
        RouteSearchRequest request = createRouteRequest("A", "D", LocalDate.of(2024, 3, 1));
        Transportation busAB = createTransportation(locationA, locationB, TransportationType.BUS);
        Transportation uberBC = createTransportation(locationB, locationC, TransportationType.UBER);
        Transportation flightCD = createTransportation(locationC, locationD, TransportationType.FLIGHT);
        
        TransportationDto busABDto = createTransportationDto(locationA, locationB, TransportationType.BUS);
        TransportationDto uberBCDto = createTransportationDto(locationB, locationC, TransportationType.UBER);
        TransportationDto flightCDDto = createTransportationDto(locationC, locationD, TransportationType.FLIGHT);

        when(transportationRepository.findByOriginCode("A")).thenReturn(List.of(busAB));
        when(transportationMapper.toDto(busAB)).thenReturn(busABDto);
        when(transportationRepository.findByOriginCode("B")).thenReturn(List.of(uberBC));
        when(transportationMapper.toDto(uberBC)).thenReturn(uberBCDto);
        when(transportationRepository.findNonFlightsByDestinationCode("D")).thenReturn(List.of(flightCD));
        when(transportationMapper.toDto(flightCD)).thenReturn(flightCDDto);

        // Act
        List<RouteResponse> routes = routeService.searchRoutes(request);

        // Assert
        assertThat(routes).isEmpty();
    }

    @Test
    void shouldNotFindRouteWithMultiplePostFlightTransfers() {
        // Arrange
        RouteSearchRequest request = createRouteRequest("A", "D", LocalDate.of(2024, 3, 1));
        Transportation flightAB = createTransportation(locationA, locationB, TransportationType.FLIGHT);
        Transportation uberBC = createTransportation(locationB, locationC, TransportationType.UBER);
        Transportation subwayCD = createTransportation(locationC, locationD, TransportationType.SUBWAY);

        TransportationDto flightABDto = createTransportationDto(locationA, locationB, TransportationType.FLIGHT);
        TransportationDto uberBCDto = createTransportationDto(locationB, locationC, TransportationType.UBER);
        TransportationDto subwayCDDto = createTransportationDto(locationC, locationD, TransportationType.SUBWAY);

        when(transportationRepository.findByOriginCode("A")).thenReturn(List.of(flightAB));
        when(transportationMapper.toDto(flightAB)).thenReturn(flightABDto);
        when(transportationRepository.findByOriginCode("B")).thenReturn(List.of(uberBC));
        when(transportationMapper.toDto(uberBC)).thenReturn(uberBCDto);
        when(transportationRepository.findNonFlightsByDestinationCode("D")).thenReturn(List.of(subwayCD));
        when(transportationMapper.toDto(subwayCD)).thenReturn(subwayCDDto);

        // Act
        List<RouteResponse> routes = routeService.searchRoutes(request);

        // Assert
        assertThat(routes).isEmpty();
    }


    private RouteSearchRequest createRouteRequest(String origin, String destination, LocalDate date) {
        RouteSearchRequest request = new RouteSearchRequest();
        request.setOriginLocationCode(origin);
        request.setDestinationLocationCode(destination);
        request.setTravelDate(date);
        return request;
    }

    private Transportation createTransportation(Location origin, Location destination, 
            TransportationType type, Set<Integer> operatingDays) {
        Transportation transportation = new Transportation();
        transportation.setOriginLocation(origin);
        transportation.setDestinationLocation(destination);
        transportation.setTransportationType(type);
        transportation.setOperatingDays(operatingDays.toString());
        return transportation;
    }

    private Transportation createTransportation(Location origin, Location destination, TransportationType type) {
        return createTransportation(origin,destination,type,Set.of(1, 2, 3, 4, 5));
    }

    private TransportationDto createTransportationDto(Location origin, Location destination,
            TransportationType type, Set<Integer> operatingDays) {
        TransportationDto dto = new TransportationDto();
        dto.setOriginLocation(origin);
        dto.setDestinationLocation(destination);
        dto.setTransportationType(type);
        dto.setOperatingDays(new TreeSet<>(operatingDays));
        return dto;
    }
    private TransportationDto createTransportationDto(Location origin, Location destination, TransportationType type) {
        return createTransportationDto(origin,destination,type,Set.of(1, 2, 3, 4, 5));
    }
} 