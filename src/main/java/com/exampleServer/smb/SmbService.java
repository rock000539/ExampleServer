/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.exampleServer.smb;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import jcifs.CIFSContext;
import jcifs.smb.SmbFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Slf4j
@ConditionalOnBean(CIFSContext.class)
@Service
public class SmbService {

	@Autowired
	private CIFSContext cifsContext;

	private String buildSmbPath(String basePath, String... components) {
		StringBuilder path = new StringBuilder(basePath);
		for (String component : components) {
			if (path.length() > 0 && !path.toString().endsWith(File.separator)) {
				path.append(File.separator);
			}
			path.append(component);
		}
		String smbPath = path.toString().replace("\\", "/"); // SMB URLs should use forward slashes
		log.info("[buildSmbPath] Constructed SMB path: {}", smbPath);
		return smbPath;
	}

	public File readFileFromSmb(String smbBasePath, String... smbPathComponents) {
		String smbUrl = buildSmbPath(smbBasePath, smbPathComponents);
		try (SmbFile smbFile = new SmbFile(smbUrl, cifsContext);) {
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
			log.error("[readFileFromSmb] Error while downloading file from NAS server: {}", e.getMessage());
		}
		return null;
	}

	public void deleteFileFromSmb(String smbBasePath, String... smbPathComponents) {
		String smbUrl = buildSmbPath(smbBasePath, smbPathComponents);
		try (SmbFile smbFile = new SmbFile(smbUrl, cifsContext)) {
			if (smbFile.exists()) {
				smbFile.delete();
				log.info("[deleteFileFromSmb] File deleted successfully: {}", smbUrl);
			} else {
				log.warn("[deleteFileFromSmb] File not found: {}", smbUrl);
			}
		} catch (Exception e) {
			log.error("[deleteFileFromSmb] Error while deleting file from NAS server: {}", e.getMessage());
		}
	}

	public void clearDirectoryFromSmb(String smbBasePath, String... smbPathComponents) {
		String smbUrl = buildSmbPath(smbBasePath, smbPathComponents);
		log.info("[clearDirectoryFromSmb] Clearing directory: {}", smbUrl);
		try (SmbFile smbDir = new SmbFile(smbUrl, cifsContext)) {
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
			log.error("[clearDirectoryFromSmb] Error while clearing directory from NAS server: {}", e.getMessage());
		}
	}
}
