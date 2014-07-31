package com.sentiment.web.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Sentiment {
	private String id;
	private String sentimentText;
	private String sentimentResult;
	private List<SentimentResult> results = new ArrayList<SentimentResult>();
	

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
	
	public List<SentimentResult> getResults() {
		return results;
	}

	public void setResults(List<SentimentResult> results) {
		this.results = results;
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
