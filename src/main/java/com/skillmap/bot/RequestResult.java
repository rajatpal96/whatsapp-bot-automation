package com.skillmap.bot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;

import com.skillmap.bot.utils.HttpRequestTask;
import com.skillmap.bot.utils.HttpRequestTask.RequestType;
import com.skillmap.bot.utils.HttpUtils;
import com.skillmap.bot.utils.JSONUtils;
import com.skillmap.bot.utils.TaskResult;

public class RequestResult {

	public static void main(String args[]) throws IOException {
		/*String jobId=System.getProperty("jobId");
		String hiringCode=System.getProperty("hCode");*/
		Long jobid=new Long(123456);
		Map<String,Object> param=new HashMap<>();
		param.put("jobId",jobid);
		param.put("hiringCode", "jkdgjkdfjg");
		String url="http://localhost:8080/walkinapp/api/hiring/gettemplate";
		HttpRequestTask task=new HttpRequestTask(RequestType.post, url, 
				ContentType.APPLICATION_FORM_URLENCODED, param);
	    TaskResult result=HttpUtils.getResultFromUrl(task, true);
	    Map<String, Object> msg=JSONUtils.getJsonMapFromString(result.getData());
	    String temp=(String) msg.get("whatsAppTemplate");
	    System.out.println(temp);
	}
	
	
}
