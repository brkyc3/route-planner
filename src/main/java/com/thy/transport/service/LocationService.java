package com.thy.transport.service;

import com.thy.transport.dto.request.LocationRequest;
import com.thy.transport.dto.response.LocationResponse;
import com.thy.transport.mapper.LocationMapper;
import com.thy.transport.model.Location;
import com.thy.transport.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    public List<LocationResponse> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationMapper::toResponse)
                .collect(Collectors.toList());
    }

    public LocationResponse getLocationById(Long id) {
        return locationRepository.findById(id)
                .map(locationMapper::toResponse)
                .orElse(null);
    }

    public LocationResponse createLocation(LocationRequest request) {
        Location location = locationMapper.toEntity(request);
        Location savedLocation = locationRepository.save(location);
        return locationMapper.toResponse(savedLocation);
    }

    public LocationResponse updateLocation(Long id, LocationRequest request) {
        return locationRepository.findById(id)
                .map(location -> {
                    locationMapper.updateEntityFromRequest(request, location);
                    Location updatedLocation = locationRepository.save(location);
                    return locationMapper.toResponse(updatedLocation);
                })
                .orElse(null);
    }

    public boolean deleteLocation(Long id) {
        return locationRepository.findById(id)
                .map(location -> {
                    locationRepository.delete(location);
                    return true;
                })
                .orElse(false);
    }
} 