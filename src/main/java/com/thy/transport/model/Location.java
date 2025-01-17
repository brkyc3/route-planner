package com.thy.transport.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "locations", indexes = {
        @Index(name = "idx_location_code", columnList = "location_code"),
        @Index(name = "idx_name", columnList = "name")
})
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, unique = true)
    private String locationCode;
} 