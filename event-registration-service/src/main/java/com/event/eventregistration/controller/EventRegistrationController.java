package com.event.eventregistration.controller;

import com.event.eventregistration.model.request.AnswerRegistrationRequest;
import com.event.eventregistration.model.request.EventInfoAddRequest;
import com.event.eventregistration.model.request.RegistrationAddRequest;
import com.event.eventregistration.model.response.EventInfoResponse;
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
