package com.event.eventregistration.service;

import com.event.eventregistration.entity.EventInfo;
import com.event.eventregistration.entity.Registration;
import com.event.eventregistration.entity.RegistrationStatus;
import com.event.eventregistration.exception.BusinessException;
import com.event.eventregistration.exception.ErrorCode;
import com.event.eventregistration.model.request.AnswerRegistrationRequest;
import com.event.eventregistration.model.request.EventInfoAddRequest;
import com.event.eventregistration.model.request.RegistrationAddRequest;
import com.event.eventregistration.model.response.EventInfoResponse;
import com.event.eventregistration.repository.RegistrationRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class EventRegistrationService {
	private final RegistrationRepository registrationRepository;

	public EventInfoResponse getEventRegistration(String id) {
		EventInfo eventInfo = registrationRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Event not found.", ErrorCode.resource_missing));
		return EventInfoResponse.fromEntity(eventInfo);
	}
	@KafkaListener(topics = "createdEvents")
	public EventInfoResponse addEvent(EventInfoAddRequest eventInfoAddRequest){
		EventInfo eventInfo = fromRequest(eventInfoAddRequest);
		registrationRepository.save(eventInfo);
		return EventInfoResponse.fromEntity(eventInfo);
	}

	public EventInfoResponse addRegistration(RegistrationAddRequest registrationAddRequest) {
		EventInfo eventInfo = registrationRepository.findById(registrationAddRequest.getEventId())
				.orElseThrow(() -> new BusinessException("Event not found.", ErrorCode.resource_missing));
		Registration registration = fromRequest(registrationAddRequest);
		eventInfo.getUsers().add(registration);
		registrationRepository.save(eventInfo);
		return EventInfoResponse.fromEntity(eventInfo);
	}

	public EventInfoResponse acceptRegistration(AnswerRegistrationRequest answerRegistrationRequest) {
		EventInfo eventInfo = registrationRepository.findById(answerRegistrationRequest.getEventId())
				.orElseThrow(() -> new BusinessException("Event not found.", ErrorCode.resource_missing));
		Registration registration = eventInfo.findUser(answerRegistrationRequest.getUserUsername())
				.orElseThrow(() -> new BusinessException("User not registered.", ErrorCode.resource_missing));
		if (eventInfo.getOrganizatorUsername().equals(answerRegistrationRequest.getOrganizatorUsername())){
			registration.setStatus(RegistrationStatus.ACCEPTED);
		}else {
			throw new BusinessException("Only organizator accept the registration request.", ErrorCode.unauthorized);
		}
		registrationRepository.save(eventInfo);
		return EventInfoResponse.fromEntity(eventInfo);
	}

	public EventInfoResponse rejectRegistration(AnswerRegistrationRequest answerRegistrationRequest) {
		EventInfo eventInfo = registrationRepository.findById(answerRegistrationRequest.getEventId())
				.orElseThrow(() -> new BusinessException("Event not found.", ErrorCode.resource_missing));
		Registration registration = eventInfo.findUser(answerRegistrationRequest.getUserUsername())
				.orElseThrow(() -> new BusinessException("User not registered.", ErrorCode.resource_missing));
		if (eventInfo.getOrganizatorUsername().equals(answerRegistrationRequest.getOrganizatorUsername())){
			registration.setStatus(RegistrationStatus.REJECTED);
		}else {
			throw new BusinessException("Only organizator reject the registration request.", ErrorCode.unauthorized);
		}
		registrationRepository.save(eventInfo);
		return EventInfoResponse.fromEntity(eventInfo);
	}

	private Registration fromRequest(RegistrationAddRequest registrationAddRequest){
		return Registration.builder()
				.username(registrationAddRequest.getUsername())
				.status(RegistrationStatus.WAITING)
				.build();
	}
	private EventInfo fromRequest(EventInfoAddRequest eventInfoAddRequest) {
		return EventInfo.builder()
				.id(eventInfoAddRequest.getId())
				.organizatorUsername(eventInfoAddRequest.getOrganizatorUsername())
				.userLimit(eventInfoAddRequest.getUserLimit())
				.users(eventInfoAddRequest.getUsers())
				.build();
	}



}
