/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.project.frame.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

public class KafkaConsumerConfig {

	@Value("${spring.kafka.event.consumer.auto-offset-reset:latest}")
	private String AUTO_OFFSET_RESET;

	@Bean
	KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		// 設置消費者工廠
		factory.setConsumerFactory(consumerFactory());
		// 消費者組中線程數量
		factory.setConcurrency(3);
		// 拉取超時時間
		factory.getContainerProperties().setPollTimeout(3000);

		// 當使用批量監聽器時需要設置為true
		factory.setBatchListener(true);

		return factory;
	}

	// @Bean
	public ConsumerFactory<String, String> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs());
	}

	// @Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> propsMap = new HashMap<>();
		// Kafka地址
		propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		// 配置默認分組，這裡沒有配置+在監聽的地方沒有設置groupId，多個服務會出現收到相同消息情況
		propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, "defaultGroup");
		// 是否自動提交offset偏移量(默認true)
		propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		// 自動提交的頻率(ms)
		propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		// Session超時設置
		propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		// 鍵的反序列化方式
		propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		// 值的反序列化方式
		propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		// offset偏移量規則設置：
		// (1)、earliest：當各分區下有已提交的offset時，從提交的offset開始消費；無提交的offset時，從頭開始消費
		// (2)、latest：當各分區下有已提交的offset時，從提交的offset開始消費；無提交的offset時，消費新產生的該分區下的數據
		// (3)、none：topic各分區都存在已提交的offset時，從offset後開始消費；只要有一個分區不存在已提交的offset，則拋出異常
		propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET);
		return propsMap;
	}
}
