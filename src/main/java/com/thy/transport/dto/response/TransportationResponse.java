package com.thy.transport.dto.response;

import com.thy.transport.model.TransportationType;
import lombok.Data;
import java.util.List;
import java.util.SortedSet;

@Data
public class TransportationResponse {
    private Long id;
    private LocationResponse originLocation;
    private LocationResponse destinationLocation;
    private TransportationType transportationType;
    private SortedSet<Integer> operatingDays;
} 