package com.thy.transport.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationRequest {
    @NotEmpty(message = "Name cannot be null")
    @Size(min = 1, max = 50,message = "Name must not be empty")
    private String name;

    @NotEmpty(message = "Country cannot be null")
    @Size(min = 1, max = 20, message = "Country must not be empty")
    private String country;

    @NotNull(message = "City cannot be null")
    @Size(min = 1, max = 20, message = "City must not be empty")
    private String city;

    @NotNull(message = "Location code cannot be null")
    @Size(min = 3, max = 5, message = "Location code must not be empty")
    private String locationCode;
} 