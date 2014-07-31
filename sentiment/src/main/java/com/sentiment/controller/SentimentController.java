package com.sentiment.controller;

import java.security.Principal;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sentiment.common.ListWrapper;
import com.sentiment.security.SampleUserDetails;
import com.sentiment.service.SentimentService;
//import the domain
import com.sentiment.web.domain.Sentiment;

@Controller
public class SentimentController {

	private Logger logger = ESAPI.getLogger(this.getClass());

	@Autowired
	SentimentService sentimentService;
	
	
	@RequestMapping(value = "/sentiment/{id}", method = RequestMethod.GET)
	public @ResponseBody
	Sentiment getSentiment(@PathVariable("id") String id, Principal principal) {
		Authentication authenticationToken = (Authentication) principal;
		SampleUserDetails user = (SampleUserDetails) authenticationToken
				.getPrincipal();
		return new Sentiment();
	}

	@RequestMapping(value = "/sentiment", headers = { "accept=application/json" }, method = RequestMethod.POST)
	public @ResponseBody
	Sentiment saveNewSentiment(@RequestBody Sentiment sentiment) throws Exception {
		return sentimentService.evaluateSentiment(sentiment);
	}

	@RequestMapping(value = "/sentiment/{id}", headers = { "accept=application/json" }, method = RequestMethod.PUT)
	public @ResponseBody
	Sentiment updateSentiment(@RequestBody Sentiment sentiment) {
		logger.info(Logger.EVENT_SUCCESS, sentiment.toString());
		return sentiment;
	}

	@RequestMapping("/sentiments")
	public @ResponseBody
	ListWrapper<Sentiment> getAllSentiments(
			@RequestParam("page") int pageNumber,
			@RequestParam("per_page") int pageSize,
			@RequestParam(value = "sort_by", required = false) String sortByAttributeName,
			@RequestParam(value = "order", required = false) String sortDirection) {
		return sentimentService.getSentiments(pageNumber, pageSize,
				sortByAttributeName, sortDirection);

	}

	//=============
}
