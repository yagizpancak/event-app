package com.event.eventfeed.service;


import com.event.eventfeed.dto.*;
import com.event.eventfeed.model.Feed;
import com.event.eventfeed.repo.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventFeedService {

    private final FeedRepository feedRepository;
    private final MongoTemplate mongoTemplate;
    private final WebClient.Builder webClientBuilder;

    //FIXME Kafka ekle
    public EventsInfoRestricted getFeed(String username) {
        List<String> uuids = new ArrayList<>();
        feedRepository.findByUsername(username).ifPresent(feed ->{
             uuids.addAll(feed.getFeedEvents());
        });
        EventsInfoRestricted eventResponses = webClientBuilder.build().post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("event-management-service")
                        .path("/api/v1/event-management/get-current-events/from-uuid-list")
                        .build())
                .body(BodyInserters.fromValue(GetEventsRequest.builder()
                                                .idList(uuids)
                                                .build()))
                .retrieve()
                .bodyToMono(EventsInfoRestricted.class)
                .block();

        return eventResponses;
    }

    public List<String> getFeeds(String username){
        var feedOptional  = feedRepository.findByUsername(username);
        if(feedOptional.isPresent()){
            return feedOptional.get().getFeedEvents();
        }
        return null;
    }

    @KafkaListener(topics = "createdEvents")
    public void addFeed(KafkaEventRequest kafkaEventRequest){
        FollowerResponse followerResponse = webClientBuilder.build().get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host("user-service")
                    .path("/api/v1/users/follower-list/{username}")
                    .build(kafkaEventRequest.getOrganizatorUsername()))
                .retrieve()
                .bodyToMono(FollowerResponse.class)
                .block();
        List<String> usernames = followerResponse.getFollowers().stream().map(ApplicationUserRestrictedResponse::getUsername).collect(Collectors.toList());
        Query query = Query.query(Criteria.where("username").in(usernames));
        Update update = new Update().addToSet("feedEvents", kafkaEventRequest.getId());
        mongoTemplate.updateMulti(query, update, Feed.class);
    }

    public void addUser(String username) {
        feedRepository.save(Feed.builder()
                    .username(username)
                    .feedEvents(new ArrayList<>())
                    .build());
    }
}
