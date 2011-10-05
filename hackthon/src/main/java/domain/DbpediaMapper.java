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
package domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import domain.Type;

import util.EntityException;
import util.KeyGenerator;
import util.LRUCache;
import util.NoResultException;
import util.SuggestionProperties;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
public class DbpediaMapper {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(DbpediaMapper.class);

	private final static String MAX_RESULT_PARAM = "MaxHits";
	private final static String QUERY_STRING_PARAM = "QueryString";

//	private static KeyGenerator kg = KeyGenerator.getInstance();

	private static LRUCache<Integer,Dbpedia> cache = new LRUCache<Integer,Dbpedia>(10000);
	private String label;
	private String description;
	private String context;
	private List<String> classes;

	private DbpediaMapper() {
	}

	/**
	 * @return the classes
	 */
	public List<String> getClasses() {
		return classes;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	public static Dbpedia getInstanceFromQuery(String query) throws EntityException, NoResultException{
		KeyGenerator kg = new KeyGenerator();

		Integer key = kg.getKey(query);
		if (cache.containsKey(key)) return cache.get(key);
		DbpediaMapper dm = new DbpediaMapper();
		dm.load(query);
		List<Type> types = new ArrayList<Type>();
		for ( String clazz : dm.getClasses()){
			Type t = new Type("dbpedia", clazz);
			types.add(t);
			
		}
		Dbpedia obj = new Dbpedia(dm.getLabel(), dm.getDescription(),types);
		cache.put(key,obj);
		return obj;
		
	}

	private boolean load(String query) throws EntityException, NoResultException {

		boolean success = true;

		label = "";
		description = "";
		String queryUrl = produceQueryUrl(query);

		try {
			Document response = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(queryUrl);

			if (response != null) {

				XPathFactory factory = XPathFactory.newInstance();
				XPath xPath = factory.newXPath();

				NodeList nodes = (NodeList) xPath.evaluate(
						"/ArrayOfResult/Result", response,
						XPathConstants.NODESET);

				if (nodes.getLength() != 0) {

					// WE TAKE THE FIRST ENTITY FROM DBPEDIA
					label = ((String) xPath.evaluate("Label", nodes.item(0),
							XPathConstants.STRING));

					description = ((String) xPath.evaluate("Description",
							nodes.item(0), XPathConstants.STRING))
							.toLowerCase();

					NodeList classesList = (NodeList) xPath.evaluate(
							"Classes/Class", nodes.item(0),
							XPathConstants.NODESET);

					classes = new ArrayList<String>(classesList.getLength());
					for (int i = 0; i < classesList.getLength(); i++) {
						Node n = classesList.item(i);
						String clazz = (String) xPath.evaluate("Label", n,
								XPathConstants.STRING);
						classes.add(clazz);
					}
				}
				if (label.isEmpty()) {
					success = false;
					label = query;
				}

			}
		} catch (Exception e) {
			logger.error("Error during the mapping of the query " + query
					+ " on dbPedia (" + e.toString() + ")");

			if (e.toString().contains("sorry")) {
				try {
					Thread.sleep(60000 * 5);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			success = false;

		} catch (Error e) {
			logger.error("Error during the mapping of the query " + query
					+ " on dbPedia (" + e.toString() + ")");
			if (e.toString().contains("sorry")) {
				try {
					Thread.sleep(60000 * 5);
				} catch (InterruptedException e1) {
					throw new EntityException("Error during the mapping of the query " + query
					+ " on dbPedia (" + e.toString() + ")");
				}
			}
			success = false;

		}

		if (!success) {
			throw new NoResultException("mapping to dbpedia failed for query "
					+ query);
		}
		return success;
	}

	protected String produceQueryUrl(String query) throws EntityException {
		String dbpediaUri = SuggestionProperties.getInstance().getProperty(
				"dbpedia.query.api");
		String encodedQuery = "";
		try {
			encodedQuery = URIUtil.encodeQuery(query);
		} catch (URIException e) {
			logger.error("Error producing the enconded query for " + query);
			throw new EntityException("Error loading the entity from dbpedia");
		}
		String queryString = dbpediaUri + MAX_RESULT_PARAM + "=1" + "&"
				+ QUERY_STRING_PARAM + "=" + encodedQuery;
		return queryString;
	}

	/**
	 * @param classes
	 *            the classes to set
	 */
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(label).append("\n");
		sb.append(description).append("\n");
		sb.append("{ ");
		for (String clazz : classes) {
			sb.append(clazz).append(" ");
		}
		sb.append("}");
		return sb.toString();
	}

}
