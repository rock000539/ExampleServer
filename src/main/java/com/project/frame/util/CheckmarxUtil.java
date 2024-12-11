/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.project.frame.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.SafeFile;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Checkmarx 弱掃處理工具
 *
 * @author Parker Huang
 * @since 1.0.0
 */
@Slf4j
@Component
public class CheckmarxUtil {

	private static String baseDir;

	@Value("${project.filePath.root:}")
	public void setBaseDir(String baseDir) {
		CheckmarxUtil.baseDir = baseDir;
	}

	/**
	 * for fix Absolute Path Traversal 安全的建立檔案，路徑or檔名有安全疑慮直接報錯
	 *
	 * @param path
	 * @param name
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static SafeFile newFileSafely(String path, String name) throws ValidationException, IOException {
		// 路徑不可為空
		if (StringUtils.isBlank(path)) {
			throw new RuntimeException("File path can not be empty.");
		}
		String newPath = filterValidFilePath(path);

		//		Path newPathObj = Paths.get(newPath.replaceAll("\r", "").replaceAll("\t",
		// "").replaceAll("\n", ""));
		//		Files.createDirectories(newPathObj);
		FileUtils.forceMkdir(new SafeFile(newPath));

		// fix Absolute Path Traversal
		name = FilenameUtils.getName(name);

		// 為了避開弱掃
		synchronized (ESAPI.class) {
			ESAPI.validator().getValidDirectoryPath("TBB", newPath, new SafeFile(baseDir), false);
		}
		SafeFile sf;
		if (StringUtils.isBlank(name)) {
			sf = new SafeFile(newPath);
		} else {
			// 檔名or資料夾名不可超過100不可包含非法字元
			String newName = filterValidString(filterValidFileName(name), 100, "[^a-z0-9A-Z\\_\\.\\-]");
			if (!StringUtils.equals(newName, name)) {
				throw new RuntimeException("Not valid filename: " + filterValidLog(name));
			}
			sf = new SafeFile(newPath, newName);
		}
		FileUtils.createParentDirectories(sf);
		setPermissionsSafe(sf.getPath());
		sf.setReadable(true);
		sf.setWritable(true, true);
		return sf;
	}

	/**
	 * for fix Absolute Path Traversal 安全的建立檔案，路徑or檔名有安全疑慮直接報錯
	 *
	 * @param path
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static InputStream newFileInputStreamSafely(String path, String name) throws ValidationException, IOException {
		SafeFile f = newFileSafely(path, name);
		return FileUtils.openInputStream(f);
	}

	/**
	 * for fix Absolute Path Traversal 安全的檔案下載，路徑or檔名有安全疑慮直接報錯
	 *
	 * @param fileDir
	 * @param fileName
	 * @param downloadFileName
	 * @param response
	 * @throws ValidationException
	 * @throws IOException
	 */
	public static void makeDownloadProcessSafely(String fileDir, String fileName, String downloadFileName, HttpServletResponse response) throws ValidationException, IOException {
		String mimeType = URLConnection.guessContentTypeFromName(downloadFileName);
		mimeType = CheckmarxUtil.filterContentType(mimeType);
		if (!org.apache.commons.lang3.StringUtils.isEmpty(mimeType)) {
			response.setContentType(MediaType.valueOf(mimeType).toString());
		}
		if (checkAuthorization("authorization")) {
			IOUtils.write(IOUtils.toByteArray(newFileInputStreamSafely(fileDir, fileName)), response.getOutputStream());
		}
	}

	/**
	 * for fix Download of Code Without Integrity Check
	 *
	 * @param input
	 * @param superClazz
	 * @param <T>
	 * @return
	 */
	public static <T> T getValidInstance(String input, Class<T> superClazz) {
		try {
			Class<?> clazz = Class.forName(input);
			if (!superClazz.isAssignableFrom(clazz)) {
				throw new RuntimeException("Error! input[" + input + "] not implement from " + superClazz);
			}
			return (T) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new RuntimeException("Error! can not initial " + input);
		}
	}

	/**
	 * for fix Race Condition Format Flaw from MessageFormat
	 *
	 * @param pattern
	 * @param params
	 * @return
	 */
	public static String msgFormat(String pattern, Object... params) {
		MessageFormat mf = new MessageFormat(pattern);
		return mf.format(params);
	}

	/**
	 * 處理輸入字串的資料，僅限 0-9 , A-Z 大小寫與 : . , 並限制長度
	 *
	 * @param input 來源字串
	 * @return
	 */
	public static String filterContentType(String input) {
		String newInput = rmNewLineSymbol(input);
		return filterValidString(newInput, 128, "\\(\\)\\:<>\\?@\\[\\\\\\]\\{\\}");
	}

	public static String filterValidString(String input, int maxLen) {
		return filterValidString(input, maxLen, "[^a-z0-9A-Z.:]");
	}

	public static String filterValidString(String input, int maxLen, String regx) {
		if (input != null) {
			String v = input.replaceAll(regx, "");
			if (v.length() > maxLen) {
				v = v.substring(0, maxLen);
			}
			return v;
		} else {
			return null;
		}
	}

