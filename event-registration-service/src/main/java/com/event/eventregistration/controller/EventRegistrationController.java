package com.event.eventregistration.controller;

import com.event.eventregistration.model.request.AnswerRegistrationRequest;
import com.event.eventregistration.model.request.EventInfoAddRequest;
import com.event.eventregistration.model.request.RegistrationAddRequest;
import com.event.eventregistration.model.response.EventInfoResponse;
import com.event.eventregistration.model.response.RegisteredEventsResponse;
import com.event.eventregistration.model.response.RegisteredEventsWithStatusResponse;
import com.event.eventregistration.model.response.RegisteredUsersResponse;
import com.event.eventregistration.service.EventRegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/event-registration")
public class EventRegistrationController {
	private final EventRegistrationService eventRegistrationService;

	@GetMapping("/{id}")
	public EventInfoResponse getEventRegistration(@PathVariable String id) {
		return eventRegistrationService.getEventRegistration(id);
	}

	@GetMapping("/registered-users/{eventUUID}")
	public RegisteredUsersResponse getRegisteredUsersForAnEvent(@PathVariable String eventUUID){
		return RegisteredUsersResponse.builder()
				.registeredUsers(eventRegistrationService.getRegisteredUsersForAnEvent(eventUUID))
				.build();
	}

	@GetMapping("/registered-events/accepted/{username}")
	public RegisteredEventsResponse getEventsThatAUserRegistered(@PathVariable String username){
		return RegisteredEventsResponse.builder()
				.events(eventRegistrationService.getEventsThatAUserRegistered(username))
				.build();
	}

	@GetMapping("registered-events/all-status/{username}")
	public RegisteredEventsWithStatusResponse getEventsThatAUserMadeRegistrationRequest(@PathVariable String username){
		return RegisteredEventsWithStatusResponse.builder()
				.eventsWithRegistrationResponses(eventRegistrationService.getEventsThatAUserMadeRegistrationRequest(username))
				.build();
	}

	@PostMapping("/add_registration")
	public EventInfoResponse addRegistration(@RequestBody RegistrationAddRequest registrationAddRequest){
		return eventRegistrationService.addRegistration(registrationAddRequest);
	}

	@PutMapping("/accept_request")
	public EventInfoResponse acceptRegistration(@RequestBody AnswerRegistrationRequest answerRegistrationRequest){
		return eventRegistrationService.acceptRegistration(answerRegistrationRequest);
	}

	@PutMapping("/reject_request")
	public EventInfoResponse rejectRegistration(@RequestBody AnswerRegistrationRequest answerRegistrationRequest){
		return eventRegistrationService.rejectRegistration(answerRegistrationRequest);
	}

}
