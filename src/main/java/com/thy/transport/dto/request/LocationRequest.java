package com.thy.transport.dto.request;

import lombok.Data;

@Data
public class LocationRequest {
    private String name;
    private String country;
    private String city;
    private String locationCode;
} 