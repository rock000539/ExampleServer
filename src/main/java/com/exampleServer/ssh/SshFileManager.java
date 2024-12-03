/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.exampleServer.ssh;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.util.Vector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SshFileManager {

	/**
	 * Copy file from SFTP server to local directory.
	 */

	public void downloadFile(String host, String username, String password, int port, String remoteFilePath, String localFilePath) {
		ChannelSftp channelSftp = null;
		Session session = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(username, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();

			channelSftp.get(remoteFilePath, localFilePath);
		} catch (JSchException | SftpException e) {
			log.info("[downloadFile] Error while downloading file from SFTP server: {}", e.getMessage());
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
	}

	/**
	 * Copy folder from SFTP server to local directory.
	 */
	public void synchronizeFolder(String host, String username, String password, int port, String remoteFolderPath, String localFolderPath) {
		Session session = null;
		ChannelSftp channelSftp = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(username, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();

			// Change to the remote directory
			channelSftp.cd(remoteFolderPath);

			// List files in the remote directory
			Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(remoteFolderPath);
			for (ChannelSftp.LsEntry entry : fileList) {
				if (!entry.getAttrs().isDir()) { // Ignore directories
					String remoteFileName = entry.getFilename();
					String localFilePath = localFolderPath + File.separator + remoteFileName;
					File localFile = new File(localFilePath);
					// Download file from remote server
					channelSftp.get(remoteFileName, localFile.getAbsolutePath());
				}
			}
		} catch (JSchException | SftpException e) {
			log.info("[synchronizeFolder] Error while downloading file from SFTP server: {}", e.getMessage());
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
	}

	/**
	 * Move all files from SFTP server source directory to SFTP server target directory.
	 */
	public void moveAllFiles(String host, String username, String password, int port, String remoteFolderPath, String destinationFolderPath) {
		Session session = null;
		ChannelSftp channelSftp = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(username, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();

			// Change to the remote directory
			channelSftp.cd(remoteFolderPath);

			// List files in the remote directory
			Vector<ChannelSftp.LsEntry> fileList = channelSftp.ls(".");
			for (ChannelSftp.LsEntry entry : fileList) {
				if (!entry.getAttrs().isDir()) { // Ignore directories
					String remoteFileName = entry.getFilename();
					String destinationFilePath = destinationFolderPath + "/" + remoteFileName;
					channelSftp.rename(remoteFileName, destinationFilePath); // Move file
				}
			}
		} catch (JSchException | SftpException e) {
			log.info("[moveAllFiles] Error while downloading file from SFTP server: {}", e.getMessage());
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
	}
}
