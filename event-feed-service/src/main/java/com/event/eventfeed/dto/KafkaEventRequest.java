package com.event.eventfeed.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class KafkaEventRequest {
	private String id;
	private String organizatorUsername;
}
