package com.thy.transport.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RouteSearchRequest {
    private String originLocationCode;
    private String destinationLocationCode;
    private LocalDate travelDate;
} 