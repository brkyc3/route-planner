package com.thy.transport.dto.response;

import com.thy.transport.model.TransportationType;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

@Data
public class RouteSegment {
    private LocationResponse originLocation;
    private LocationResponse destinationLocation;
    private TransportationType transportationType;
}