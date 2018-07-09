package com.infinitus.husky;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class XmlUtil {
	/**
	 * load 载入一个xml文档
	 * 
	 * @return 成功返回Document对象，失败返回null
	 * @param filepath
	 *            文件路径
	 */
	public static Document load(String filepath) {
		Document document = null;
		try {
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(new File(filepath));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return document;
	}

	/**
	 * xml转换成JavaBean
	 * 
	 * @param xml
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T converyToJavaBean(String xml, Class<T> c) {
		T t = null;
		try {
			JAXBContext context = JAXBContext.newInstance(c);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			t = (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * JavaBean转换成xml
	 * 
	 * @param obj
	 * @param encoding
	 * @return
	 */
	public static String convertToXml(Object obj, String encoding) {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			StringWriter write = new StringWriter();
			marshaller.marshal(obj, write);
			result = write.toString();
			write.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String convertToXml(Object obj) {
		return convertToXml(obj,"UTF-8");
	}

	/**
	 * 从xml中获取获取指定的节点值
	 * @param xmlStr
	 * @param xpath
	 * @return
	 * @throws DocumentException
     */
	public static String getSingleNodeValue(String xmlStr,String xpath) throws DocumentException {
		if(StringUtils.isBlank(xmlStr) || StringUtils.isBlank(xpath) ){
			return null;
		}
		Document document = DocumentHelper.parseText(xmlStr);
		Element element = document.getRootElement();
		Node node =  element.selectSingleNode(xpath);
		if (node == null) {
			return null;
		}
		return node.getStringValue();
	}

	public static List<Element> getXpath(String xmlStr,String xpath)throws DocumentException{
		Document document = DocumentHelper.parseText(xmlStr);
		return document.selectNodes(xpath);
	}
}
