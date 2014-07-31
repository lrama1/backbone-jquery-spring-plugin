package com.sentiment.web.domain;

public class SentimentResult {
	private String evaluation;
	private String text;
	
	public SentimentResult(){
		
	}
	
	public SentimentResult(String evaluation, String text) {
		super();
		this.evaluation = evaluation;
		this.text = text;
	}
	
	public String getEvaluation() {
		return evaluation;
	}
	
	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	
}
