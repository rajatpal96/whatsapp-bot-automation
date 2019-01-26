package com.skillmap.bot.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Extractor {
	
	public static Object getData(Map<String, Object> data, String key) {
		if(data == null) return null;
		if(!StringUtils.containsAny(key, "."))
			return data.get(key);
		return getData((Map<String, Object>) data.get(key.substring(0, key.indexOf("."))), key.substring(key.indexOf(".")+1));
	}
	
	public static String getStringData(Map<String, Object> data, String key) {
		Object val = getData(data, key);
		return val == null ? null : (String) val;
	}
	
	public static Long getLongData(Map<String, Object> data, String key) {
		Object val = getData(data, key);
		return val == null ? null : Long.valueOf(val.toString());
	}
	
	public static Double getDoubleData(Map<String, Object> data, String key) {
		Object val = getData(data, key);
		return val == null ? null : Double.valueOf(val.toString());
	}
	
	public static List getListData(Map<String, Object> data, String key) {
		Object finaldata = getData(data, key);
		if (finaldata == null)
	       return null;
		return (List) finaldata;
	}
	
	public static Map<String, Object> getMapData(Map<String, Object> data, String key) {
		Object finaldata = getData(data, key);
		if (finaldata == null)
	       return null;
		return (Map<String, Object>) finaldata;
	}
}
