package com.thy.transport.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class RouteResponse {
    private List<RouteSegment> segments;
    private int totalLegs;
} 