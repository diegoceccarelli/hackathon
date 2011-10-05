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

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.EntityException;
import util.KeyGenerator;
import util.LRUCache;
import util.NoResultException;
import util.SuggestionProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
public class FreebaseMapper{
	/**
	 * Logger for this class
	 *
	 */
	private static final Logger logger = LoggerFactory.getLogger(FreebaseMapper.class);	
	private static LRUCache<Integer,Freebase> cache = new LRUCache<Integer,Freebase>(10000);
	//private static KeyGenerator kg = KeyGenerator.getInstance();
	
	public boolean isLegal(){
		return (result.size() > 1);
	}
	
	
	
	
	
	List<Result> result;
	
	public class Result{
		List<String> alias;
		String name;
		String guid; 
		String id; 
		List<Type> type;
		
		
		public class Type{
			
			
			/**
			 * @param id
			 * @param name
			 */
			public Type(String id, String name) {
				super();
				this.id = id;
				this.name = name;
			}
			/**
			 * @return the id
			 */
			public String getId() {
				return id;
			}
			/**
			 * @param id the id to set
			 */
			public void setId(String id) {
				this.id = id;
			}
			/**
			 * @return the name
			 */
			public String getName() {
				return name;
			}
			/**
			 * @param name the name to set
			 */
			public void setName(String name) {
				this.name = name;
			}
			String id;
			String name;
		}
	}
	
	public static Freebase parseFromJson(Reader r) throws EntityException, NoResultException{
		Gson gson = new Gson();
		FreebaseMapper obj = null;
		try {
			obj = gson.fromJson(r, FreebaseMapper.class);
		} catch (Exception e) {
			logger.error("error parsing the freebase json ({}) ", e.getMessage());
			throw new EntityException("Error parsing the freebase results");
		}
		
		if (obj.isLegal()){
			Result res = obj.result.get(0);
			List<domain.Type> types = new ArrayList<domain.Type>();
			for (FreebaseMapper.Result.Type t : res.type){
				types.add(new domain.Type(t.id, t.name));
			}
			Freebase f = new Freebase(res.alias,res.name,res.guid,res.id, types);
			return f;
		}

		throw new NoResultException();
	}
	
	public static Freebase getInstanceFromQuery(String query) throws IOException, EntityException, NoResultException {
		KeyGenerator kg = new KeyGenerator();
		Integer key = kg.getKey(query);
		if (cache.containsKey(key)) return cache.get(key);
		
		String uri = SuggestionProperties.getInstance().getProperty(
				"freebase.query.api");
		uri += query;
		
		try {
			uri = URIUtil.encodeQuery(uri);
		} catch (URIException e) {
			logger.error("Error producing the enconded query for {}", uri);
			return null;
		}
		try {
			URL u = new URL(uri);
			URLConnection uc = u.openConnection();

			InputStream is = uc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			Freebase fb =  parseFromJson(br);
			cache.put(key, fb);
			return fb;
			
		} catch (IOException e) {
			throw new IOException("error retrieving the query from freebase");
			
			
		}
		
	}
	
}
