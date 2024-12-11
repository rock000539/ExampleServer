/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.project.integration.kafka.producer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaProducer {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	/**
	 * producer 同步方式發送數據
	 *
	 * @param topic topic名稱
	 * @param message producer發送的數據
	 * @throws TimeoutException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public void send(String topic, Object message) throws InterruptedException, ExecutionException, TimeoutException {
		kafkaTemplate.send(topic, message).get(10, TimeUnit.SECONDS);
	}

	/** producer 同步方式發送數據 */
	public <R> CompletionStage<R> sendMessage(String topic, Object message, Class<R> responseType) {
		CompletableFuture<SendResult<String, Object>> kafkaFuture = kafkaTemplate.send(topic, message);

		return kafkaFuture.thenApply(result -> {
			Object sentValue = result.getProducerRecord().value();
			if (responseType.isInstance(sentValue)) {
				R castedValue = responseType.cast(sentValue);
				log.info("Message sent successfully. Sent message: {}", castedValue);
				return castedValue;
			} else {
				throw new ClassCastException("Message value is not of expected type: " + responseType.getName());
			}
		}).exceptionally(ex -> {
			log.error("Failed to send message: {}", ex.getMessage(), ex);
			throw new RuntimeException(ex);
		});
	}
}
