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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.lucene.document.Document;

import util.SuggestionProperties;

import domain.Type;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
public class Freebase implements Entity {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(Freebase.class);

	private List<String> alias;
	private String name;
	private String guid;

	/**
	 * @param alias
	 * @param name
	 * @param guid
	 * @param id
	 * @param types
	 */
	public Freebase(List<String> alias, String name, String guid, String id,
			List<Type> types) {
		super();
		this.alias = alias;
		this.name = name;
		this.guid = guid;
		this.id = id;
		this.types = types;
	}

	private String id;
	private List<Type> types;

	/**
	 * @return the alias
	 */
	public List<String> getAlias() {
		return alias;
	}

	/**
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(List<String> alias) {
		this.alias = alias;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid
	 *            the guid to set
	 */
	public void setGuid(String guid) {
		
		this.guid = guid;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the types
	 */
	public List<Type> getTypes() {
		return types;
	}

	/**
	 * @param types
	 *            the types to set
	 */
	public void setTypes(List<Type> types) {
		this.types = types;
	}

	
}
