/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.project.integration.ssh;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Vector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SshFileManager
{

	private String host;
	private int port = 22;
	private String username;
	private String password;

	public SshFileManager(String host, int port, String username, String password)
	{
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	/**
	 * Copy file from SFTP server to local directory.
	 */
	public void downloadFile(String remoteFilePath, String localFilePath)
	{
		ChannelSftp channelSftp = null;
		Session session = null;
		try
		{
			JSch jsch = new JSch();
			session = jsch.getSession(username, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();

			channelSftp.get(remoteFilePath, localFilePath);
		}
		catch (JSchException | SftpException e)
		{
			log.info("[downloadFile] Error while downloading file from SFTP server: {}", e.getMessage());
		}
		finally
		{
			if (channelSftp != null)
			{
				channelSftp.disconnect();
			}
			if (session != null)
			{
				session.disconnect();
			}
		}
	}

	/**
	 * Copy folder from SFTP server to local directory.
	 */
	public void synchronizeFolder(String remoteFolderPath, String localFolderPath)
	{
		Session session = null;
		ChannelSftp channelSftp = null;
		try
		{
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
			for (ChannelSftp.LsEntry entry : fileList)
			{
				if (!entry.getAttrs().isDir())
				{ // Ignore directories
					String remoteFileName = entry.getFilename();
					String localFilePath = localFolderPath + File.separator + remoteFileName;
					File localFile = new File(localFilePath);
					// Download file from remote server
					channelSftp.get(remoteFileName, localFile.getAbsolutePath());
				}
			}
		}
		catch (JSchException | SftpException e)
		{
			log.info("[synchronizeFolder] Error while downloading file from SFTP server: {}", e.getMessage());
		}
		finally
		{
			if (channelSftp != null)
			{
				channelSftp.disconnect();
			}
			if (session != null)
			{
				session.disconnect();
			}
		}
	}

	/**
	 * Move all files from SFTP server source directory to SFTP server target directory.
	 */
	public void moveAllFiles(String remoteFolderPath, String destinationFolderPath)
	{
		Session session = null;
		ChannelSftp channelSftp = null;
		try
		{
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
			for (ChannelSftp.LsEntry entry : fileList)
			{
				if (!entry.getAttrs().isDir())
				{ // Ignore directories
					String remoteFileName = entry.getFilename();
					String destinationFilePath = destinationFolderPath + "/" + remoteFileName;
					channelSftp.rename(remoteFileName, destinationFilePath); // Move file
				}
			}
		}
		catch (JSchException | SftpException e)
		{
			log.info("[moveAllFiles] Error while downloading file from SFTP server: {}", e.getMessage());
		}
		finally
		{
			if (channelSftp != null)
			{
				channelSftp.disconnect();
			}
			if (session != null)
			{
				session.disconnect();
			}
		}
	}

	public void uploadFile(Path localFilePath, String remoteDir, String remoteFileName) throws Exception
	{
		try (InputStream inputStream = Files.newInputStream(localFilePath))
		{
			uploadFile(inputStream, remoteDir, remoteFileName);
		}
	}

	public void uploadFile(byte[] fileBytes, String remoteDir, String remoteFileName) throws Exception
	{
		try (InputStream inputStream = new ByteArrayInputStream(fileBytes))
		{
			uploadFile(inputStream, remoteDir, remoteFileName);
		}
	}


	private void uploadFile(InputStream inputStream, String remoteDir, String remoteFileName) throws Exception
	{
		JSch jsch = new JSch();
		Session session = null;
		ChannelSftp channelSftp = null;

		try
		{
			session = jsch.getSession(username, host, port);
			session.setPassword(password);

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();

			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();
			channelSftp.cd(remoteDir);

			channelSftp.put(inputStream, remoteFileName);

		}
		finally
		{
			if (channelSftp != null && channelSftp.isConnected())
			{
				channelSftp.disconnect();
			}
			if (session != null && session.isConnected())
			{
				session.disconnect();
			}
		}
	}
}