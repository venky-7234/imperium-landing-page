package com.imperium.ims.mapper;

import com.imperium.ims.dto.EventRequest;
import com.imperium.ims.dto.EventResponse;
import com.imperium.ims.entity.Event;
import org.mapstruct.*;

/**
 * MapStruct mapper for {@link Event} ↔ DTO conversions.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface EventMapper {

    @Mapping(target = "organizerId",   source = "organizer.id")
    @Mapping(target = "organizerName", expression = "java(event.getOrganizer() != null ? event.getOrganizer().getFirstName() + ' ' + event.getOrganizer().getLastName() : null)")
    EventResponse toResponse(Event event);

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "organizer", ignore = true)   // Set by service
    @Mapping(target = "status",    ignore = true)
    Event toEntity(EventRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "organizer", ignore = true)
    void updateEntity(EventRequest request, @MappingTarget Event event);
}
