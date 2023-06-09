package com.event.eventmanagementservice.model.request;

import lombok.Data;

import java.util.List;

@Data
public class GetEventsRequest {
	private List<String> idList;
}
