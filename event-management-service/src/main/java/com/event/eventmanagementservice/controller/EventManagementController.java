package com.event.eventmanagementservice.controller;

import com.event.eventmanagementservice.model.request.EventAddRequest;
import com.event.eventmanagementservice.model.request.GetEventsRequest;
import com.event.eventmanagementservice.model.response.EventResponse;
import com.event.eventmanagementservice.service.EventManagementService;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/event-management")
public class EventManagementController {
	private final EventManagementService eventManagementService;

	@GetMapping("/getByUsername/{organizatorUsername}")
	public Page<EventResponse> getEvents(Pageable pageable, @PathVariable String organizatorUsername) {
		return eventManagementService.getEventsByUsername(pageable, organizatorUsername);
	}

	@PostMapping("/id")
	public List<EventResponse> getEventsByIds(@RequestBody GetEventsRequest getEventsRequest){
		return eventManagementService.getEventsByIds(getEventsRequest);
	}

	@GetMapping("/getById/{id}")
	public EventResponse getEvent(@PathVariable String id) {
		return eventManagementService.getEvent(id);
	}

	@PostMapping
	public EventResponse addEvent(@RequestBody EventAddRequest eventAddRequest){
		return eventManagementService.addEvent(eventAddRequest);
	}

	@DeleteMapping("/{id}")
	public EventResponse deleteEvent(@PathVariable String id) {
		return eventManagementService.deleteEvent(id);
	}
}
