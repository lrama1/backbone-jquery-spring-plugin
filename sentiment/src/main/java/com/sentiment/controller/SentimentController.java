package com.sentiment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




















//import the domain
import com.sentiment.web.domain.Sentiment;
import com.sentiment.service.SentimentService;
import com.sentiment.security.SampleUserDetails;
import com.sentiment.common.ListWrapper;
import com.sentiment.common.NameValuePair;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

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
		return sentimentService.getSentiment(id);
	}

	@RequestMapping(value = "/sentiment", headers = { "accept=application/json" }, method = RequestMethod.POST)
	public @ResponseBody
	Sentiment saveNewSentiment(@RequestBody Sentiment sentiment) {
		logger.info(Logger.EVENT_SUCCESS, sentiment.toString());
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	    
	    // read some text in the text variable
	    String text = sentiment.getSentimentText();
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        System.out.println("Token: " + word + "==" + doubleMetaphone.doubleMetaphone(word));
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        // this is the NER label of the token
	        String ne = token.get(NamedEntityTagAnnotation.class);       
	      }

	      // this is the parse tree of the current sentence
	      Tree tree = sentence.get(TreeAnnotation.class);

	      // this is the Stanford dependency graph of the current sentence
	      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
	      
	      String sentimentResult = sentence.get(SentimentCoreAnnotations.ClassName.class);
	      System.out.println(sentimentResult + "\t" + sentence);
	    }

	    // This is the coreference link graph
	    // Each chain stores a set of mentions that link to each other,
	    // along with a method for getting the most representative mention
	    // Both sentence and token offsets start at 1!
	    Map<Integer, CorefChain> graph = 
	      document.get(CorefChainAnnotation.class);
		
		
		
		return sentiment;
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
