/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.integration.smb;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import jcifs.CIFSContext;
import jcifs.smb.SmbFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
//@ConditionalOnBean(CIFSContext.class)
@Service
public class SmbService {

  private final Map<String, CIFSContext> cifsContextMap;
  private final SmbProperties smbProperties;

  public SmbService(Map<String, CIFSContext> cifsContextMap, SmbProperties smbProperties) {
    this.cifsContextMap = cifsContextMap;
    this.smbProperties = smbProperties;
  }

  private String buildSmbPath(String basePath, String... components) {
    StringBuilder path = new StringBuilder(basePath);
    for (String component : components) {
      if (!path.isEmpty() && !path.toString().endsWith(File.separator)) {
        path.append(File.separator);
      }
      path.append(component);
    }
    String smbPath = path.toString().replace("\\", "/"); // SMB URLs should use forward slashes
    log.info("[buildSmbPath] Constructed SMB path: {}", smbPath);
    return smbPath;
  }

  public File readFileFromSmb(String serverName, String smbBasePath, String... smbPathComponents) {
    CIFSContext context = cifsContextMap.get(serverName);
    if (context == null) {
      throw new IllegalArgumentException("No CIFSContext found for server: " + serverName);
    }
    String smbUrl = buildSmbPath(smbBasePath, smbPathComponents);
    try (SmbFile smbFile = new SmbFile(smbUrl, context); ) {
      String uuid = UUID.randomUUID().toString();

      // the generated name should be a simple file name
      Path tempFile = Files.createTempFile("smb_", uuid);
      Path targetFile = tempFile.resolveSibling(smbFile.getName());
      try (InputStream smbInputStream = smbFile.getInputStream()) {
        Files.copy(smbInputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
      }

      Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
      return targetFile.toFile();
    } catch (Exception e) {
      log.error(
          "[readFileFromSmb] Error while downloading file from NAS server: {}", e.getMessage());
    }
    return null;
  }

  public void deleteFileFromSmb(
      String serverName, String smbBasePath, String... smbPathComponents) {
    CIFSContext context = cifsContextMap.get(serverName);
    if (context == null) {
      throw new IllegalArgumentException("No CIFSContext found for server: " + serverName);
    }
    String smbUrl = buildSmbPath(smbBasePath, smbPathComponents);
    try (SmbFile smbFile = new SmbFile(smbUrl, context)) {
      if (smbFile.exists()) {
        smbFile.delete();
        log.info("[deleteFileFromSmb] File deleted successfully: {}", smbUrl);
      } else {
        log.warn("[deleteFileFromSmb] File not found: {}", smbUrl);
      }
    } catch (Exception e) {
      log.error(
          "[deleteFileFromSmb] Error while deleting file from NAS server: {}", e.getMessage());
    }
  }

  public void clearDirectoryFromSmb(
      String serverName, String smbBasePath, String... smbPathComponents) {
    CIFSContext context = cifsContextMap.get(serverName);
    if (context == null) {
      throw new IllegalArgumentException("No CIFSContext found for server: " + serverName);
    }

    String smbUrl = buildSmbPath(smbBasePath, smbPathComponents);
    log.info("[clearDirectoryFromSmb] Clearing directory: {}", smbUrl);
    try (SmbFile smbDir = new SmbFile(smbUrl, context)) {
      if (smbDir.exists() && smbDir.isDirectory()) {
        log.info("[clearDirectoryFromSmb] Directory exists: {}", smbUrl);
        for (SmbFile file : smbDir.listFiles()) {
          log.info("[clearDirectoryFromSmb] Deleting file: {}", file.getPath());
          file.delete();
        }
        log.info("[clearDirectoryFromSmb] Directory cleared successfully: {}", smbUrl);
      } else {
        log.warn("[clearDirectoryFromSmb] Directory not found or not a directory: {}", smbUrl);
      }
    } catch (Exception e) {
      log.error(
          "[clearDirectoryFromSmb] Error while clearing directory from NAS server: {}",
          e.getMessage());
    }
  }

  private void uploadFile(String serverName, String fileName, InputStream inputStream)
      throws Exception {
    CIFSContext context = cifsContextMap.get(serverName);
    if (context == null) {
      throw new IllegalArgumentException("No CIFSContext found for server: " + serverName);
    }

    SmbProperties.ServerConfig config = getServerConfig(serverName);

    String smbUrl =
        String.format(
            "smb://%s/%s/%s",
            config.getUrl(),
            config.getRemotePath().replace("\\", "/").replace(" ", "%20"),
            fileName);

    try (SmbFile smbFile = new SmbFile(smbUrl, context);
        OutputStream os = smbFile.getOutputStream()) {

      byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        os.write(buffer, 0, bytesRead);
      }
      os.flush();
    }

    log.info("File uploaded successfully to server {}: {}", serverName, smbUrl);
  }

  public void uploadFileToServer(String serverName, String fileName, byte[] fileBytes)
      throws Exception {
    try (InputStream is = new java.io.ByteArrayInputStream(fileBytes)) {
      uploadFile(serverName, fileName, is);
    }
  }

  public void uploadFileToServer(String serverName, String fileName, String localFilePath)
      throws Exception {
    File file = new File(localFilePath);
    if (!file.exists() || !file.isFile()) {
      throw new IllegalArgumentException("Local file not found: " + localFilePath);
    }

    try (InputStream is = new FileInputStream(file)) {
      uploadFile(serverName, fileName, is);
    }
  }

  public SmbProperties.ServerConfig getServerConfig(String serverName) {
    SmbProperties.ServerConfig config = smbProperties.getServers().get(serverName);
    if (config == null) {
      throw new IllegalArgumentException("No SMB configuration found for server: " + serverName);
    }
    return config;
  }
}
