/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.project.frame.config;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.LaxRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate設定
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Configuration
public class WebServiceConfig {

	@Value("${rest.connectTimeout:10000}")
	private int connectTimeout;

	@Autowired(required = false)
	private List<HttpMessageConverter<?>> converters;

	@Primary
	@Bean
	public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
		restTemplate.setMessageConverters(converters);
		return restTemplate;
	}

	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(closeableHttpClient()));
	}

	@Bean
	public CloseableHttpClient closeableHttpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		// Accept all certificates
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		var sslContext = SSLContextBuilder.create().loadTrustMaterial(trustStore, acceptingTrustStrategy).build();

		// SSL Socket Factory
		var sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1.2", "TLSv1.3"}, null, (hostname, session) -> true);

		// Connection Manager
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", sslSocketFactory)
				.build();
		var connectionManager = new PoolingHttpClientConnectionManager(registry);
		connectionManager.setMaxTotal(50);
		connectionManager.setDefaultMaxPerRoute(10);

		// Request Config
		var requestConfig = RequestConfig.custom()
				.setConnectTimeout(Timeout.ofSeconds(10))
				.build();

		// Build and return HttpClient
		return HttpClients.custom()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(requestConfig)
				.build();
	}
}
