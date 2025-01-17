package com.thy.transport.controller;

import com.thy.transport.dto.request.LocationRequest;
import com.thy.transport.dto.response.LocationResponse;
import com.thy.transport.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@Tag(name = "Location", description = "Location management APIs")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(summary = "Get all locations")
    @GetMapping
    public Page<LocationResponse> getAllLocations(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return locationService.getAllLocations(pageable);
    }

    @Operation(summary = "Search locations by name")
    @GetMapping("/search")
    public List<LocationResponse> searchLocationsByName(@RequestParam String name) {
        return locationService.searchLocationsByName(name);
    }

    @Operation(summary = "Get location by ID")
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Long id) {
        LocationResponse location = locationService.getLocationById(id);
        return location != null ? ResponseEntity.ok(location) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create new location")
    @PostMapping
    public LocationResponse createLocation(@RequestBody LocationRequest request) {
        return locationService.createLocation(request);
    }

    @Operation(summary = "Update location")
    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> updateLocation(@PathVariable Long id, @RequestBody LocationRequest request) {
        LocationResponse updatedLocation = locationService.updateLocation(id, request);
        return updatedLocation != null ? ResponseEntity.ok(updatedLocation) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete location")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        return locationService.deleteLocation(id) 
            ? ResponseEntity.ok().build() 
            : ResponseEntity.notFound().build();
    }
} 