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
package rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import domain.EuropeanaEntity;

import api.Suggestion;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
public class SuggestionHTTP extends ServerResource {

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
	    server.createContext("/suggest", new MyHandler());
	    server.setExecutor(null); // creates a default executor
	    server.start();
	  }

	  static class MyHandler implements HttpHandler {
	    public void handle(HttpExchange t) throws IOException {
	     
	      String uri = t.getRequestURI().toString();
	      int pos = uri.indexOf("=");
	      String query = uri.substring(pos+1);
	      StringBuilder sb = new StringBuilder();
			String line = "";
			InputStream is = this.getClass().getResourceAsStream("/start");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ( (line = br.readLine()) != null){
				sb.append(line).append("\n");
			}
			sb.append("result=[");
			query = query.replaceAll("%20", "+");
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
			String response = sb.toString();
	      t.sendResponseHeaders(200, response.length());
	      OutputStream os = t.getResponseBody();
	      os.write(response.getBytes());
	      os.close();
	    }
	  }
}