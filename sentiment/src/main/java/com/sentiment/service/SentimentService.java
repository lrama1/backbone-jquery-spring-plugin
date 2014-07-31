package com.sentiment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;














import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
//import the domain
import com.sentiment.web.domain.Sentiment;
import com.sentiment.web.domain.SentimentResult;
import com.sentiment.common.ListWrapper;
import com.sentiment.dao.SentimentDAO;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
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
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service
public class SentimentService {

	private Logger logger = ESAPI.getLogger(this.getClass());
	
	@Autowired
	SentimentDAO sentimentDAO;
	
	private static StanfordCoreNLP pipeline; 
	//private static Map<String, String> words = new LinkedHashMap<String, String>();
	private static Multimap<String, String> words = LinkedHashMultimap.create();
	
	private static DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
	
	private static StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
	//private static Directory index = new RAMDirectory();
	
	@PostConstruct
	private void init(){
		doubleMetaphone.setMaxCodeLen(6);
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
	    pipeline = new StanfordCoreNLP(props);
	    loadWords();

	}

	private void createIndexWriterOfAspects(StandardAnalyzer analyzer, Directory index, String aspects)
			throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42, analyzer);

		IndexWriter w = new IndexWriter(index, config);
		String[] aspectsToken = aspects.split(",");
		for(String token : aspectsToken){
			addDoc(w, token);
			System.out.println("Adding aspect: " + token + "***********************");
		}
		w.close();
	}
	
	private static void addDoc(IndexWriter w, String title) throws IOException {
		  Document doc = new Document();
		  doc.add(new TextField("content", title, Store.YES));
		  
		  DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
		  String[] tokens = title.split("\\s");
		  StringWriter stringWriter = new StringWriter();
		  for(int i = 0; i < tokens.length; i++){
			  String encoded = doubleMetaphone.doubleMetaphone(tokens[i]);			  
			  stringWriter.append(encoded + " ");
		  }		  
		  System.out.println("Indexing: " + stringWriter.toString());
		  doc.add(new TextField("contentmetaphone", stringWriter.toString(), Store.YES));		 
		  w.addDocument(doc);
		}
	
	private void findByMetaphone(Directory index, Analyzer analyzer, String customerComment) throws Exception{
		DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
		doubleMetaphone.setMaxCodeLen(6);
		QueryParser parser = new QueryParser(Version.LUCENE_42, "contentmetaphone", analyzer);		
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		
		Query query = parser.parse(customerComment);
		Set<Term> terms = new LinkedHashSet<Term>();
		query.extractTerms(terms);
		StringWriter stringWriter = new StringWriter();
		for(Term term : terms){
			System.out.println(term.text());
			stringWriter.append(doubleMetaphone.doubleMetaphone(term.text()) + " ");
		}
		System.out.println("Searching for: " + stringWriter.toString());
		searcher.search(parser.parse(stringWriter.toString()), collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;		
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;++i) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    System.out.println((i + 1) + ". " + d.get("content") + "\t" + d.get("contentmetaphone") + " " + hits[i].score);
		}
	}
	
	public ListWrapper<Sentiment> getSentiments(int pageNumber, int pageSize,
			String sortByAttribute, String sortDirection) {
		return sentimentDAO.getSentiments(pageNumber, pageSize,
				sortByAttribute, sortDirection);
	}

	public Sentiment getSentiment(String id) {
		return sentimentDAO.getSentiment(id);
	}
	
	public Sentiment evaluateSentiment(Sentiment sentiment) throws Exception{
		logger.info(Logger.EVENT_SUCCESS, sentiment.toString());		

		Directory index = new RAMDirectory();
		createIndexWriterOfAspects(analyzer, index, sentiment.getSampleAspects());
		
		
	    // read some text in the text variable
	    String text = sentiment.getSentimentText().toLowerCase();
	    
	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    
	    
	    
	    
	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        System.out.println("Token: " + word + "==" + doubleMetaphone.doubleMetaphone(word));
	        if(!words.containsValue(word)){
	        	System.out.println("Could not recognize word: " + word);
	        	String metaphone = doubleMetaphone.doubleMetaphone(word);
	        	for(String candidate: words.get(metaphone)){
	        		//System.out.println("Did u mean: " + candidate);
	        	}
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
	      sentiment.getResults().add(new SentimentResult(sentimentResult, sentence.toString()));
	      
	      findByMetaphone(index, analyzer, sentence.toString());
	      
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(SentimentService.class.getResourceAsStream("/wordsEn.txt")));
		String line = "";
		try {
			logger.info(Logger.EVENT_SUCCESS, "Loading words");
			while((line = reader.readLine()) != null){
				words.put(doubleMetaphone.doubleMetaphone(line),line.trim());
			}
			logger.info(Logger.EVENT_SUCCESS, "Done loading words.  Mapsize: " + words.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
