package com.thy.transport.service;

import com.thy.transport.dto.request.LocationRequest;
import com.thy.transport.dto.response.LocationResponse;
import com.thy.transport.exception.BusinessException;
import com.thy.transport.mapper.LocationMapper;
import com.thy.transport.model.Location;
import com.thy.transport.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public Page<LocationResponse> getAllLocations(Pageable pageable) {
        return locationRepository.findAll(pageable)
                .map(locationMapper::toResponse);
    }

    public List<LocationResponse> searchLocationsByName(String name) {
        List<Location> locations = locationRepository.findTop10ByNameContainingIgnoreCase(name);
        return locations.stream()
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
                .orElseThrow(()->new BusinessException("Not Valid request"));

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