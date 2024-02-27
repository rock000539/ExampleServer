/*
 * Copyright (c) 2024 -Parker.
 * All rights reserved.
 */
package com.frame.util;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class SoapRequestParser {

	public static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope";

	private static String convertSOAPMessageToString(SOAPMessage soapMessage) throws SOAPException, TransformerException {
		java.io.StringWriter sw = new java.io.StringWriter();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(new DOMSource(soapMessage.getSOAPPart()), new StreamResult(sw));
		return sw.toString();
	}

	public static String buildSoapRequest(Object headObj, Object bodyObj) {
		try {
			// 創建一個新的SOAPMessage
			SOAPMessage soapMessage = createSOAPMessage();

			// 設定SOAP Envelope的命名空間
			SOAPPart soapPart = soapMessage.getSOAPPart();
			SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
			soapEnvelope.removeNamespaceDeclaration("SOAP-ENV"); // 刪除預設的SOAP-ENV命名空間
			soapEnvelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
			soapEnvelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			soapEnvelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");

			// 使用JAXB將物件對象添加到SOAPMessage的header還有body中
			addObjectToSOAPMessageHeader(soapMessage, headObj);
			addObjectToSOAPMessageBody(soapMessage, bodyObj);

			// 將SOAPMessage的內容輸出為字符串
			String soapMessageString = convertSOAPMessageToString(soapMessage);
			return soapMessageString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 創建一個新的SOAPMessage
	private static SOAPMessage createSOAPMessage() throws SOAPException {
		MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		return messageFactory.createMessage();
	}

	// 使用JAXB將FmState對象添加到SOAPMessage的header中
	private static void addObjectToSOAPMessageHeader(SOAPMessage soapMessage, Object headObj) throws SOAPException, JAXBException {
		SOAPPart soapPart = soapMessage.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		SOAPHeader soapHeader = soapEnvelope.getHeader();

		// 使用JAXBContext創建一個marshaller
		JAXBContext jaxbContext = JAXBContext.newInstance(headObj.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();

		// 將FmState對象marshaller為XML元素，並添加到SOAPMessage的body中
		marshaller.marshal(headObj, soapHeader);
	}

	// 使用JAXB將FmState對象添加到SOAPMessage的body中
	private static void addObjectToSOAPMessageBody(SOAPMessage soapMessage, Object bodyObj) throws SOAPException, JAXBException {
		SOAPPart soapPart = soapMessage.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		SOAPBody soapBody = soapEnvelope.getBody();

		// 使用JAXBContext創建一個marshaller
		JAXBContext jaxbContext = JAXBContext.newInstance(bodyObj.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();

		// 將FmState對象marshaller為XML元素，並添加到SOAPMessage的body中
		marshaller.marshal(bodyObj, soapBody);
	}
}
