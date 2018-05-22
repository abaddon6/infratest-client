package com.volvo.jvs.infratest.client;

import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.http.HttpMethod;

public class TestExecutor extends Thread{

	private String url;
	
	private HttpMethod httpMethod;
	
	public TestExecutor(String url, HttpMethod httpMethod) {
		if(url == null || httpMethod == null) {
			throw new RuntimeException("Url and http method must be not null.");
		}
		this.url = url;
		this.httpMethod = httpMethod;
	}
	
	@Override
	public void run() {
		super.run();
		try {
			URL testUrl = new URL(getUrl());
			HttpURLConnection testConnection = (HttpURLConnection) testUrl.openConnection();
			testConnection.setRequestMethod(getHttpMethod().name());
			testConnection.getResponseCode();
		}
		catch(Exception e) {
			// do nothing
		}
	}

	protected String getUrl() {
		return url;
	}

	protected HttpMethod getHttpMethod() {
		return httpMethod;
	}
}
