/*
 * Copyright (c) 2023 - SoftBI Corporation Limited.
 * All rights reserved.
 */
package com.frame.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.frame.util.CheckmarxUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "kafka", name = "used", havingValue = "true")
public class KafkaTemplateConfig {

    @Value("${kafka.server.url:}")
    private String kafkaServerUrl;

    @Value("${kafka.server.jaas.account:}")
    private String jaasAccount;

    @Value("${kafka.server.jaas.password:}")
    private String jaasPassword;

    @Value("${kafka.server.cer.file.path:}")
    private String cerFilePath;

    @Value("${kafka.server.cer.password:}")
    private String cerFilePassword;

    @Value("${project.file.base.path:}")
    private String keystoreFilePath;

    @Primary
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() throws Exception {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        try {
            return new DefaultKafkaProducerFactory<>(producerConfigs());
        } catch (Exception e) {
            log.error("Failed to create Kafka producer factory.", e);
            throw e;
        }
    }

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		// 指定多個kafka集群多個地址
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

		// 重試次數，0為不啟用重試機制
		props.put(ProducerConfig.RETRIES_CONFIG, 5);
		// 同步到副本, 默認為1
		// acks=0 把消息發送到kafka就認為發送成功
		// acks=1 把消息發送到kafka leader分區，並且寫入磁盤就認為發送成功
		// acks=all 把消息發送到kafka leader分區，並且leader分區的副本follower對消息進行了同步就任務發送成功
		props.put(ProducerConfig.ACKS_CONFIG, "all");

		// 生產者空間不足時，send()被阻塞的時間，默認60s
		props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 6000);
		// 控制批處理大小，單位為字節
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		// 批量發送，延遲為1毫秒，啟用該功能能有效減少生產者發送消息次數，從而提高並發量
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		// 生產者可以使用的總內存字節來緩衝等待發送到服務器的記錄
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		// 消息的最大大小限制,也就是說send的消息大小不能超過這個限制, 默認1048576(1MB)
		props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1048576);
		// 鍵的序列化方式
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		// 值的序列化方式
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		// 壓縮消息，支持四種類型，分別為：none、lz4、gzip、snappy，默認為none。
		// 消費者默認支持解壓，所以壓縮設置在生產者，消費者無需設置。
		props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
		
	    // SSL/TLS 配置
        convertCerFile();
        props.put("security.protocol", "SASL_SSL");
        props.put("ssl.truststore.location", keystoreFilePath + File.separator + "kafkaJks.jks");
        props.put("ssl.truststore.password", cerFilePassword);
        
        //其他設定
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("socket.connection.setup.timeout.ms", 5000);
        props.put("request.timeout.ms", 150000);
		return props;
	}
	
	
	//cer檔轉jks檔
    private void convertCerFile() {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            FileInputStream cerInputStream = new FileInputStream(cerFilePath);
            Certificate certificate = certificateFactory.generateCertificate(cerInputStream);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, cerFilePassword.toCharArray());

            keyStore.setCertificateEntry("kafkaJks", certificate);

            File keystoreFile = CheckmarxUtil.newFileSafely(keystoreFilePath, "kafkaJks.jks");
            if (!keystoreFile.exists()) {
                keystoreFile.createNewFile();
            }

            FileOutputStream keystoreOutputStream = new FileOutputStream(keystoreFile);
            keyStore.store(keystoreOutputStream, cerFilePassword.toCharArray());

            keystoreOutputStream.close();
        } catch (Exception e) {
            log.error("Failed to convert Cer File.", e);
        }
    }
}
