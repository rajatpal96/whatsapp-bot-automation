package com.skillmap.bot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class XmlUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);
	
	/**
	 * function to convert xml to java object. This is used for calling apis which have default xmls
	 * @param clazz the class to which the converted object belongs to
	 * @param resourcePath the resource pathm this is relative path from web-inf for eg. for a file located in WEB-INF asyncTaskConfig/asyncTasks.xml the resource path would be
	 * /asyncTaskConfig/asyncTasks.xml
	 * @return
	 */
	public static <T> T convertXmlToJavaObjectSilently(Class<T> clazz, String resourcePath) {
		try {
			return convertXmlToJavaObject(clazz, resourcePath);
		}catch(Exception e){
			logger.error("failed while loading data for class :"+clazz+" resourcepath :"+resourcePath, e);
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T convertXmlToJavaObjectSilently(Class<T> clazz, File xmlFile) {
		try {
			return convertXmlToJavaObject(clazz, xmlFile.toURI().toURL());
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T convertXmlToJavaObject(Class<T> clazz, String resourcePath) throws FileNotFoundException {
		URL fileUrl = new XmlUtils().getClass().getResource(resourcePath);
		if(fileUrl == null)
			throw new FileNotFoundException(resourcePath+" not available");
		return convertXmlToJavaObject(clazz, fileUrl);
	}
	
	public static String convertObjectToXml(Object obj) {
		try {
			XmlMapper mapper = getXmlMapperObj();
			byte[] byteArr = mapper.writeValueAsBytes(obj);
			return new String(byteArr);
		} catch(Exception e) {
			logger.error("failed while converting obj to xml", e);
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T convertXmlToJavaObject(Class<T> clazz, URL fileUrl) throws FileNotFoundException {
		try {
			XmlMapper mapper = getXmlMapperObj();
			InputStream is = new FileInputStream(new File(fileUrl.toURI()));
		    T l = mapper.readValue(XMLInputFactory.newInstance().createXMLStreamReader(is), clazz);
		    return l;
		}catch(Exception e){
			logger.error("failed while loading data for class :"+clazz+" resourcepath :"+fileUrl.toString(), e);
			throw new RuntimeException(e);
		}
	} 
	
	public static <T> T convertXmlStringToJavaObject(Class<T> clazz, String xmlString) {
		try {
			XmlMapper mapper = getXmlMapperObj();
		    T l = mapper.readValue(xmlString.trim(), clazz);
		    return l;
		}catch(Exception e){
			logger.error("failed while loading data for class :"+clazz+" xmlString :"+xmlString, e);
			throw new RuntimeException(e);
		}
	}
	
	private static XmlMapper getXmlMapperObj() {
		XmlMapper mapper = new XmlMapper();
		mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
		mapper.setAnnotationIntrospector(new AnnotationIntrospectorPair(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()),new JacksonAnnotationIntrospector()));
		return mapper;
	}

}

