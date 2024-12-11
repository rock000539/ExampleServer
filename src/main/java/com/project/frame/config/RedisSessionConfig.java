/*
 * Copyright (c) 2023 -Parker.
 * All rights reserved.
 */
package com.project.frame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;

@Configuration
public class RedisSessionConfig {

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		// 嘗試連接 Redis，如果失敗則傳回 null
		RedisConnectionFactory redisConnectionFactory = createJedisConnectionFactory();
		if (redisConnectionFactory != null) {
			return redisConnectionFactory;
		} else {
			// 如果 Redis 不可用，回傳 null，Spring Boot 會使用預設的 Session 配置
			return null;
		}
	}

	private RedisConnectionFactory createJedisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		try {
			// 嘗試連線 Redis，如果連線失敗，則傳回 null
			jedisConnectionFactory.afterPropertiesSet();
			return jedisConnectionFactory;
		} catch (Exception e) {
			// 連線失敗，回傳 null
			return null;
		}
	}

	// 當 Redis 不可用時，使用基於記憶體的備用方案
	@Bean
	@Primary
	public MapSessionRepository myMapSessionRepository(RedisConnectionFactory redisConnectionFactory) {
		if (redisConnectionFactory != null) {
			// 如果 Redis 可用，則使用 JedisConnectionFactory
			return new MapSessionRepository();
		} else {
			// 如果 Redis 不可用，回傳 null，Spring Boot 會使用預設的 Session 配置
			return null;
		}
	}

	private static class MapSessionRepository implements SessionRepository<MapSession> {

		@Override
		public MapSession createSession() {
			return new MapSession();
		}

		@Override
		public void save(MapSession session) {
			// Do nothing for an in-memory storage
		}

		@Override
		public MapSession findById(String id) {
			return null;
		}

		@Override
		public void deleteById(String id) {
			// Do nothing for an in-memory storage
		}
	}
}
