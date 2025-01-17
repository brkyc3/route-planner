package com.thy.transport.repository;

import com.thy.transport.config.Constants;
import com.thy.transport.model.Transportation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {

    @Query("SELECT t FROM Transportation t " +
            "JOIN FETCH t.originLocation " +
            "JOIN FETCH t.destinationLocation ")
    Page<Transportation> findAllWithLocationsAndOperatingDays(Pageable pageable);

    @Cacheable(value = Constants.RedisCacheNames.TRANSPORTATION_BY_ORIGIN, key = "#originCode")
    @Query("SELECT t FROM Transportation t " +
            "JOIN FETCH t.originLocation " +
            "JOIN FETCH t.destinationLocation " +
            "WHERE t.originLocation.locationCode = :originCode")
    List<Transportation> findByOriginCode(String originCode);

} 