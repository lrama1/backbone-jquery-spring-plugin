package com.sentiment.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
//import the domain
import com.sentiment.web.domain.Sentiment;
import com.sentiment.common.ListWrapper;
import com.sentiment.dao.SentimentDAO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Repository
public class SentimentDAO {

	//private List<Sentiment> allSentiment = new ArrayList<Sentiment>();
	private Map<String, Sentiment> allData = new LinkedHashMap<String, Sentiment>();

	@PostConstruct
	public void init() {

		InputStream is = getClass().getResourceAsStream(
				"/sampledata/Sentiments.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				String data[] = line.split(",");
				Sentiment sentiment = new Sentiment();
				//person.setId(data[0]);
				//person.setFirstName(data[1]);
				//person.setLastName(data[2]);
				sentiment.setId(data[0]);
				sentiment.setSentimentText(data[1]);
				sentiment.setSentimentResult(data[2]);
				allData.put(data[0], sentiment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Sentiment getSentiment(String id) {
		return allData.get(id);
	}

	public ListWrapper<Sentiment> getSentiments(int page, int pageSize,
			String sortByAttributeName, String sortDirection) {

		List<Sentiment> allDataList = new ArrayList<Sentiment>(allData.values());
		List<Sentiment> partialPage = new ArrayList<Sentiment>();
		int end = (page * pageSize);
		int start = (end) - pageSize;
		int totalPages = roundUp(allDataList.size(), pageSize);

		if (end > allDataList.size())
			end = allDataList.size();
		if (start < allDataList.size())
			partialPage = allDataList.subList(start, end);
		Long totalRows = new Long(allDataList.size());

		ListWrapper<Sentiment> listWrapper = new ListWrapper<Sentiment>();
		listWrapper.setRows(partialPage);
		listWrapper.setTotalRecords(totalRows.intValue());
		listWrapper.setLastPage(totalPages);
		return listWrapper;
	}

	private int roundUp(int num, int divisor) {
		return (num + divisor - 1) / divisor;
	}
}
