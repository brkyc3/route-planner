package com.thy.transport.mapper;

import com.thy.transport.dto.request.TransportationRequest;
import com.thy.transport.dto.response.TransportationResponse;
import com.thy.transport.model.Transportation;
import com.thy.transport.service.dto.TransportationDto;
import org.mapstruct.*;
import com.thy.transport.mapper.util.Converter;

@Mapper(componentModel = "spring", uses = {LocationMapper.class}, imports = { Converter.class })
public interface TransportationMapper {

    @Mapping(target = "originLocation", ignore = true)
    @Mapping(target = "destinationLocation", ignore = true)
    @Mapping(target = "operatingDays", expression = "java(Converter.mapListToString(request.getOperatingDays()))")
    Transportation toEntity(TransportationRequest request);

    @Mapping(target = "originLocation", source = "originLocation")
    @Mapping(target = "destinationLocation", source = "destinationLocation")
    TransportationResponse toResponse(TransportationDto source);

    @Mapping(target = "originLocation", source = "originLocation")
    @Mapping(target = "destinationLocation", source = "destinationLocation")
    @Mapping(target = "operatingDays", expression = "java(Converter.mapStringToList(transportation.getOperatingDays()))")
    TransportationResponse toResponse(Transportation transportation);

    @Mapping(target = "originLocation", source = "originLocation")
    @Mapping(target = "destinationLocation", source = "destinationLocation")
    @Mapping(target = "operatingDays", expression = "java(Converter.mapStringToList(transportation.getOperatingDays()))")
    TransportationDto toDto(Transportation transportation);

    @Mapping(target = "originLocation", ignore = true)
    @Mapping(target = "destinationLocation", ignore = true)
    @Mapping(target = "operatingDays", expression = "java(Converter.mapListToString(request.getOperatingDays()))")
    void updateEntityFromRequest(TransportationRequest request, @MappingTarget Transportation transportation);
} 