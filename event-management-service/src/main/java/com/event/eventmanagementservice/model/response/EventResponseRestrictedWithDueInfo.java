package com.event.eventmanagementservice.model.response;


import com.event.eventmanagementservice.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseRestrictedWithDueInfo {
    private String name;
    private LocalDateTime startDate;
    private boolean isClosed;
    private String imageUrl;

    public static EventResponseRestrictedWithDueInfo fromEvent(Event event,boolean isClosed){
        return EventResponseRestrictedWithDueInfo.builder()
                .name(event.getName())
                .startDate(event.getStartDate())
                .isClosed(isClosed)
                .imageUrl("/api/v1/event-management/get-event-image/" + event.getId())
                .build();
    }
}
