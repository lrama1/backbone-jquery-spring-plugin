package com.sentiment.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
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
import org.junit.Test;
//import the domain

public class SentimentControllerTest {

	@Test
	public void testUpdate() {
		
		String[] words = {"lucene", "lusene","terrible", "terible", "teribol"};
		DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
		for(int i =0; i < words.length; i++){
			System.out.println(words[i] + "==" + doubleMetaphone.doubleMetaphone(words[i]));
		}
		
	}
	
	@Test
	public void testLucene() throws Exception{
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		Directory index = new RAMDirectory();

		createIndexWriterOfAspects(analyzer, index);
				
		findByNormalWords(index, analyzer, "the acount sumary page is confusing");
				
		findByMetaphone(index, analyzer, "acount sumary page confusing and");
		
		
	}
	
	private void findByNormalWords(Directory index, Analyzer analyzer, String customerComment) throws Exception{
		QueryParser parser = new QueryParser(Version.LUCENE_42, "content", analyzer);		
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		
		Query query = parser.parse(customerComment);
		Set<Term> terms = new LinkedHashSet<Term>();
		query.extractTerms(terms);
		for(Term term : terms){
			System.out.println(term.text());
		}
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;		
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;++i) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    System.out.println((i + 1) + ". " + d.get("content") + "\t" + d.get("contentmetaphone")+ " " + hits[i].score);
		}
	}
	
	private void findByMetaphone(Directory index, Analyzer analyzer, String customerComment) throws Exception{
		DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
		doubleMetaphone.setMaxCodeLen(6);
		QueryParser parser = new QueryParser(Version.LUCENE_42, "contentmetaphone", analyzer);		
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		
		//String customerComment = "payment historee is complicated";
		String[] tokens = customerComment.split("\\s");
		StringWriter writer = new StringWriter();
		for(String token : tokens){
			writer.append(doubleMetaphone.doubleMetaphone(token) + " ");
		}
		
		System.out.println("Searching for: " + writer.toString());
		searcher.search(parser.parse(writer.toString()), collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;		
		System.out.println("Found " + hits.length + " hits.");
		for(int i=0;i<hits.length;++i) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    System.out.println((i + 1) + ". " + d.get("content") + "\t" + d.get("contentmetaphone") + " " + hits[i].score);
		}
	}

	private void createIndexWriterOfAspects(StandardAnalyzer analyzer, Directory index)
			throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42, analyzer);

		IndexWriter w = new IndexWriter(index, config);
		addDoc(w, "Home Page");
		addDoc(w, "Account Summary Page");
		addDoc(w, "Payment History");
		addDoc(w, "Automated Payment");
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

}