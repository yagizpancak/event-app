package com.event.eventfeed.api;


import com.event.eventfeed.dto.FeedRequest;
import com.event.eventfeed.dto.FeedResponse;
import com.event.eventfeed.model.Feed;
import com.event.eventfeed.service.EventFeedService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event-feed")
@RequiredArgsConstructor
public class EventFeedController {

    private final EventFeedService service;
    private final ModelMapper modelMapper;

    @GetMapping("/{username}")
    public ResponseEntity<FeedResponse> getUserFeed(@PathVariable String username){
        return ResponseEntity.ok().body(FeedResponse.builder()
                        .events(service.getFeed(username))
                        .build());
    }

    @GetMapping("/test/{username}")
    public ResponseEntity<List<String>> getUserFeedString(@PathVariable String username){
        return ResponseEntity.ok().body(service.getFeeds(username));
    }

    @PostMapping("/add-user/{username}")
    public void addUser(@PathVariable String username){
        service.addUser(username);
    }

}
