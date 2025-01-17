package com.thy.transport.mapper;

import com.thy.transport.dto.request.LocationRequest;
import com.thy.transport.dto.response.LocationResponse;
import com.thy.transport.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    
    Location toEntity(LocationRequest request);
    
    LocationResponse toResponse(Location location);
    
    void updateEntityFromRequest(LocationRequest request, @MappingTarget Location location);
} 