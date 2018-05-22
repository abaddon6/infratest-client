package com.volvo.jvs.infratest.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestConfiguration {

	private String host = "http://localhost:8080";
	
	private String applicationName = "infratest";
	
	private String restAddress = "mq";
	
	private int repeat = 1;
	
	private boolean writeToFile = false;
	
	private boolean writeToOutput = true;
	
	private boolean asserts = false;
	
	private File file = new File("results.txt");
	
	private FileWriter fileWriter;
	
	public TestConfiguration() throws IOException {
		if(System.getProperty("host") != null) {
			host = System.getProperty("host");
		}
		if(System.getProperty("repeat") != null) {
			repeat = Integer.valueOf(System.getProperty("repeat"));
		}
		if(System.getProperty("writeToFile") != null) {
			writeToFile = true;
		}
		if(System.getProperty("noWriteToOutput") != null) {
			writeToOutput = false;
		}
		if(System.getProperty("asserts") != null) {
			asserts = true;
		}
		fileWriter = new FileWriter(file);
	}
	
	public String getTestServerAddress() {
		return host + "/" + applicationName + "/" + restAddress;
	}

	public int getRepeat() {
		return repeat;
	}

	public boolean isWriteToFile() {
		return writeToFile;
	}

	public boolean isAsserts() {
		return asserts;
	}

	public FileWriter getFileWriter() {
		return fileWriter;
	}

	public boolean isWriteToOutput() {
		return writeToOutput;
	}
}
