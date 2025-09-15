/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.frame.config;

import com.project.integration.smb.SmbProperties;
import java.util.HashMap;
import java.util.Map;
import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@ConditionalOnProperty(value = "smb.server.enabled", havingValue = "true")
@Configuration
public class SmbConfig {

  private static final Logger LOG = LoggerFactory.getLogger(SmbConfig.class);

  private final SmbProperties smbProperties;

  public SmbConfig(SmbProperties smbProperties) {
    this.smbProperties = smbProperties;
  }

  @Bean
  public Map<String, CIFSContext> cifsContextMap() {
    Map<String, CIFSContext> contextMap = new HashMap<>();
    for (Map.Entry<String, SmbProperties.ServerConfig> entry :
        smbProperties.getServers().entrySet()) {
      String serverName = entry.getKey();
      SmbProperties.ServerConfig config = entry.getValue();
      try {
        NtlmPasswordAuthenticator auth =
            new NtlmPasswordAuthenticator(
                config.getUrl(), config.getUsername(), config.getPassword());
        CIFSContext baseContext = SingletonContext.getInstance();
        contextMap.put(serverName, baseContext.withCredentials(auth));
        LOG.info("Created CIFSContext for server: {}", serverName);
      } catch (Exception e) {
        LOG.error("Error creating CIFSContext for server {}: {}", serverName, e.getMessage());
      }
    }
    return contextMap;
  }
}
