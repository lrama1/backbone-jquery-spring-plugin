package com.sentiment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import the domain
import com.sentiment.web.domain.Sentiment;
import com.sentiment.common.ListWrapper;
import com.sentiment.dao.SentimentDAO;

@Service
public class SentimentService {

	@Autowired
	SentimentDAO sentimentDAO;

	public ListWrapper<Sentiment> getSentiments(int pageNumber, int pageSize,
			String sortByAttribute, String sortDirection) {
		return sentimentDAO.getSentiments(pageNumber, pageSize,
				sortByAttribute, sortDirection);
	}

	public Sentiment getSentiment(String id) {
		return sentimentDAO.getSentiment(id);
	}
}
