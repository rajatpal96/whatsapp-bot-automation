package com.skillmap.bot.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;

import com.skillmap.bot.utils.HttpRequestTask.RequestType;
import com.skillmap.bot.utils.TaskResult.TaskStatus;

public class HttpUtils {
	public static TaskResult getResultFromUrl(HttpRequestTask task, boolean exceptionOnFailure) {
		TaskResult result = null;
		try {
			result = getResultFromUrl(task, 0, 0);
		} catch (Exception e) {
			result = new TaskResult();
			result.setStatus(TaskStatus.failed);
		}
		if(result.getStatus() != TaskStatus.completed && exceptionOnFailure)
			throw new RuntimeException("statusCode : "+ result.getStatusCode() +" data:"+result.getData() + "task:" + task);
		return result;
	}
	
	private static TaskResult getResultFromUrl(HttpRequestTask task, Integer urlHitInterval, int redirectionCount) throws Exception {
		Exception finalException = null;
		TaskResult result = new TaskResult();
		if (redirectionCount > 5) {
			result.setStatus(TaskStatus.tooManyRedirections);
			return result;
		}
		int i = 1;
		boolean emptyData = false;
		do {
			try {
				String urlParameters = task.getPayload();

				URL obj = new URL(task.getRequestType() == RequestType.get && StringUtils.isNotBlank(urlParameters) ? task.getUrl() + "?" + urlParameters : task.getUrl());
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setInstanceFollowRedirects(false);
				con.setRequestMethod(task.getRequestType() == RequestType.post ? "POST" : "GET");
				con.setRequestProperty("Accept-Encoding", "gzip");
				con.setRequestProperty("Accept", "*/*");
				con.setConnectTimeout(60000);
				con.setReadTimeout(60000);

				if (task.getHeaderParams() != null) {
					Set<Map.Entry<String, String>> entrySet = task.getHeaderParams().entrySet();
					for (Iterator<Map.Entry<String, String>> it = entrySet.iterator(); it.hasNext();) {
						Map.Entry<String, String> eleEntry = it.next();
						if (eleEntry.getValue() == null)
							continue;
						con.setRequestProperty(eleEntry.getKey(), eleEntry.getValue());
					}
				}

				if (task.getRequestType() == RequestType.post) {
					con.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					wr.writeBytes(ObjectUtils.defaultIfNull(urlParameters, ""));
					wr.flush();
					wr.close();
				}
				result.setStatusCode(con.getResponseCode());
				result.setFinalUrl(task.getUrl());
				String responseCode = con.getResponseCode() + "";
				if (responseCode.matches("[4|5]\\d\\d")) {
					emptyData = true;
					result.setStatus(TaskStatus.failed);
					result.setData(getResponseDataFromStream(con.getErrorStream(), con.getContentEncoding()));
				} else if (responseCode.equals(HttpURLConnection.HTTP_MOVED_PERM + "") || responseCode.equals(HttpURLConnection.HTTP_MOVED_TEMP + "")) {
					String redirectUrl = con.getHeaderField("Location");
					task.setPayload(null);
					task.setRequestType(RequestType.get);
					result = getResultFromUrl(task, urlHitInterval, redirectionCount + 1);
				} else {
					result.setData(getResponseDataFromStream(con.getInputStream(), con.getContentEncoding()));
					result.setStatus(TaskStatus.completed);
				}
			} catch (IOException e) {
				if (urlHitInterval != null) {
					try {
						Thread.sleep(urlHitInterval * (i++));
					} catch (InterruptedException e1) {
					}
				} else {
					// this is done so that loop ends in the first round itself.
					i = 5;
				}
				result.setStatus(TaskStatus.failed);
				finalException = e;
			}
		} while ((i < 5 && (result.getStatus() == null || result.getStatus() == TaskStatus.failed) && !emptyData));
		if (result.getStatus() == null || result.getStatus() == TaskStatus.failed) {
			if (finalException != null)
				throw finalException;
		}
		return result;
	}

	private static String getResponseDataFromStream(InputStream is, String contentEncoding) throws IOException {
		if (is == null)
			return null;
		boolean isZip = StringUtils.containsIgnoreCase(contentEncoding, "gzip");
		if (isZip)
			is = new GZIPInputStream(is);
		return IOUtils.toString(is, StandardCharsets.UTF_8);
	}
	
	public static TaskResult getResultAfterUploadingFile(HttpRequestTask task) {
		TaskResult result = new TaskResult();
		HttpClient client = HttpClientBuilder.create().build();
		
		//TODO this function currently doesn't handles the use case where parameters need to uploaded via form parameters 
		HttpPost post = new HttpPost(task.getUrl() + (task.getPayload() == null ? "" : "?" + task.getPayload()));
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for (Entry<String, File> entry : task.getFileNameMap().entrySet()) 
			builder.addPart(entry.getKey(), new FileBody(entry.getValue()));
		
		post.setEntity(builder.build());
		try {
			HttpResponse response = client.execute(post);
			if(response != null && response.getEntity() != null) {
				Header contentEncoding = response.getEntity().getContentEncoding();
				result.setData(getResponseDataFromStream(response.getEntity().getContent(), contentEncoding == null ? null : contentEncoding.toString()));
			}
			result.setStatus(TaskStatus.completed);
			return result;
		} catch (IOException e1) {
			result.setException(e1);
			result.setStatus(TaskStatus.failed);
			return result;
		}
	}
	
}
