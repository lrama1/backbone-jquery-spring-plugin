package com.sentiment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;







//import the domain
import com.sentiment.web.domain.Sentiment;
import com.sentiment.common.ListWrapper;
import com.sentiment.dao.SentimentDAO;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

@Service
public class SentimentService {

	private Logger logger = ESAPI.getLogger(this.getClass());
	
	@Autowired
	SentimentDAO sentimentDAO;
	
	private static StanfordCoreNLP pipeline; 
	private static Map<String, String> words = new LinkedHashMap<String, String>();
	
	@PostConstruct
	private void init(){
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
	    pipeline = new StanfordCoreNLP(props);
	    loadWords();
	}

	public ListWrapper<Sentiment> getSentiments(int pageNumber, int pageSize,
			String sortByAttribute, String sortDirection) {
		return sentimentDAO.getSentiments(pageNumber, pageSize,
				sortByAttribute, sortDirection);
	}

	public Sentiment getSentiment(String id) {
		return sentimentDAO.getSentiment(id);
	}
	
	public Sentiment evaluateSentiment(Sentiment sentiment){
		logger.info(Logger.EVENT_SUCCESS, sentiment.toString());		

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
	        if(!words.containsKey(word)){
	        	System.out.println("Could not recognize word: " + word);
	        }
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
	
	private  void loadWords(){
		DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
		BufferedReader reader = new BufferedReader(new InputStreamReader(SentimentService.class.getResourceAsStream("/wordsEn.txt")));
		String line = "";
		try {
			logger.info(Logger.EVENT_SUCCESS, "Loading words");
			while((line = reader.readLine()) != null){
				words.put(line.trim(), doubleMetaphone.doubleMetaphone(line));
			}
			logger.info(Logger.EVENT_SUCCESS, "Done loading words.  Mapsize: " + words.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
