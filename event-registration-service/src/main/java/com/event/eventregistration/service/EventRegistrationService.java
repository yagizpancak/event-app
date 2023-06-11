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
import com.event.eventregistration.model.response.RegistrationInfoWithStatus;
import com.event.eventregistration.repository.RegistrationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class EventRegistrationService {
	private final RegistrationRepository registrationRepository;
	private final MongoTemplate mongoTemplate;

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

	public List<String> getRegisteredUsersForAnEvent(String eventUUID){
		EventInfo eventInfo = registrationRepository.findById(eventUUID).orElse(null);
		if (eventInfo != null) {
			return eventInfo.getUsers().stream()
					.filter(registration -> registration.getStatus() == RegistrationStatus.ACCEPTED)
					.map(Registration::getUsername)
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
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



	public List<String> getEventsThatAUserRegistered(String username) {
		List<EventInfo> eventInfos = mongoTemplate.find(
				Query.query(Criteria.where("users.username").is(username)
						.and("users.status").is(RegistrationStatus.ACCEPTED)),
				EventInfo.class);
		return eventInfos.stream()
				.map(EventInfo::getId)
				.toList();
	}


	public List<RegistrationInfoWithStatus> getEventsThatAUserMadeRegistrationRequest(String username) {
		List<EventInfo> eventInfos = mongoTemplate.find(
				Query.query(Criteria.where("users.username").is(username)),
				EventInfo.class);
		List<RegistrationInfoWithStatus> registrationsWithStatus = new ArrayList<>();
		for(EventInfo eventInfo : eventInfos){
			for(Registration registration : eventInfo.getUsers()){
				if(registration.getUsername().equals(username)){
					registrationsWithStatus.add(RegistrationInfoWithStatus.builder()
							.eventUUID(eventInfo.getId())
							.registrationStatus(registration.getStatus())
							.build());
				}
			}
		}
		return registrationsWithStatus;
	 }
	/*

	public List<RegistrationInfoWithStatus> getEventsThatAUserMadeRegistrationRequest(String username) {
		MatchOperation matchUser = Aggregation.match(Criteria.where("organizator_username").is(username));
		UnwindOperation unwindUsers = Aggregation.unwind("users");
		MatchOperation matchUsername = Aggregation.match(Criteria.where("users.username").is(username));
		ProjectionOperation projectFields = Aggregation.project("id", "users.status")
				.and("id").as("eventUUID")
				.and("users.status").as("registrationStatus");
		ReplaceRootOperation replaceRoot = Aggregation.replaceRoot().withValueOf("users");

		Aggregation aggregation = Aggregation.newAggregation(matchUser, unwindUsers, matchUsername, projectFields, replaceRoot);

		List<RegistrationInfoWithStatus> registrationResponses =
				mongoTemplate.aggregate(aggregation, EventInfo.class, RegistrationInfoWithStatus.class)
						.getMappedResults();
		return registrationResponses;
	}
*/


}
