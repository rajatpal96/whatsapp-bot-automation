package com.skillmap.bot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class APIRequest {

	private static final String USER_AGENT = "Mozilla/5.0";

	public Map getRequesthttp(String api, Map<String, Object> headers) throws IOException {

		Map map = null;
		URL obj = new URL(api);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		if (headers != null) {
			for (Map.Entry<String, Object> entry : headers.entrySet()) {
				con.setRequestProperty(entry.getKey(), (String) entry.getValue());
			}
		}
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			map = JSONUtils.getJsonMapFromString(response.toString());
		} else {
			System.out.println("GET request not worked");
		}
		return map;
	}

	public Map deleteRequesthttp(String api, Map<String, Object> headers) throws IOException {

		Map map = null;
		URL obj = new URL(api);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("DELETE");
		if (headers != null) {
			for (Map.Entry<String, Object> entry : headers.entrySet()) {
				con.setRequestProperty(entry.getKey(), (String) entry.getValue());
			}
		}
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			map = JSONUtils.getJsonMapFromString(response.toString());
		} else if (responseCode == HttpURLConnection.HTTP_NO_CONTENT)
			map = new HashMap<>();
		else {
			System.out.println("DELETE request not worked");
		}
		return map;
	}

	public static Map postRequesthttp(String url, Map<String, Object> headers, Map arg0) throws IOException {
		String POST_PARAMS = JSONUtils.getJsonStrFromObject(arg0);
		Map map = null;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		for (Map.Entry<String, Object> entry : headers.entrySet()) {
			con.setRequestProperty(entry.getKey(), (String) entry.getValue());
		}
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String output = response.toString();
			map = JSONUtils.getJsonMapFromString(output);
		} else {
			System.out.println("POST request not worked");
		}
		return map;

	}

	public Map putRequesthttp(String url, Map<String, Object> headers, Map arg0) throws IOException {
		String PUT_PARAMS = JSONUtils.getJsonStrFromObject(arg0);
		Map map = null;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("PUT");
		for (Map.Entry<String, Object> entry : headers.entrySet()) {
			con.setRequestProperty(entry.getKey(), (String) entry.getValue());
		}
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(PUT_PARAMS.getBytes());
		os.flush();
		os.close();
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String output = response.toString();
			map = JSONUtils.getJsonMapFromString(output);
		} else {
			System.out.println("PUT request not worked");
		}
		return map;

	}

	public int postRequesthttpReturnWthStatusCode(String url, Map<String, Object> headers, Map arg0)
			throws IOException {
		String POST_PARAMS = JSONUtils.getJsonStrFromObject(arg0);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		for (Map.Entry<String, Object> entry : headers.entrySet()) {
			con.setRequestProperty(entry.getKey(), (String) entry.getValue());
		}
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();
		int responseCode = con.getResponseCode();
		return responseCode;

	}

}
