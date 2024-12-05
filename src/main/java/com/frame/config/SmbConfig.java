/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.frame.config;

import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@ConditionalOnProperty(value = "smb.server.enabled", havingValue = "true")
@Configuration
public class SmbConfig {

	@Value("${smb.server.url}")
	String smbServerUrl;

	@Value("${smb.server.userName}")
	String smbServerUserName;

	@Value("${smb.server.password}")
	String smbServerPassword;

	@Bean
	public CIFSContext cifsContext() {
		try {
			NtlmPasswordAuthenticator auth = new NtlmPasswordAuthenticator(smbServerUrl, smbServerUserName, smbServerPassword);
			CIFSContext baseContext = SingletonContext.getInstance();
			return baseContext.withCredentials(auth);
		} catch (Exception e) {
			log.error("[cifsContext] Error while creating CIFSContext: {}", e.getMessage());
		}
		return null;
	}
}
