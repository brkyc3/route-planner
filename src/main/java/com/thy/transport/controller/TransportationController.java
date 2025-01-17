package com.thy.transport.controller;

import com.thy.transport.dto.request.TransportationRequest;
import com.thy.transport.dto.response.TransportationResponse;
import com.thy.transport.service.TransportationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportations")
@Tag(name = "Transportation", description = "Transportation management APIs")
public class TransportationController {
    private final TransportationService transportationService;

    public TransportationController(TransportationService transportationService) {
        this.transportationService = transportationService;
    }

    @Operation(summary = "Get all transportations")
    @GetMapping
    public Page<TransportationResponse> getAllTransportations(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return transportationService.getAllTransportations(pageable);
    }

    @Operation(summary = "Get transportation by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TransportationResponse> getTransportationById(@PathVariable Long id) {
        TransportationResponse transportation = transportationService.getTransportationById(id);
        return transportation != null ? ResponseEntity.ok(transportation) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create new transportation")
    @PostMapping
    public TransportationResponse createTransportation(@RequestBody TransportationRequest request) {
        return transportationService.createTransportation(request);
    }

    @Operation(summary = "Update transportation")
    @PutMapping("/{id}")
    public ResponseEntity<TransportationResponse> updateTransportation(@PathVariable Long id, @RequestBody TransportationRequest request) {
        TransportationResponse updatedTransportation = transportationService.updateTransportation(id, request);
        return updatedTransportation != null ? ResponseEntity.ok(updatedTransportation) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete transportation")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransportation(@PathVariable Long id) {
        return transportationService.deleteTransportation(id) 
            ? ResponseEntity.ok().build() 
            : ResponseEntity.notFound().build();
    }
} 