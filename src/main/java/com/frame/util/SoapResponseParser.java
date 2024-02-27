/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.frame.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import lombok.extern.slf4j.Slf4j;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.ADBException;
import org.apache.axis2.databinding.utils.BeanUtil;
import org.apache.axis2.engine.DefaultObjectSupplier;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class SoapResponseParser {

	/**
	 * SOAP XML，自動物件轉換
	 * <p>
	 * elementName會自動對應tag名稱 e.g "fm_rpnet" 從<fm_rpnet diffgr:id="FM_RPNET1"
	 * msdata:roworder="0">開始轉換
	 * <p>
	 * clazz : 直接對應物件欄位名稱，無須額外annotation。所以須注意xml的tag名稱
	 *
	 * @param xmlString Xml資料
	 * @param elementName 欲轉換物件，Xml tag名稱
	 * @param clazz 欲轉換物件，
	 * @return
	 */
	public static <E> E convertXmlToObject(String xmlString, String elementName, Class<E> clazz) {
		try {
			xmlString = validateAndNormalizeSoapXml(xmlString, elementName);
			StringReader stringReader = new StringReader(xmlString);
			OMElement omElement = OMXMLBuilderFactory.createSOAPModelBuilder(stringReader).getDocumentElement();
			// 使用 Axiom 解析 XML
			if (!(omElement instanceof SOAPEnvelope)) {
				throw new ADBException("The provided XML is not a SOAP Envelope");
			}

			SOAPEnvelope soapEnvelope = (SOAPEnvelope) omElement;

			// 獲取 SOAP Body
			SOAPBody soapBody = soapEnvelope.getBody();
			if (soapBody == null) {
				throw new ADBException("SOAP Body not found in the XML");
			}

			OMElement targetElement;
			targetElement = findElementByName(soapBody, elementName);

			if (targetElement == null) {
				throw new ADBException("Element not found in the XML");
			}

			// 解析元素
			return convertElementToObject(targetElement, clazz);
		} catch (Exception e) {
			log.error("Error converting XML to Object", e);
		}
		return null;
	}

	private static OMElement findElementByName(OMElement sourceElement, String targetName) {
		OMElement result = null;
		Iterator<OMElement> elementsIterator = sourceElement.getChildElements();
		while (elementsIterator.hasNext()) {
			OMElement element = (OMElement) elementsIterator.next();
			String localName = element.getLocalName();
			if (localName.equals(targetName)) {
				return element;
			}
			result = findElementByName(element, targetName);

			if (result != null) {
				return result;
			}
		}
		return result;
	}

	public static <E> E convertElementToObject(OMElement element, Class<E> clazz) throws AxisFault {
		if (element == null) {
			return null;
		}
		return (E) BeanUtil.processObject(element, clazz, null, true, new DefaultObjectSupplier(), null);
	}

	/**
	 * 透過Xml 自動name，來查找OMElement
	 *
	 * @param sourceElement Xml資料
	 * @param targetName 欲轉換Xml
	 * @return OMElement
	 */
	private static OMElement findElementByAttributeValue(OMElement sourceElement, String targetName) {
		OMElement result = null;
		Iterator<OMElement> elementsIterator = sourceElement.getChildElements();
		while (elementsIterator.hasNext()) {
			OMElement element = (OMElement) elementsIterator.next();

			String attributeValue = element.getAttributeValue(new QName("name"));
			if (StringUtils.isNotBlank(attributeValue) && attributeValue.equals(targetName)) {
				return element;
			}
			result = findElementByAttributeValue(element, targetName);

			if (result != null) {
				return result;
			}
		}
		return result;
	}

	private static String validateAndNormalizeSoapXml(String xmlString, String targetName) {
		String lowTargetName = targetName.toLowerCase();
		xmlString = xmlString.replaceAll(lowTargetName, targetName);

		xmlString = xmlString.replace(":envelope", ":Envelope");
		xmlString = xmlString.replace(":body", ":Body");
		return xmlString;
	}

	/**
	 * SOAP XML，自動物件List轉換
	 * <p>
	 * elementName會自動對應tag名稱 e.g "fm_rpnet" 從<fm_rpnet diffgr:id="FM_RPNET1"
	 * msdata:roworder="0">開始轉換
	 * <p>
	 * clazz : 直接對應物件欄位名稱，無須額外annotation。所以須注意xml的tag名稱
	 * <p>
	 * List資料，階層需在同層
	 *
	 * @param xmlString Xml資料
	 * @param elementName 欲轉換物件，Xml tag名稱
	 * @param clazz 欲轉換物件，
	 * @return
	 */
	public static <E> List<E> convertXmlToObjectList(String xmlString, String elementName, Class<E> clazz) {
		try {
			xmlString = validateAndNormalizeSoapXml(xmlString, elementName);
			StringReader stringReader = new StringReader(xmlString);
			OMElement omElement = OMXMLBuilderFactory.createSOAPModelBuilder(stringReader).getDocumentElement();

			// 使用 Axiom 解析 XML
			if (!(omElement instanceof SOAPEnvelope)) {
				throw new ADBException("The provided XML is not a SOAP Envelope");
			}

			SOAPEnvelope soapEnvelope = (SOAPEnvelope) omElement;

			// 獲取 SOAP Body
			SOAPBody soapBody = soapEnvelope.getBody();
			if (soapBody == null) {
				throw new ADBException("SOAP Body not found in the XML");
			}

			List<OMElement> targetElements = findElementsByName(soapBody, elementName);

			if (CollectionUtils.isEmpty(targetElements)) {
				throw new ADBException("Element not found in the XML");
			}

			// 解析元素列表
			List<E> result = new ArrayList<>();
			for (OMElement targetElement : targetElements) {
				result.add(convertElementToObject(targetElement, clazz));
			}
			return result;
		} catch (Exception e) {
			log.error("Error converting XML to List", e);
		}
		return null;
	}

	private static List<OMElement> findElementsByName(OMElement sourceElement, String targetName) {
		List<OMElement> result = new ArrayList<>();
		Iterator<OMElement> elementsIterator = sourceElement.getChildElements();
		while (elementsIterator.hasNext()) {
			OMElement element = (OMElement) elementsIterator.next();
			String localName = element.getLocalName();
			if (localName.equals(targetName)) {
				// return element;
				result.add(element);
			}
			List<OMElement> childResult = findElementsByName(element, targetName);

			if (CollectionUtils.isNotEmpty(childResult)) {
				// return result;
				result.addAll(childResult);
			}
		}
		return result;
	}
}
