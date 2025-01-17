package com.thy.transport.util;

import com.thy.transport.model.TransportationType;
import com.thy.transport.service.dto.TransportationDto;

import java.util.List;

public class RouteValidator {
    
    public static boolean isValidRoutePattern(List<TransportationDto> route) {
        if (route == null || route.isEmpty() || route.size() > 3) {
            return false;
        }

        // Single leg: must be FLIGHT
        if (route.size() == 1) {
            return TransportationType.FLIGHT.equals(route.get(0).getTransportationType());
        }

        // Two legs
        if (route.size() == 2) {
            TransportationDto first = route.get(0);
            TransportationDto second = route.get(1);

            // Either (non-FLIGHT → FLIGHT) or (FLIGHT → non-FLIGHT)
            return (TransportationType.FLIGHT.equals(first.getTransportationType()) 
                    && !TransportationType.FLIGHT.equals(second.getTransportationType()))
                   ||
                   (!TransportationType.FLIGHT.equals(first.getTransportationType()) 
                    && TransportationType.FLIGHT.equals(second.getTransportationType()));
        }

        // Three legs
        TransportationDto first = route.get(0);
        TransportationDto second = route.get(1);
        TransportationDto third = route.get(2);

        // Must have exactly one FLIGHT, can be in any position
        // Case 1: FLIGHT in the middle
        if (TransportationType.FLIGHT.equals(second.getTransportationType())) {
            return !TransportationType.FLIGHT.equals(first.getTransportationType())
                    && !TransportationType.FLIGHT.equals(third.getTransportationType());
        }
        // Case 2: FLIGHT at start
        if (TransportationType.FLIGHT.equals(first.getTransportationType())) {
            return !TransportationType.FLIGHT.equals(second.getTransportationType())
                    && !TransportationType.FLIGHT.equals(third.getTransportationType());
        }
        // Case 3: FLIGHT at end
        if (TransportationType.FLIGHT.equals(third.getTransportationType())) {
            return !TransportationType.FLIGHT.equals(first.getTransportationType())
                    && !TransportationType.FLIGHT.equals(second.getTransportationType());
        }

        return false;
    }
} 