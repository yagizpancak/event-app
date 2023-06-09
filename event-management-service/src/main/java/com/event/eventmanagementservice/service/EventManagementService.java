package com.event.eventmanagementservice.service;

import com.event.eventmanagementservice.entity.Event;
import com.event.eventmanagementservice.exception.BusinessException;
import com.event.eventmanagementservice.exception.ErrorCode;
import com.event.eventmanagementservice.topic.CreatedEvents;
import com.event.eventmanagementservice.model.request.EventAddRequest;
import com.event.eventmanagementservice.model.request.GetEventsRequest;
import com.event.eventmanagementservice.model.response.EventResponse;
import com.event.eventmanagementservice.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventManagementService {
	private final EventRepository eventRepository;
	private final KafkaTemplate<String, CreatedEvents> kafkaTemplate;

	public Page<EventResponse> getEventsByUsername(Pageable pageable, String organizatorUsername){
		return eventRepository.findByUsername(pageable, organizatorUsername).map(EventResponse::fromEntity);
	}
	public List<EventResponse> getEventsByIds(GetEventsRequest getEventsRequest) {
		return eventRepository.findAllByIds(getEventsRequest.getIdList()).stream().map(EventResponse::fromEntity).toList();
	}

	public EventResponse getEvent(String id){
		Event election = eventRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Event not found.", ErrorCode.resource_missing));
		return EventResponse.fromEntity(election);
	}
	public EventResponse addEvent(EventAddRequest eventAddRequest){
		Event event = fromRequest(new Event(), eventAddRequest);
		eventRepository.save(event);
		CreatedEvents createdEvents = new CreatedEvents(event.getId(), event.getOrganizatorUsername(), event.getUserLimit());
		kafkaTemplate.send("createdEvents", createdEvents);
		return EventResponse.fromEntity(event);
	}

	public EventResponse deleteEvent(String id) {
		Event event = eventRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Event not found.", ErrorCode.resource_missing));
		eventRepository.delete(event);
		return EventResponse.fromEntity(event);
	}

	private Event fromRequest(Event event, EventAddRequest eventAddRequest){
		event.setName(eventAddRequest.getName());
		event.setLocationX(eventAddRequest.getLocationX());
		event.setLocationY(eventAddRequest.getLocationY());
		event.setDescription(eventAddRequest.getDescription());
		event.setStartDate(eventAddRequest.getStartDate());
		event.setRegisterDueDate(eventAddRequest.getRegisterDueDate());
		event.setUserLimit(eventAddRequest.getUserLimit());
		event.setOrganizatorUsername(eventAddRequest.getOrganizatorUsername());

		return event;
	}
}
