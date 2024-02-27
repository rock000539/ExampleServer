/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.exampleServer.kafka.consumer;

import java.util.concurrent.CountDownLatch;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// @Component
public class KafkaConsumer {

	private CountDownLatch latch = new CountDownLatch(1);

	private String payload;

	@Component
	public class KafkaConsumerListener {

		@KafkaListener(topics = {"realtimeEvent"}, groupId = "group1", containerFactory = "kafkaListenerContainerFactory")
		public void kafkaListener(String message) {
			System.out.println(message);
		}
	}
}
