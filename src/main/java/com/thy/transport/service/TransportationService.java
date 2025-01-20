package com.thy.transport.service;

import com.thy.transport.dto.request.TransportationRequest;
import com.thy.transport.dto.response.TransportationResponse;
import com.thy.transport.exception.BusinessException;
import com.thy.transport.mapper.TransportationMapper;
import com.thy.transport.model.Location;
import com.thy.transport.model.Transportation;
import com.thy.transport.repository.LocationRepository;
import com.thy.transport.repository.TransportationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransportationService {
    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;
    private final TransportationMapper transportationMapper;

    public Page<TransportationResponse> getAllTransportations(Pageable pageable) {
        return transportationRepository.findAllWithLocationsAndOperatingDays(pageable)
                .map(transportationMapper::toResponse);
    }

    public TransportationResponse getTransportationById(Long id) {
        return transportationRepository.findById(id)
                .map(transportationMapper::toResponse)
                .orElseThrow(()-> new BusinessException("Not found", HttpStatus.NOT_FOUND));
    }

    public TransportationResponse createTransportation(TransportationRequest request) {
        Transportation transportation = transportationMapper.toEntity(request);
        setLocations(transportation, request);
        Transportation savedTransportation = transportationRepository.save(transportation);
        return transportationMapper.toResponse(savedTransportation);
    }

    public TransportationResponse updateTransportation(Long id, TransportationRequest request) {
        return transportationRepository.findById(id)
                .map(transportation -> {
                    String oldOriginCode = transportation.getOriginLocation().getLocationCode();
                    transportationMapper.updateEntityFromRequest(request, transportation);
                    setLocations(transportation, request);
                    Transportation updatedTransportation = transportationRepository.save(transportation);
                    return transportationMapper.toResponse(updatedTransportation);
                })
                .orElseThrow(()-> new BusinessException("Not found", HttpStatus.NOT_FOUND));
    }

    public boolean deleteTransportation(Long id) {
        return transportationRepository.findById(id)
                .map(transportation -> {
                    String originCode = transportation.getOriginLocation().getLocationCode();
                    transportationRepository.delete(transportation);
                    return true;
                })
                .orElse(false);
    }

    private void setLocations(Transportation transportation, TransportationRequest request) {
        // Validate and set origin location
        Location originLocation = locationRepository.findByLocationCode(request.getOriginLocationCode())
                .orElseThrow(() -> new EntityNotFoundException("Origin location not found for code: " + request.getOriginLocationCode()));
        transportation.setOriginLocation(originLocation);

        // Validate and set destination location
        Location destinationLocation = locationRepository.findByLocationCode(request.getDestinationLocationCode())
                .orElseThrow(() -> new EntityNotFoundException("Destination location not found for code: " + request.getDestinationLocationCode()));
        transportation.setDestinationLocation(destinationLocation);
    }
} 