package com.sentiment.web.domain;

public class Sentiment {
	private String id;
	private String sentimentText;
	private String sentimentResult;

	public String getId() {
		return id;
	}

	public String getSentimentText() {
		return sentimentText;
	}

	public String getSentimentResult() {
		return sentimentResult;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSentimentText(String sentimentText) {
		this.sentimentText = sentimentText;
	}

	public void setSentimentResult(String sentimentResult) {
		this.sentimentResult = sentimentResult;
	}

	public void populateWithSample() {
		int appender = getNextNumber();
		id = "Sample Value id " + appender;
		sentimentText = "Sample Value sentimentText " + appender;
		sentimentResult = "Sample Value sentimentResult " + appender;
	}

	static int sampleCounter = 0;

	private static int getNextNumber() {
		sampleCounter++;
		return sampleCounter;
	}

	public String toString() {
		return "id = " + id + ", sentimentText = " + sentimentText
				+ ", sentimentResult = " + sentimentResult;
	}
}
