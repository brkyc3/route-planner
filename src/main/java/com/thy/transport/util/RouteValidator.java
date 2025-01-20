package com.thy.transport.util;

import com.thy.transport.model.TransportationType;
import com.thy.transport.service.dto.TransportationDto;

import java.util.List;

public class RouteValidator {

    public static boolean isValidTransportationPattern(List<TransportationType> path) {
        // Single leg must be a flight
        if (path.size() == 1) {
            return TransportationType.FLIGHT.equals(path.get(0));
        }

        // Two legs: must be flight->other or other->flight
        if (path.size() == 2) {
            boolean firstIsFlight = TransportationType.FLIGHT.equals(path.get(0));
            boolean secondIsFlight = TransportationType.FLIGHT.equals(path.get(1));
            return firstIsFlight != secondIsFlight; // XOR - one must be flight, one must not be
        }

        // Three legs: must be other->flight->other
        return !TransportationType.FLIGHT.equals(path.get(0)) && TransportationType.FLIGHT.equals(path.get(1)) && !TransportationType.FLIGHT.equals(path.get(2));
    }

    public static boolean couldLeadToValidPath(List<TransportationType> currentPath) {
        if (currentPath.size() == 2) {
            return TransportationType.FLIGHT.equals(currentPath.get(1)) && !TransportationType.FLIGHT.equals(currentPath.get(0));
        } else {
            return true;
        }
    }

} 