package com.imperium.ims.mapper;

import com.imperium.ims.dto.ApplicationResponse;
import com.imperium.ims.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import org.mapstruct.Builder;

/**
 * MapStruct mapper for {@link Application} ↔ DTO conversions.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface ApplicationMapper {

    @Mapping(target = "eventId", source = "event.publicId")
    @Mapping(target = "eventTitle", source = "event.title")
    @Mapping(target = "assignedAdminId", source = "assignedAdmin.publicId")
    @Mapping(target = "assignedAdminName", expression = "java(application.getAssignedAdmin() != null ? application.getAssignedAdmin().getFirstName() + ' ' + application.getAssignedAdmin().getLastName() : null)")
    ApplicationResponse toResponse(Application application);
}
