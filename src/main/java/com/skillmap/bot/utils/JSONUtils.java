package com.skillmap.bot.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class JSONUtils {

	private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);
	
	public static Map<String, Object> getJsonMapFromString(String jsonStr) {
		if(StringUtils.isBlank(jsonStr))
			return new HashMap<>();
		ObjectMapper mapper = getDefaultObjectMapper();
		try {
			return mapper.readValue(jsonStr, Map.class);
		} catch (IOException e) {
			logger.error("some error occured in JsonUtils, string was :" + jsonStr, e);
			return new HashMap<>();
		}
	}
	
	public static List<?> getJsonListFromString(String jsonStr) {
		if(StringUtils.isBlank(jsonStr))
			return new ArrayList<>();
		ObjectMapper mapper = getDefaultObjectMapper();
		try {
			return mapper.readValue(jsonStr, List.class);
		} catch (IOException e) {
			logger.error("some error occured in JsonUtils, string was :" + jsonStr, e);
			return new ArrayList<>();
		}
	}
	
	public static Map<String, Object> getJsonMapFromFile(String resourcePath) {
		ObjectMapper mapper = getDefaultObjectMapper();
		try {
			URI fileUrl = new JSONUtils().getClass().getResource(resourcePath).toURI();
			if(fileUrl == null)
				throw new FileNotFoundException(resourcePath+" not available");
			String jsonStr = new String(Files.readAllBytes(Paths.get(fileUrl)));
			return mapper.readValue(jsonStr, Map.class);
		} catch (IOException | URISyntaxException e) {
			logger.error("some error occured in JsonUtils, file was :" + resourcePath, e);
			return new HashMap<>();
		}
	}
	
	public static <T> T getObjectFromFile(String resourcePath, Class<T> clazz) {
		ObjectMapper mapper = getDefaultObjectMapper();
		try {
			URI fileUrl = new JSONUtils().getClass().getResource(resourcePath).toURI();
			if(fileUrl == null)
				throw new FileNotFoundException(resourcePath+" not available");
			String jsonStr = new String(Files.readAllBytes(Paths.get(fileUrl)));
			return mapper.readValue(jsonStr, clazz);
		} catch (IOException | URISyntaxException e) {
			logger.error("some error occured in JsonUtils, file was :" + resourcePath, e);
			return null;
		}
	}
	
	public static <T> T getObjectFromString(String jsonStr, Class<T> clazz) {
		ObjectMapper mapper = getDefaultObjectMapper();
		try {
			return mapper.readValue(jsonStr, clazz);
		} catch (IOException e) {
			logger.error("some error occured in JsonUtils, string was :" + jsonStr, e);
			return null;
		}
	}
	
	public static <T> T getObjectFromJsonStrSubPart(String jsonStr, String jsonPath, Class<T> clazz) {
		if(StringUtils.isEmpty(jsonStr))
			return null;
		ObjectMapper mapper = getDefaultObjectMapper();
		try {
			JsonNode json = mapper.readTree(jsonStr);
			for (String key : jsonPath.split("\\."))
				json = json.get(key);
			return mapper.treeToValue(json, clazz);
		} catch (IOException e) {
			logger.error("some error occured in JsonUtils, string was :" + jsonStr, e);
			return null;
		}
	}
	
	public static <E> List<E> getJsonListFromStrSubPart(String jsonStr, String jsonPath, Class<E> clazz) {
		if(StringUtils.isEmpty(jsonStr))
			return new ArrayList<E>();
		ObjectMapper mapper = getDefaultObjectMapper();
		try {
			JsonNode json = mapper.readTree(jsonStr);
			for (String key : jsonPath.split("\\."))
				json = json.get(key);
			ArrayNode arrNode = (ArrayNode)json;
			List<E> resultList = new ArrayList<>();
			for(int i = 0 ; i < arrNode.size(); i++) {
				E obj = mapper.treeToValue(arrNode.get(i), clazz);
				resultList.add(obj);
			}
			return resultList;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <E> List<E> getJsonListFromString(String jsonStr, Class<E> clazz) {
		if(StringUtils.isEmpty(jsonStr))
			return new ArrayList<E>();
		ObjectMapper mapper = getDefaultObjectMapper();
		try {
			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
			List <E> result = mapper.readValue(jsonStr, type);
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getJsonStrFromObject(Object data) {
		ObjectMapper mapper = getDefaultObjectMapper();
		try {
			return mapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Map<String, Object> getMapFromObject(Object data) {
		ObjectMapper mapper = getDefaultObjectMapper();
		return mapper.convertValue(data, Map.class);
	}
	
	public static <T> T getObjectFromMap(Map<String, Object> dataMap, Class<T> clazz) {
		ObjectMapper mapper = getDefaultObjectMapper();
		return mapper.convertValue(dataMap, clazz);
	}

	public static <E> List<E> getObjectListFromMapList(List<Map<String, Object>> mapList, Class<E> clazz) {
		ObjectMapper mapper = getDefaultObjectMapper();
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
		List <E> result = mapper.convertValue(mapList, type);
		return result;
	}
	
	private static ObjectMapper getDefaultObjectMapper() {
		ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.USE_ANNOTATIONS, true);
		mapper.setAnnotationIntrospector(new AnnotationIntrospectorPair(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()),new JacksonAnnotationIntrospector()));
		return mapper;
	}
}
