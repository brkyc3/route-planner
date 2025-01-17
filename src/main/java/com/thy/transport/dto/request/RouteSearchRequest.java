package com.thy.transport.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RouteSearchRequest {
    @NotNull(message = "Origin location code cannot be null")
    @Size(min = 3, max = 5, message = "Origin location code length must be between 3 and 5")
    private String originLocationCode;

    @NotNull(message = "Destination location code cannot be null")
    @Size(min = 3, max = 5, message = "Destination location code length must be between 3 and 5")
    private String destinationLocationCode;

    @Future(message = "Travel date must be in the future")
    private LocalDate travelDate;
} 