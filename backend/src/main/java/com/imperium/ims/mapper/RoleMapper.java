package com.imperium.ims.mapper;

import com.imperium.ims.dto.RoleRequest;
import com.imperium.ims.dto.RoleResponse;
import com.imperium.ims.entity.Role;
import org.mapstruct.*;

/**
 * MapStruct mapper for {@link Role} ↔ DTO conversions.
 *
 * <p>{@code componentModel = "spring"} is set globally via the Maven compiler arg
 * {@code -Amapstruct.defaultComponentModel=spring}, so MapStruct generates a
 * Spring-managed bean automatically.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface RoleMapper {

    RoleResponse toResponse(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "systemRole", ignore = true)
    Role toEntity(RoleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(RoleRequest request, @MappingTarget Role role);
}
