package com.thy.transport.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.collection.spi.PersistentSortedSet;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.io.Serializable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@Entity
@Table(name = "transportations", indexes = {
        @Index(name = "idx_origin_location", columnList = "origin_location_id"),
        @Index(name = "idx_destination_location", columnList = "destination_location_id")
})
public class Transportation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origin_location_id")
    private Location originLocation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_location_id")
    private Location destinationLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportationType transportationType;

    @Column(nullable = false)
    private String operatingDays;
} 