package com.thy.transport.service.dto;

import com.thy.transport.model.Location;
import com.thy.transport.model.TransportationType;
import lombok.Data;

import java.util.SortedSet;

@Data
public class TransportationDto {

    private Long id;

    private Location originLocation;

    private Location destinationLocation;

    private TransportationType transportationType;

    private SortedSet<Integer> operatingDays;
} 