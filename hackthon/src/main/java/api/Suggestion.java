/**
 *  Copyright 2011 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import util.SuggestionProperties;

import domain.EuropeanaEntity;
import domain.EuropeanaEntityMapper;
import domain.GoogleQuery2Entity;
import domain.GoogleQuery2Entity.Result;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
public class Suggestion {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Suggestion.class);
	
	private static SolrServer server = null;
	
	private static Suggestion sugg = null;
	private Suggestion(){
		server = getSolrServer();
	}
	
	public static Suggestion getInstance(){
		if (sugg == null) sugg = new Suggestion();
		return sugg;
	}
	
	public List<EuropeanaEntity> getSuggestedEntities(String query){
		if (query == null ) return null;
		List<EuropeanaEntity> suggestedEntities = new ArrayList<EuropeanaEntity>();
		List<SuggestedItem> sugg = filterSuggestions(query, getSuggestion(query)); 
		for (SuggestedItem si : sugg ){
			if (!si.getLabel().isEmpty()){
			EuropeanaEntity ee = EuropeanaEntityMapper.getInstanceFromQuery(si.getLabel());
			ee.setExplaination(si.getExplain());
			suggestedEntities.add(ee);
			}
		}
		return suggestedEntities;
	}
	
	public List<SuggestedItem> filterSuggestions(String query,List<String> suggestion){
		String[] explaination = new String[suggestion.size()];
		CleanerSuggestionThread[] cst = new CleanerSuggestionThread[suggestion.size()];
		for (int i = 0; i < suggestion.size(); i++){
			try {
				cst[i] = new CleanerSuggestionThread(suggestion,explaination,query, i);
			} catch (IOException e) {
			}
			cst[i].start();
			try{
				Thread.sleep(50);
			}
			catch(Exception e){
				
			}
			
		}
		for (int i = 0; i < suggestion.size(); i++){
			try {
				if (cst[i] != null)
					cst[i].join();
			} catch (InterruptedException e) {
				
			}
		}
		List<SuggestedItem> cleanSuggestion = new ArrayList<SuggestedItem>();
		for (int i = 0; i < suggestion.size(); i++){
			if (!suggestion.get(i).isEmpty()){
				cleanSuggestion.add(new SuggestedItem(suggestion.get(i), explaination[i]));
			}
		}
		return cleanSuggestion;
	}
	
	
	public List<String> getSuggestion(String query){
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		// sets the number of results
		solrQuery.setRows(20);
		solrQuery.setFilterQueries("freq:[15 TO *]");
		QueryResponse rsp = null;
		try {
			rsp = server.query(solrQuery);
		} catch (SolrServerException e) {
			logger.error("The suggestion server can not reply {}",e.toString());
			//e.printStackTrace();
			return null;
		}catch(Exception e){
			logger.error("Problem producing the suggestions "+e.toString());
			return null;
		}
		SolrDocumentList docs = rsp.getResults();
		List<String> suggestions = new ArrayList<String>();
		for (SolrDocument d : docs) {
			String value = (String)d.getFieldValue("sem_label");
			value = value.replaceAll("%28.*%29"," ");
			suggestions.add(value);
		}
		return suggestions;
	}
	
	/**
	 * Returns the instance of solr server, just one shared by all. If there are
	 * problem with the server it exits.
	 * 
	 * @return the current instance of the solr server
	 * 
	 */
	private static SolrServer getSolrServer() {
		if (server == null){
			try {
				server = new CommonsHttpSolrServer("http://ornellaia.isti.cnr.it:8983/solr");
			} catch (MalformedURLException e) {
				logger.error("instantiating the solr server"+e);
				System.exit(-1);
			}
		}
		return server;

	}
	
	public class SuggestedItem{
		public String label; 
		public String explain;
		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}
		/**
		 * @param label the label to set
		 */
		public void setLabel(String label) {
			this.label = label;
		}
		/**
		 * @return the explain
		 */
		public String getExplain() {
			return explain;
		}
		/**
		 * @param explain the explain to set
		 */
		public void setExplain(String explain) {
			this.explain = explain;
		}
		/**
		 * @param label
		 * @param explain
		 */
		public SuggestedItem(String label, String explain) {
			super();
			this.label = label;
			this.explain = explain;
		}
		
	}
	
	public class CleanerSuggestionThread extends Thread {
		
	
		
		int id;
		String query;
		private List<String> suggestion;
		private String[] explaination;

		public CleanerSuggestionThread(List<String> suggestion,String[] explaination, String query,
				 int id) throws IOException {
			this.suggestion = suggestion;
			this.explaination = explaination;
			this.id = id;
			this.query=query.replaceAll(" ", "+").toLowerCase();
		}

		public void run() {
			logger.info("thread " + id + " start");
			String candidateQuery = suggestion.get(id).replaceAll(" ", "+").toLowerCase();
			String resultSnippet = null;
			logger.debug("query {} candidate {}",query,candidateQuery);
			try {
				List<Result> res = GoogleQuery2Entity.getInstance().getResults("http://www.google.com/search?hl=en&source=hp&biw=887&bih=508&q="+query+"+"+candidateQuery+"+site:wikipedia.org");
				if (res.size() == 0) suggestion.set(id, "");
				else{
					resultSnippet = res.get(0).getSummary().toLowerCase();
				}
			} catch (InterruptedException e) {
				suggestion.set(id, "");
		
			}
			boolean matchedInQuery = false;
			boolean matchedInCandidateQuery = false;
			for (String term : query.split("[+]")){
				if (resultSnippet.contains(term)){
					resultSnippet = resultSnippet.replaceAll(term,"<b>"+term+"</b>");
					matchedInQuery = true;
				}
			}
			for (String term : candidateQuery.split("[+]")){
				if (resultSnippet.contains(term)){
					resultSnippet= resultSnippet.replaceAll(term,"<b>"+term+"</b>");
					matchedInCandidateQuery = true;
				}
			}
			if (matchedInCandidateQuery && matchedInQuery) explaination[id] = resultSnippet;
			else suggestion.set(id,"");
		}
	}
}
