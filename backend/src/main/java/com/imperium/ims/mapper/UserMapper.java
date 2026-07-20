package com.imperium.ims.mapper;

import com.imperium.ims.dto.CreateUserRequest;
import com.imperium.ims.dto.UpdateUserRequest;
import com.imperium.ims.dto.UserResponse;
import com.imperium.ims.entity.User;
import org.mapstruct.*;

/**
 * MapStruct mapper for {@link User} ↔ DTO conversions.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface UserMapper {

    @Mapping(target = "assignedEventIds", source = "assignedEvents")
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)        // Password encoded separately
    @Mapping(target = "roles", ignore = true)           // Roles resolved separately
    @Mapping(target = "assignedEvents", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "emailVerified", constant = "false")
    User toEntity(CreateUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)        // Username is immutable
    @Mapping(target = "email", ignore = true)           // Email changed via separate flow
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "assignedEvents", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);

    default java.util.Set<java.util.UUID> mapEvents(java.util.Set<com.imperium.ims.entity.Event> events) {
        if (events == null) return null;
        return events.stream().map(com.imperium.ims.entity.BaseEntity::getPublicId).collect(java.util.stream.Collectors.toSet());
    }
}
