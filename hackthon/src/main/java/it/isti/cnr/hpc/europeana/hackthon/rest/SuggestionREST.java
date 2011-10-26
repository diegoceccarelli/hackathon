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
package it.isti.cnr.hpc.europeana.hackthon.rest;

import it.isti.cnr.hpc.europeana.hackthon.api.Suggestion;
import it.isti.cnr.hpc.europeana.hackthon.domain.EuropeanaEntity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;



/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
public class SuggestionREST extends ServerResource {

	public static void main(String[] args) throws Exception {
		// Create the HTTP server and listen on port 8182
		new Server(Protocol.HTTP, 8182, SuggestionREST.class).start();
	}


	
	@Get()
	public String suggest() throws IOException{
		
		
		String query = getQuery().getValues("query");
		
		StringBuilder sb = new StringBuilder();
		String line = "";
		InputStream is = this.getClass().getResourceAsStream("/start");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while ( (line = br.readLine()) != null){
			sb.append(line).append("\n");
		}
		sb.append("\nresult=[")
		
;		List<EuropeanaEntity> sugg = Suggestion.getInstance().getSuggestedEntities(query);
		for (EuropeanaEntity ee : sugg){
			sb.append(ee.toJson()).append(",");
		}
		sb.deleteCharAt(sb.length()-1).append("]\n");
		
		 is = this.getClass().getResourceAsStream("/second");
		 br = new BufferedReader(new InputStreamReader(is));
		while ( (line = br.readLine()) != null){
			sb.append(line).append("\n");
		}
		
		
		return sb.toString();
		
	}

}
