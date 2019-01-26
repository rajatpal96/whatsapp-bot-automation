package com.skillmap.bot.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class TaskResult {
	
	public enum TaskStatus { notStarted, timedOut, humanLoginReq, failedTemporary, tooManyRedirections, running, failed, completed };
	
	private Long taskId;
	
	private String data;
	
	private TaskStatus status;
	
	private int statusCode;
	
	private String exception;
	
	private String finalUrl;
	
	public TaskResult() {
	}
	
	public TaskResult(Long taskId) {
		this();
		this.taskId = taskId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Long getTaskId() {
		return taskId;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getException() {
		return exception;
	}

	public void setException(Exception e) {
		this.exception = ExceptionUtils.getStackTrace(e);
	}

	public void setFinalUrl(String finalUrl) {
		this.finalUrl = finalUrl;
	}

	public String getFinalUrl() {
		return finalUrl;
	}

}
