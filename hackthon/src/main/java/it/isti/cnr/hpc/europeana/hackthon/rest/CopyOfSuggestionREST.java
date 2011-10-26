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

import java.util.List;

import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;



/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
public class CopyOfSuggestionREST extends ServerResource {

	public static void main(String[] args) throws Exception {
		// Create the HTTP server and listen on port 8182
		new Server(Protocol.HTTP, 8182, CopyOfSuggestionREST.class).start();
	}


	
	@Get("json")
	public String suggest(){
		String query = getQuery().getValues("query");
		StringBuilder sb = new StringBuilder("[");
		List<EuropeanaEntity> sugg = Suggestion.getInstance().getSuggestedEntities(query);
		for (EuropeanaEntity ee : sugg){
			sb.append(ee.toJson()).append(",");
		}
		sb.deleteCharAt(sb.length()-1).append("]");
		return sb.toString();
		
	}

}