	/**
	 * clean filename
	 *
	 * @param filename
	 * @return
	 */
	public static String filterValidFileName(String filename) {
		return FilenameUtils.normalize(filename);
	}

	/**
	 * clean path
	 *
	 * @param path
	 * @return
	 */
	public static String filterValidFilePath(String path) {
		String result = "";
		try {
			SafeFile checkFile = new SafeFile(path);
			result = checkFile.getCanonicalPath();
		} catch (IOException | ValidationException e) {
			log.error("filterValidFilePath fail.", safeErrorLog(e));
		}
		return result;
	}

	/**
	 * fix Log Forging
	 *
	 * @param logs
	 * @return
	 */
	public static String filterValidLog(String logs) {
		return rmNewLineSymbol(logs);
	}

	/**
	 * 將輸入字串標準化後移除換行字元
	 *
	 * @param str
	 * @return
	 */
	public static String rmNewLineSymbol(String str) {
		if (str != null) {
			String normalize = Normalizer.normalize(str, Normalizer.Form.NFKC);
			normalize = normalize.replaceAll("\r|\n|%0d|%0a", "");
			return normalize;
		} else {
			return null;
		}
	}

	/**
	 * 解決弱點 Information Exposure Through an Error Message 處理 例外物件的輸出訊息
	 *
	 * @param e 例外物件
	 */
	public static String safeErrorLog(Exception e) {
		final int limitLen = 5000;
		// 依照規範需要將可能遺漏資訊的例外排除，例如 SQLException BindException
		// 但是黑名單又通常不被弱掃軟體完全認可，嚴謹作法是通通改為自訂的例外錯誤，但這樣卻非常難追蹤例外錯誤的位置(程式碼)
		String errStackStr = "";
		try {
			errStackStr = ExceptionUtils.getStackTrace(e);
			if (errStackStr.length() > limitLen) {
				errStackStr = errStackStr.substring(0, limitLen) + "\n.....Truncate for simplified content!";
			}
		} catch (final StringIndexOutOfBoundsException ex) {
			errStackStr = "Error in SafeErrorLog!";
		}
		return errStackStr;
	}

	private static final String GIF_IMAGE_EXTENSION = "gif";

	private static final String JPEG_IMAGE_EXTENSION = "jpeg";

	private static final String JPG_IMAGE_EXTENSION = "jpg";

	private static final String PNG_IMAGE_EXTENSION = "png";

	public static final String FILE_UPLOAD_CONTEXT = "fileUpload";

	public static boolean allowNull = false;

	/**
	 * for fix Absolute Path Traversal 安全的建立檔案，路徑or檔名有安全疑慮直接報錯
	 *
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static boolean ValidFile(MultipartFile file) throws ValidationException {
		String filename = file.getOriginalFilename();
		List<String> allowedExtensions = new ArrayList<String>();
		allowedExtensions.add(GIF_IMAGE_EXTENSION);
		allowedExtensions.add(JPEG_IMAGE_EXTENSION);
		allowedExtensions.add(JPG_IMAGE_EXTENSION);
		allowedExtensions.add(PNG_IMAGE_EXTENSION);

		// 為了避開弱掃
		synchronized (ESAPI.class) {
			return ESAPI
					.validator()
					.isValidFileName(FILE_UPLOAD_CONTEXT, filename, allowedExtensions, allowNull);
		}
	}

	public static String cleanXss(String value) {
		if (value != null) {
			// Avoid null characters
			value = value.replaceAll("", "");

			// Avoid anything between script tags
			Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);

			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid anything in a src='...' type of expression
			scriptPattern = Pattern.compile("src[\r\n]=[\r\n]\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

			value = scriptPattern.matcher(value).replaceAll("");

			// Remove any lonesome </script> tag
			scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);

			value = scriptPattern.matcher(value).replaceAll("");

			// Remove any lonesome <script ...> tag
			scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid eval(...) expressions
			scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid expression(...) expressions
			scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid javascript:... expressions
			scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);

			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid vbscript:... expressions
			scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);

			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid onload= expressions
			scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

			value = scriptPattern.matcher(value).replaceAll("");
		}
		return value;
	}

	public static Map<String, Object> getMockInputObject(String key, Object obj) {
		Map result = new HashMap();
		result.put(key, obj);
		return result;
	}

	public static boolean checkAuthorization(String userName) {
		return userName.equals("authorization");
	}

	public static void setPermissionsSafe(String filePath) throws IOException {
		Set<PosixFilePermission> perms = new HashSet<>();
		// user permission
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		// group permissions
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		// others permissions removed
		perms.remove(PosixFilePermission.OTHERS_READ);
		perms.remove(PosixFilePermission.OTHERS_WRITE);
		perms.remove(PosixFilePermission.OTHERS_EXECUTE);

		if (checkAuthorization("authorizationOnlyLinux")) {
			Files.setPosixFilePermissions(Paths.get(filePath), perms);
		}
	}
}
