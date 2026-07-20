package com.imperium.ims.service.impl;

import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.common.enums.Status;
import com.imperium.ims.dto.EventRequest;
import com.imperium.ims.dto.EventResponse;
import com.imperium.ims.entity.Event;
import com.imperium.ims.mapper.EventMapper;
import com.imperium.ims.repository.EventRepository;
import com.imperium.ims.service.EventService;
import com.imperium.ims.exception.ResourceNotFoundException;
import com.imperium.ims.repository.UserRepository;
import com.imperium.ims.entity.User;
import com.imperium.ims.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EventResponse> getAllEvents(Pageable pageable) {
        Page<EventResponse> page = eventRepository.findAll(pageable).map(eventMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID publicId) {
        Event event = eventRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "publicId", publicId));
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EventResponse> getEventsByOrganizer(UUID organizerId, Pageable pageable) {
        Page<EventResponse> page = eventRepository
                .findByOrganizerPublicIdAndDeletedFalse(organizerId, pageable)
                .map(eventMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    public EventResponse createEvent(EventRequest request) {
        String currentUserEmail = com.imperium.ims.util.SecurityUtils.getCurrentUser()
                .map(com.imperium.ims.security.UserPrincipal::getEmail)
                .orElseThrow(() -> new com.imperium.ims.exception.BusinessException("User not authenticated"));
        User organizer = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUserEmail));
                
        Event event = eventMapper.toEntity(request);
        event.setOrganizer(organizer);
        event.setStatus(Status.ACTIVE);
        event = eventRepository.save(event);
        
        auditService.log(currentUserEmail, "EVENT_CREATED", "EVENT", event.getPublicId().toString(), "Created event " + event.getTitle(), "SUCCESS");
        
        return eventMapper.toResponse(event);
    }

    @Override
    public EventResponse updateEvent(UUID publicId, EventRequest request) {
        Event event = eventRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "publicId", publicId));
        eventMapper.updateEntity(request, event);
        return eventMapper.toResponse(eventRepository.save(event));
    }

    @Override
    public void deleteEvent(UUID publicId) {
        Event event = eventRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "publicId", publicId));
        event.setDeleted(true);
        eventRepository.save(event);
        
        String currentUserEmail = com.imperium.ims.util.SecurityUtils.getCurrentUser()
                .map(com.imperium.ims.security.UserPrincipal::getEmail)
                .orElseThrow(() -> new com.imperium.ims.exception.BusinessException("User not authenticated"));
        auditService.log(currentUserEmail, "EVENT_DELETED", "EVENT", event.getPublicId().toString(), "Deleted event " + event.getTitle(), "SUCCESS");
    }

    @Override
    public void publishEvent(UUID publicId) {
        Event event = eventRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "publicId", publicId));
        event.setStatus(Status.ACTIVE);
        eventRepository.save(event);
    }

    @Override
    public void cancelEvent(UUID publicId) {
        Event event = eventRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "publicId", publicId));
        event.setStatus(Status.INACTIVE);
        eventRepository.save(event);
    }
    
    @Override
    public void archiveEvent(UUID publicId) {
        Event event = eventRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "publicId", publicId));
        event.setStatus(Status.ARCHIVED);
        eventRepository.save(event);
    }
    
    @Override
    public void deactivateEvent(UUID publicId) {
        Event event = eventRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "publicId", publicId));
        event.setStatus(Status.INACTIVE);
        eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EventResponse> searchEvents(String query, Pageable pageable) {
        Page<EventResponse> page = eventRepository.searchEvents(query, pageable).map(eventMapper::toResponse);
        return PageResponse.of(page);
    }
}
