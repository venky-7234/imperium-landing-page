package com.imperium.ims.mapper;

import com.imperium.ims.dto.RoleRequest;
import com.imperium.ims.dto.RoleResponse;
import com.imperium.ims.entity.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T20:16:55+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleResponse toResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleResponse roleResponse = new RoleResponse();

        roleResponse.setId( role.getId() );
        roleResponse.setName( role.getName() );
        roleResponse.setDescription( role.getDescription() );
        roleResponse.setSystemRole( role.isSystemRole() );
        roleResponse.setCreatedAt( role.getCreatedAt() );
        roleResponse.setUpdatedAt( role.getUpdatedAt() );

        return roleResponse;
    }

    @Override
    public Role toEntity(RoleRequest request) {
        if ( request == null ) {
            return null;
        }

        Role role = new Role();

        role.setName( request.getName() );
        role.setDescription( request.getDescription() );

        return role;
    }

    @Override
    public void updateEntity(RoleRequest request, Role role) {
        if ( request == null ) {
            return;
        }

        if ( request.getName() != null ) {
            role.setName( request.getName() );
        }
        if ( request.getDescription() != null ) {
            role.setDescription( request.getDescription() );
        }
    }
}
