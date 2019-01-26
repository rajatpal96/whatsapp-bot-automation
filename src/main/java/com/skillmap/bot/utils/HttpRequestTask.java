package com.skillmap.bot.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestTask {
	
	public enum UserAgent { desktop, mobile }
	
	private String url;
	
	private String userAgent;
	
	private UserAgent userAgentEnum;
	
	public enum RequestType { post, get };
	
	private RequestType requestType;
	
	private String payload;
	
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestTask.class);
	
	private String headersJson;
	
	private Map<String, File> fileNameMap;
	
	protected HttpRequestTask(){
	}
	
	public HttpRequestTask(String url) {
		this.url = url;
	}
	
	public HttpRequestTask(RequestType requestType, String url, ContentType contentType, Map<String, Object> reqData) {
		this(requestType, url, contentType, reqData, null);
	}
	
	public HttpRequestTask(RequestType requestType, String url, ContentType contentType, Map<String, Object> reqData, Map<String, String> headerParams) {
		this(url);
		this.requestType = requestType;
		headerParams = ObjectUtils.defaultIfNull(headerParams, new HashMap<String, String>());
		headerParams.put("Content-Type", contentType.getMimeType());
		this.headersJson = JSONUtils.getJsonStrFromObject(headerParams);
		if(contentType == ContentType.APPLICATION_JSON)
			this.payload = JSONUtils.getJsonStrFromObject(reqData);
		else if(contentType == ContentType.APPLICATION_FORM_URLENCODED) {
			List<String> paramList = new ArrayList<String>();
			for(Map.Entry<String, Object> e : reqData.entrySet()) {
				StringBuilder sb = new StringBuilder();
				try {
					sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append("=");
					sb.append(URLEncoder.encode(String.valueOf(e.getValue()), "UTF-8"));
					paramList.add(sb.toString());
				}catch(UnsupportedEncodingException ex) {
					logger.error("error occured while encoding data for request", ex);
					throw new RuntimeException(ex);
				}
			}
			this.payload = StringUtils.join(paramList, "&");
		}else {
			this.payload = null;
		}
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public String getPayload() {
		return payload;
	}

	public Map<String, String> getHeaderParams() {
		if(StringUtils.isBlank(headersJson))
			return new HashMap<>();
		Map<String, Object> objStrMap = JSONUtils.getJsonMapFromString(headersJson);
		Map<String, String> strHeaderParams = new HashMap<String, String>();
		for(Iterator<Map.Entry<String, Object>> it = objStrMap.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Object> entrySet = it.next();
			strHeaderParams.put(entrySet.getKey(), entrySet.getValue() == null ? null : entrySet.getValue().toString());
		}
		return strHeaderParams;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Map<String, File> getFileNameMap() {
		return fileNameMap;
	}

	public void setFileNameMap(Map<String, File> fileNameMap) {
		this.fileNameMap = fileNameMap;
	}

	public String getUrl() {
		return url;
	}
	
	public String getUserAgent() {
		return userAgent;
	}

}
