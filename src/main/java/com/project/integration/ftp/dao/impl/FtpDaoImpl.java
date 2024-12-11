/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.project.integration.ftp.dao.impl;

import com.project.integration.ftp.dao.FtpDao;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

/**
 * @author Parker Huang
 * @since 1.0.0
 */
@Slf4j
@Repository
public class FtpDaoImpl implements FtpDao {

	@Value("${ftp.host:}")
	private String host;

	@Value("${ftp.user:}")
	private String user;

	@Value("${ftp.password:}")
	private String password;

	@Value("${ftp.path:}")
	private String path;

	@Value("${ftp.url:}")
	private String url;

	@Override
	public String getFileUrl() {
		return url;
	}

	@Override
	public String getFtpPath() {
		return path;
	}

	@Override
	public boolean uploadFile(String directory, String fileName, byte[] content) throws IOException {
		FTPClient client = getConnection();
		try {
			String transferDirectory = transferEncoding(directory);
			if (!client.changeWorkingDirectory(transferDirectory)) {
				client.makeDirectory(transferDirectory);
				client.changeWorkingDirectory(transferDirectory);
			}
			return client.storeFile(transferEncoding(fileName), new ByteArrayInputStream(content));
		} finally {
			disconnect(client);
		}
	}

	@Override
	public boolean deleteFile(String directory, String fileName) throws IOException {
		FTPClient client = getConnection();
		try {
			client.changeWorkingDirectory(transferEncoding(directory));
			return client.deleteFile(transferEncoding(fileName));
		} finally {
			disconnect(client);
		}
	}

	@Override
	public boolean removeDirectory(String directory) throws IOException {
		FTPClient client = getConnection();
		try {
			return removeDirectory(client, transferEncoding(directory));
		} finally {
			disconnect(client);
		}
	}

	private boolean removeDirectory(FTPClient ftpClient, String directory) throws IOException {
		FTPFile[] files = ftpClient.mlistDir(directory);
		if (files != null && files.length > 0) {
			for (FTPFile file : files) {
				String fileName = file.getName();
				if (fileName.equals(".") || fileName.equals("..")) {
					// Skip parent directory and the directory itself.
					continue;
				}
				String filePath = directory + File.separator + fileName;

				if (file.isDirectory()) {
					// remove the sub directory
					removeDirectory(ftpClient, filePath);
				} else {
					// Delete the file.
					if (!ftpClient.deleteFile(filePath)) {
						throw new IOException("Delete child file fail: " + filePath);
					}
				}
			}
		}
		return ftpClient.removeDirectory(directory);
	}

	private FTPClient getConnection() throws IOException {
		FTPClient client = new FTPClient();
		if (log.isDebugEnabled()) {
			client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
		}
		client.connect(host);
		int reply = client.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			client.disconnect();
			throw new IOException("Fail to connect server");
		}

		client.login(user, password);
		client.enterLocalPassiveMode();
		client.setFileType(FTP.BINARY_FILE_TYPE);
		return client;
	}

	private void disconnect(FTPClient client) throws IOException {
		if (client.isConnected()) {
			client.logout();
			client.disconnect();
		}
	}

	private String transferEncoding(String text) {
		return StringUtils.newStringIso8859_1(text.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public ResponseEntity<InputStreamResource> downloadFile(String directory, String fileName) throws IOException {
		// 建立 FTPClient
		FTPClient client = getConnection();
		try {
			// 設定檔案下載的相對路徑
			String remoteFilePath = directory + File.separator + fileName;

			// 從 FTP 伺服器下載檔案
			InputStream inputStream = client.retrieveFileStream(remoteFilePath);

			// 建立 InputStreamResource
			InputStreamResource resource = new InputStreamResource(inputStream);

			// 設定檔案下載的 HTTP 標頭
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

			return ResponseEntity.ok().headers(headers).body(resource);
		} finally {
			disconnect(client);
		}
	}
}
