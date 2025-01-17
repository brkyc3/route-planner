package com.thy.transport.dto.response;

import lombok.Data;

@Data
public class LocationResponse {
    private Long id;
    private String name;
    private String country;
    private String city;
    private String locationCode;
} 