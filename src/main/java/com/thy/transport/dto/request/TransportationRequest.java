package com.thy.transport.dto.request;

import com.thy.transport.model.TransportationType;
import lombok.Data;
import java.util.List;
import java.util.SortedSet;

@Data
public class TransportationRequest {
    private String originLocationCode;
    private String destinationLocationCode;
    private TransportationType transportationType;
    private SortedSet<Integer> operatingDays;
} 