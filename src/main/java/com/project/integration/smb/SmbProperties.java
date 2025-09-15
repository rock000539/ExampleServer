package com.project.integration.smb;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "smb")
@Data
public class SmbProperties {

  private Map<String, ServerConfig> servers = new HashMap<>();

  @Data
  public static class ServerConfig {
    private String url;
    private String remotePath;
    private String username;
    private String password;

    public String getBaseSmbUrl() {
      String normalizedPath = remotePath.replace("\\", "/").replace(" ", "%20");
      return String.format("smb://%s/%s", url, normalizedPath);
    }
  }
}
