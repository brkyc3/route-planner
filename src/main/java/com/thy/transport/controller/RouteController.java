package com.thy.transport.controller;

import com.thy.transport.dto.request.RouteSearchRequest;
import com.thy.transport.dto.response.RouteResponse;
import com.thy.transport.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@Tag(name = "Routes", description = "Route search operations")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @Operation(summary = "Search routes between locations")
    @PostMapping("/search")
    public List<RouteResponse> searchRoutes(@RequestBody RouteSearchRequest request) {
        return routeService.searchRoutes(request);
    }
} 