package com.event.eventfeed.api;


import com.event.eventfeed.dto.EventsInfoRestricted;
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
    //TODO follow KAfka event get followee events
    @GetMapping("/{username}")
    public ResponseEntity<EventsInfoRestricted> getUserFeed(@PathVariable String username){
        return ResponseEntity.ok().body(service.getFeed(username));
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
