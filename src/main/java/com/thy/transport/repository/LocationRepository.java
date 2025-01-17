package com.thy.transport.repository;

import com.thy.transport.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByLocationCode(String locationCode);

    Page<Location> findAll(Pageable pageable);

    List<Location> findTop10ByNameContainingIgnoreCase(String name);
} 