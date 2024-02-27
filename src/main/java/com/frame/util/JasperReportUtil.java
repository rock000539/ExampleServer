/*
 * Copyright (c) 2022 -Parker.
 * All rights reserved.
 */
package com.frame.util;

import com.frame.model.enums.ExportType;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;
import net.sf.jasperreports.engine.fill.JRTemplateText;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class JasperReportUtil {

	private static final String JRXML_SUFFIX = ".jrxml";

	private static final String PDF_SUFFIX = ".pdf";

	private static final String EXCEL_SUFFIX = ".xls";

	public static File generateReport(String filePath, String fileName, String jrxmlPath, String jrxmlName, Map<String, Object> params, ExportType exportType) throws Exception {
		// 輸出的格式, 目前僅允許 PDF ( jrstyle=pdf ) 及 EXCEL ( jrstyle=xls )
		JasperPrint jasperPrint = getJasperPrint(jrxmlPath, jrxmlName, params);
		removeBlankPage(jasperPrint.getPages());
		switch (exportType) {
			case PDF:
				return savePDFReport(filePath, fileName, jasperPrint);
			case EXCEL:
				return saveExcelReport(filePath, fileName, jasperPrint);
			default:
				return null;
		}
	}

	/**
	 * @apiNote
	 * @param response
	 * @param jrxmlPath jrxml文件路徑
	 * @param jrxmlName jrxml名稱
	 * @param params 畫面參數
	 * @param exportType 輸出類型
	 * @return
	 * @throws JRException
	 * @throws IOException
	 */
	public static void export(HttpServletResponse response, String jrxmlPath, String jrxmlName, Map<String, Object> params, ExportType exportType) throws Exception {
		JasperPrint jasperPrint = getJasperPrint(jrxmlPath, jrxmlName, params);
		// 輸出的格式, 目前僅允許 PDF ( jrstyle=pdf ) 及 EXCEL ( jrstyle=xls )
		if (response != null) {
			switch (exportType) {
				case PDF:
					exportPDFReport(response, jasperPrint);
					break;
				case EXCEL:
					exportExcelReport(response, jasperPrint);
					break;
			}
		}
	}

	private static JasperPrint getJasperPrint(String jrxmlPath, String jrxmlName, Map<String, Object> params) throws Exception {
		if (params == null) {
			params = new HashMap<>();
		}
		Resource resource = new ClassPathResource(File.separator + jrxmlPath + File.separator + jrxmlName + JRXML_SUFFIX);
		String jrxmlFile = resource.getFile().getPath();

		JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
		return changeFontGenReport(jasperPrint);
	}

	// generate PDF
	private static File savePDFReport(String filePath, String fileName, JasperPrint jasperPrint) throws IOException, JRException, ValidationException {
		filePath = new ClassPathResource(File.separator).getPath() + filePath;
		File file = CheckmarxUtil.newFileSafely(filePath + File.separator, fileName + PDF_SUFFIX);
		try (ByteArrayOutputStream pdfOutStream = getPdfOutputStream(jasperPrint);
				OutputStream outputStream = new FileOutputStream(file);) {
			pdfOutStream.writeTo(outputStream);
			return file;
		}
	}

	private static void exportPDFReport(HttpServletResponse response, JasperPrint jasperPrint) throws JRException, IOException {
		try (OutputStream ouputStream = response.getOutputStream();
				ByteArrayOutputStream pdfOutStream = getPdfOutputStream(jasperPrint);) {
			byte bytes[] = pdfOutStream.toByteArray();
			response.reset();
			response.setContentType("application/pdf");
			ouputStream.write(bytes, 0, bytes.length);
		}
	}

	private static ByteArrayOutputStream getPdfOutputStream(JasperPrint jasperPrint) throws IOException, JRException {
		ByteArrayOutputStream pdfOutStream = new ByteArrayOutputStream();
		JRPdfExporter exporter = new JRPdfExporter();
		SimpleExporterInput exporterInput = new SimpleExporterInput(jasperPrint);
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(pdfOutStream);
		exporter.setExporterInput(exporterInput);
		exporter.setExporterOutput(exporterOutput);
		exporter.exportReport();
		return pdfOutStream;
	}

	// generate Excel
	private static File saveExcelReport(String filePath, String fileName, JasperPrint jasperPrint) throws IOException, JRException, ValidationException {
		filePath = new ClassPathResource(File.separator).getPath() + filePath;
		File file = CheckmarxUtil.newFileSafely(filePath + File.separator, fileName + EXCEL_SUFFIX);
		try (ByteArrayOutputStream excelOutStream = getExcelOutputStream(jasperPrint);
				OutputStream outputStream = new FileOutputStream(file);) {
			excelOutStream.writeTo(outputStream);
			return file;
		}
	}

	private static void exportExcelReport(HttpServletResponse response, JasperPrint jasperPrint) throws JRException, IOException {
		try (OutputStream ouputStream = response.getOutputStream();
				ByteArrayOutputStream xlsOutStream = getExcelOutputStream(jasperPrint);) {
			byte bytes[] = xlsOutStream.toByteArray();
			response.reset();
			response.setContentType("application/vnd.ms-excel");
			ouputStream.write(bytes, 0, bytes.length);
		}
	}

	private static ByteArrayOutputStream getExcelOutputStream(JasperPrint jasperPrint) throws IOException, JRException {
		ByteArrayOutputStream xlsOutStream = new ByteArrayOutputStream();
		JRXlsExporter exporter = new JRXlsExporter();
		SimpleExporterInput exporterInput = new SimpleExporterInput(jasperPrint);
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(xlsOutStream);
		SimpleXlsReportConfiguration config = new SimpleXlsReportConfiguration();
		config.setIgnoreCellBorder(true);
		exporter.setExporterInput(exporterInput);
		exporter.setExporterOutput(exporterOutput);
		exporter.setConfiguration(config);
		exporter.exportReport();
		return xlsOutStream;
	}

	private static void removeBlankPage(List<JRPrintPage> pages) {
		for (Iterator<JRPrintPage> i = pages.iterator(); i.hasNext();) {
			JRPrintPage page = i.next();
			if (page.getElements().size() == 0)
				i.remove();
		}
	}

	// 難字轉換
	private static JasperPrint changeFontGenReport(JasperPrint jasperPrint) throws Exception {
		List<JRPrintPage> pages = jasperPrint.getPages();
		// 把page內的每個element逐字去轉
		for (JRPrintPage elements : pages) {
			List<JRPrintElement> inPageElement = elements.getElements();
			for (JRPrintElement wordElement : inPageElement) {
				if (wordElement instanceof JRTemplatePrintText) {
					JRTemplatePrintText jrtp = (JRTemplatePrintText) wordElement;
					JRTemplateText template = (JRTemplateText) jrtp.getTemplate();
					// 難字處理
					StringBuilder sb = new StringBuilder();
					char[] charArr = jrtp.getOriginalText().toCharArray();
					for (char word : charArr) {
						if (word >= 0xE000 && word <= 0xF842) {
							sb.append("<style ").append("fontName=\"EUDC\">");
							sb.append(word);
							sb.append("</style>");
							template.setMarkup("styled");
						} else {
							sb.append(word);
						}
					}

					String cleanXssStr = CheckmarxUtil.cleanXss(sb.toString());
					jrtp.setText(cleanXssStr);
				}
			}
		}
		return jasperPrint;
	}
}
