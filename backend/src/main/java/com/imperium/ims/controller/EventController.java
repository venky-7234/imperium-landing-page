package com.imperium.ims.controller;

import com.imperium.ims.dto.ApiResponse;
import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.dto.EventRequest;
import com.imperium.ims.dto.EventResponse;
import com.imperium.ims.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event Management API")
public class EventController {

    private final EventService eventService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Get all events with pagination")
    public ResponseEntity<ApiResponse<PageResponse<EventResponse>>> getAllEvents(
            @PageableDefault(size = 100) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(eventService.getAllEvents(pageable)));
    }

    @GetMapping("/{publicId}")
    @Operation(summary = "Get event by ID")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EventResponse>> getEventById(@PathVariable UUID publicId) {
        return ResponseEntity.ok(ApiResponse.success(eventService.getEventById(publicId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create a new event")
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Event created successfully", response));
    }

    @PutMapping("/{publicId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update an existing event")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable UUID publicId,
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully", eventService.updateEvent(publicId, request)));
    }

    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete an event (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable UUID publicId) {
        eventService.deleteEvent(publicId);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully", null));
    }

    @PutMapping("/{publicId}/publish")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Publish an event")
    public ResponseEntity<ApiResponse<Void>> publishEvent(@PathVariable UUID publicId) {
        eventService.publishEvent(publicId);
        return ResponseEntity.ok(ApiResponse.success("Event published successfully", null));
    }

    @PutMapping("/{publicId}/cancel")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Cancel an event")
    public ResponseEntity<ApiResponse<Void>> cancelEvent(@PathVariable UUID publicId) {
        eventService.cancelEvent(publicId);
        return ResponseEntity.ok(ApiResponse.success("Event cancelled successfully", null));
    }
    
    @PutMapping("/{publicId}/archive")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Archive an event")
    public ResponseEntity<ApiResponse<Void>> archiveEvent(@PathVariable UUID publicId) {
        eventService.archiveEvent(publicId);
        return ResponseEntity.ok(ApiResponse.success("Event archived successfully", null));
    }
    
    @PutMapping("/{publicId}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Deactivate an event")
    public ResponseEntity<ApiResponse<Void>> deactivateEvent(@PathVariable UUID publicId) {
        eventService.deactivateEvent(publicId);
        return ResponseEntity.ok(ApiResponse.success("Event deactivated successfully", null));
    }
}
