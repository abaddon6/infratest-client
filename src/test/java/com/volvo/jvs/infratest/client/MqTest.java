package com.volvo.jvs.infratest.client;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volvo.jvs.infratest.beans.TestCaseResult;
import com.volvo.jvs.infratest.beans.TestStatus;
import com.volvo.jvs.infratest.beans.mq.MqTestResult;

@SpringBootTest
public class MqTest {

	private TestConfiguration testConfiguration;

	private int counter = 0;
	
	@Before
	public void configuration() throws IOException {
		testConfiguration = new TestConfiguration();		
	}
	
	@Test
	public void test() throws Exception {		
		writeHeaderToFile();
		for(;counter < testConfiguration.getRepeat(); counter++) {
			writeHeaderToOutput("Small messages test", 5, 100);
			doTest(5, 100, 2000, 2000);
			
			writeHeaderToOutput("Big messages test", 5, 10 * 1024);
			doTest(5, 10 * 1024, 10000, 25000);
			
			writeHeaderToOutput("Many small messages test", 100, 100);
			doTest(100, 100, 2000, 2000);
		}
	}

	protected void doTest(int numberOfMessages, int messageSize, long maxSendTime,
			long maxReceiveTime) throws Exception{
				
		String url = getTestConfiguration().getTestServerAddress()
				+ "/test"
				+ "?numberOfMessages=" + numberOfMessages 
				+ "&messageSize=" + messageSize;
		new TestExecutor(url, HttpMethod.PUT).start();
				
	    // check result
		URL resultUrl = new URL(getTestConfiguration().getTestServerAddress() 
				+ "/result");
		ObjectMapper mapper = new ObjectMapper();
		MqTestResult result = mapper.readValue(resultUrl, MqTestResult.class);

		while(result.getTestStatus().equals(TestStatus.ONGOING) 
				|| result.getNumberOfMessages() != numberOfMessages
				|| result.getMessageSize() != messageSize) {
			Thread.sleep(1000);
			result = mapper.readValue(resultUrl, MqTestResult.class);
		}
		        
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getTestCasesResult());
		Assert.assertTrue(result.getTestCasesResult().size() == numberOfMessages);
				
		// calculate results
		long sendTimeSum = 0;
		long sendMqTimeSum = 0;
		long receiveTimeSum = 0;
		for(TestCaseResult testCaseResult : result.getTestCasesResult()) {
			sendTimeSum += testCaseResult.getSendTime();
			sendMqTimeSum += testCaseResult.getSendTimeFromMqServer();
			receiveTimeSum += testCaseResult.getReceiveTime();
		}
		long sendTimeAvg = sendTimeSum / numberOfMessages;
		long sendTimeMqAvg = sendMqTimeSum / numberOfMessages;
		long receiveTimeAvg = receiveTimeSum / numberOfMessages;			
		
		writeRowToOutput(sendTimeAvg, sendTimeMqAvg, receiveTimeAvg);
		writeRowToFile(numberOfMessages, messageSize, sendTimeAvg,
				sendTimeMqAvg, receiveTimeAvg);		
				
		if(testConfiguration.isAsserts()) {
			if(testConfiguration.isWriteToOutput()) {
				System.out.println("Constrains checking...");
			}
			Assert.assertTrue(sendTimeAvg <= maxSendTime);
			Assert.assertTrue(receiveTimeAvg <= maxReceiveTime);
			if(testConfiguration.isWriteToOutput()) {
				System.out.println("Checked");
			}
		}
	}
	
	protected void writeHeaderToFile() throws IOException {
		if(testConfiguration.isWriteToFile()) {
			FileWriter fileWriter = testConfiguration.getFileWriter();
			fileWriter.write("No");
			fileWriter.write("\tExecute date");
			fileWriter.write("\tNumber of msg");
			fileWriter.write("\tMsg size");
			fileWriter.write("\tAvg send time");
			fileWriter.write("\tAvg send time (based on mq time)");
			fileWriter.write("\tAvg receive time");
			fileWriter.flush();
		}
	}
	
	protected void writeRowToFile(int numberOfMessages, int messageSize,
			long sendTimeAvg, long sendTimeMqAvg, long receiveTimeAvg)
					throws IOException {
		if(testConfiguration.isWriteToFile()) {
			FileWriter fileWriter = testConfiguration.getFileWriter();
			fileWriter.write("\t\n"+(counter+1));
			fileWriter.write("\t"+new Date());
			fileWriter.write("\t"+numberOfMessages);
			fileWriter.write("\t"+messageSize);
			fileWriter.write("\t"+sendTimeAvg);
			fileWriter.write("\t"+sendTimeMqAvg);
			fileWriter.write("\t"+receiveTimeAvg);
			fileWriter.flush();
		}
	}
	
	protected void writeRowToOutput(long sendTimeAvg, long sendTimeMqAvg, long receiveTimeAvg){
		if(testConfiguration.isWriteToOutput()) {
			
			System.out.println("Average send time: " + sendTimeAvg);
			System.out.println("Average send time (based on mq time): " + sendTimeMqAvg);	
			System.out.println("Average receive time: " + receiveTimeAvg);
		}
	}
	
	protected void writeHeaderToOutput(String title, int numberOfMessages,
			int messageSize) {
		if(testConfiguration.isWriteToOutput()) {
			System.out.println("\n\n" + title);	
			System.out.println("Number of messages: " + numberOfMessages);
			System.out.println("Message size: " + messageSize);
		}
	}

	protected TestConfiguration getTestConfiguration() {
		return testConfiguration;
	}
}
