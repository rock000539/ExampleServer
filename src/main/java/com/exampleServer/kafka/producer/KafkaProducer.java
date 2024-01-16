/*
 * Copyright (c) 2023 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.exampleServer.kafka.producer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SettableListenableFuture;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * producer 同步方式發送數據
     *
     * @param topic   topic名稱
     * @param message producer發送的數據
     * @throws TimeoutException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void send(String topic, Object message) throws InterruptedException, ExecutionException, TimeoutException {
        kafkaTemplate.send(topic, message).get(10, TimeUnit.SECONDS);
    }
    
    /** producer 同步方式發送數據 */
    public <R> ListenableFuture<R> sendMessage(String topic, Object message, Class<R> responseType) {
        SettableListenableFuture<R> future = new SettableListenableFuture<>();

        ListenableFuture<SendResult<String, Object>> kafkaFuture = kafkaTemplate.send(topic, message);

        kafkaFuture.addCallback(result -> {
            R sentMessage = responseType.cast(result.getProducerRecord().value());
            log.info("Message sent successfully. Sent message: " + sentMessage);
            future.set(sentMessage);
        }, ex -> {
            log.info("Failed to send message: " + ex.getMessage());
            future.setException(ex);
        });

        return future;
    }

    /**
     * producer 異步方式發送數據
     *
     * @param topic   topic名稱
     * @param message producer發送的數據
     */
    public void sendMessageAsync(String topic, Object message) {
        kafkaTemplate.send(topic, message).addCallback(new ListenableFutureCallback() {

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("success");
            }

            @Override
            public void onSuccess(Object o) {
                System.out.println("failure");
            }
        });
    }
}
