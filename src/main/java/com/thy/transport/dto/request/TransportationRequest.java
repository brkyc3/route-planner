package com.thy.transport.dto.request;

import com.thy.transport.model.TransportationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;
import java.util.SortedSet;

@Data
public class TransportationRequest {

    @NotNull(message = "Origin location code cannot be null")
    @Size(min = 3, max = 5, message = "Origin location code length must be between 3 and 5")
    private String originLocationCode;

    @NotNull(message = "Destination location code cannot be null")
    @Size(min = 3, max = 5, message = "Destination location code length must be between 3 and 5")
    private String destinationLocationCode;

    @NotNull(message = "Transportation type cannot be null")
    private TransportationType transportationType;

    @NotEmpty(message = "Operating days cannot be empty" )
    private SortedSet<Integer> operatingDays;
} 