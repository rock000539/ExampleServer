/*
 * Copyright (c) 2021 -Parker.
 * All rights reserved.
 */
package com.exampleServer.ftp.dao;

import java.io.IOException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

/**
 * @author Parker Huang
 * @since 1.0.0
 */
public interface FtpDao {

	/**
	 * 檔案連結
	 *
	 * @return
	 */
	String getFileUrl();

	/**
	 * FTP 檔案位置
	 *
	 * @return
	 */
	String getFtpPath();

	/**
	 * 上傳檔案
	 *
	 * @param directory
	 * @param filename
	 * @param content
	 * @return
	 * @throws IOException
	 */
	boolean uploadFile(String directory, String filename, byte[] content) throws IOException;

	/**
	 * 刪除檔案
	 *
	 * @param directory
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	boolean deleteFile(String directory, String filename) throws IOException;

	/**
	 * 刪除目錄
	 *
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	boolean removeDirectory(String directory) throws IOException;

	/**
	 * 下載檔案
	 *
	 * @param directory
	 * @param fileName 檔案名稱
	 * @return
	 * @throws IOException
	 */
	ResponseEntity<InputStreamResource> downloadFile(String directory, String fileName) throws IOException;
}
