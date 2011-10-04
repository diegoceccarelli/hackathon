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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import util.SuggestionProperties;

import domain.EuropeanaEntity;

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
}
