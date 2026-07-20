package com.imperium.ims.mapper;

import com.imperium.ims.dto.CreateUserRequest;
import com.imperium.ims.dto.RoleResponse;
import com.imperium.ims.dto.UpdateUserRequest;
import com.imperium.ims.dto.UserResponse;
import com.imperium.ims.entity.Role;
import com.imperium.ims.entity.User;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-20T20:16:55+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setAssignedEventIds( mapEvents( user.getAssignedEvents() ) );
        userResponse.setId( user.getId() );
        userResponse.setUsername( user.getUsername() );
        userResponse.setEmail( user.getEmail() );
        userResponse.setFirstName( user.getFirstName() );
        userResponse.setLastName( user.getLastName() );
        userResponse.setPhoneNumber( user.getPhoneNumber() );
        userResponse.setAvatarUrl( user.getAvatarUrl() );
        userResponse.setStatus( user.getStatus() );
        userResponse.setEnabled( user.isEnabled() );
        userResponse.setEmailVerified( user.isEmailVerified() );
        userResponse.setLastLoginAt( user.getLastLoginAt() );
        userResponse.setRoles( roleSetToRoleResponseSet( user.getRoles() ) );
        userResponse.setCreatedAt( user.getCreatedAt() );
        userResponse.setUpdatedAt( user.getUpdatedAt() );

        return userResponse;
    }

    @Override
    public User toEntity(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( request.getEmail() );
        user.setFirstName( request.getFirstName() );
        user.setLastName( request.getLastName() );
        user.setPhoneNumber( request.getPhoneNumber() );

        user.setEnabled( true );
        user.setEmailVerified( false );

        return user;
    }

    @Override
    public void updateEntity(UpdateUserRequest request, User user) {
        if ( request == null ) {
            return;
        }

        if ( request.getFirstName() != null ) {
            user.setFirstName( request.getFirstName() );
        }
        if ( request.getLastName() != null ) {
            user.setLastName( request.getLastName() );
        }
        if ( request.getPhoneNumber() != null ) {
            user.setPhoneNumber( request.getPhoneNumber() );
        }
        if ( request.getAvatarUrl() != null ) {
            user.setAvatarUrl( request.getAvatarUrl() );
        }
    }

    protected RoleResponse roleToRoleResponse(Role role) {
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

    protected Set<RoleResponse> roleSetToRoleResponseSet(Set<Role> set) {
        if ( set == null ) {
            return null;
        }

        Set<RoleResponse> set1 = new LinkedHashSet<RoleResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Role role : set ) {
            set1.add( roleToRoleResponse( role ) );
        }

        return set1;
    }
}
