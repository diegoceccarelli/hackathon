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

import rest.SuggestionREST;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import util.EntityException;
import util.KeyGenerator;
import util.LRUCache;
import util.NoResultException;
import util.SuggestionProperties;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
public class EuropeanaEntityMapper {
	/**
	 * Logger for this class
	 */

//	private static KeyGenerator kg = KeyGenerator.getInstance();
	private static final Logger logger = LoggerFactory
			.getLogger(EuropeanaEntityMapper.class);
	
	
	
	private static LRUCache<Integer, EuropeanaEntity> cache;
	

	public static EuropeanaEntity getInstanceFromQuery(String query) {
		KeyGenerator kg = new KeyGenerator();

		if (cache == null) loadCacheFromFile();
		Integer key = kg.getKey(query);
		if (cache.containsKey(key)) return cache.get(key);
		
		Freebase fb = null;
		Dbpedia db = null;

		try {
			fb = FreebaseMapper.getInstanceFromQuery(query);
		} catch (Exception e) {
			logger.error("could not map query {} in freebase", query);
		}

		try {
			db = DbpediaMapper.getInstanceFromQuery(query);
		} catch (Exception e) {
			logger.error("could not map query {} in dbpedia", query);
		}

		EuropeanaEntity ee = new EuropeanaEntity();
		List<String> types = new ArrayList<String>();

		if (fb != null) {
			ee.setAlias(fb.getAlias());
			ee.setFreebaseGuid(fb.getGuid());
			ee.setFreebaseId(fb.getId());
			ee.setName(fb.getName());
			for (Type t : fb.getTypes()) {
				if (t.getLabel().contains("artist")) {
					types.add("Artist");
				}
			}
		}
		if (db != null) {
			ee.setDbpediaLabel(db.getLabel());
			ee.setDbpediaUri(db.getUri());
			ee.setDbpediaDescription(db.getDescription());
			for (Type t : db.getTypes()) {
				if (t.getLabel().contains("artist")) {
					types.add("Artist");
				}
			}
		}
		ee.setTypes(types);
		cache.put(key, ee);
		dumpResult(query, ee);
		return ee;

	}

	private static void dumpResult(String query, EuropeanaEntity ee)
			 {
		KeyGenerator kg = new KeyGenerator();

		Integer key = kg.getKey(query);
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(
					SuggestionProperties.getInstance().getProperty(
							"entity.mapper.cache"), true));
		}
		catch(IOException e){
			try {
				bw = new BufferedWriter(new FileWriter(
						SuggestionProperties.getInstance().getProperty(
								"entity.mapper.cache")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			
			
		
		bw.write(key.toString());
		bw.write("\t");
		bw.write(ee.toJson());
		bw.write("\n");
		bw.flush(); 
		bw.close(); 
		} catch (IOException e) {
			logger.error("error saving in the europeana entity cache ");
			e.printStackTrace();
		}
	}
	
	private static void loadCacheFromFile(){
		logger.info("loading europeana entities in cache");
		cache = new LRUCache<Integer, EuropeanaEntity>(10000);
		BufferedReader br=null;
		try {
			br = new BufferedReader(new FileReader(SuggestionProperties.getInstance().getProperty(
							"entity.mapper.cache")));
		} catch (FileNotFoundException e) {
			logger.error("error loading the europeana entity cache");
			return;
		}
		String line = null;
		try {
			while ((line = br.readLine())!= null){
				Scanner s = new Scanner(line).useDelimiter("\t");
				Integer key = s.nextInt();
				String json = s.next();
				EuropeanaEntity ee = EuropeanaEntity.fromJson(json);
				System.out.print(ee.getName());
				System.out.print(" ");
				cache.put(key, ee);
			}
		} catch (IOException e) {
			logger.error("error loading the europeana entity cache");
			return;
		}
		System.out.println();
		logger.info("done");
	}

	
}
