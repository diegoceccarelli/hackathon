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

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
/*
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
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import util.GoogleException;


/**
 * @author Franco Maria Nardini <francomaria.nardini@isti.cnr.it>
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 21/mag/2011
 */
public class GoogleQuery2Entity  {
	public static final Logger logger = LoggerFactory
			.getLogger(GoogleQuery2Entity.class);

	public String yahooURI;
	public String yahooAppId;
	Properties properties;

	private String context;
	private String query;
	private static GoogleQuery2Entity instance;

	public GoogleQuery2Entity() {
		super();
	}
	
	public static GoogleQuery2Entity getInstance(){
		if (instance == null) instance = new GoogleQuery2Entity();
		return instance;
	}

	public String produceQueryUrl(String query) {
		String queryUrl = "http://www.google.com/search?hl=en&source=hp&biw=1063&bih=569&q="
				+ query + "&aq=f&aqi=g10&aql=&oq=";
		// String queryUrl = "http://www.di.unipi.it/~ceccarel";
		String encodedQueryUrl;
		try {
			encodedQueryUrl = URIUtil.encodeQuery(queryUrl);
		} catch (URIException e) {
			logger.error("Error producing the enconded query for " + queryUrl);
			return null;
		}
		return encodedQueryUrl;
	}

	public String resolve(String _query) throws GoogleException {
		query = _query;
		String queryUrl = produceQueryUrl("site:wikipedia.org " + query);

		try {
			List<Result> results = getResults(queryUrl);
			if (results.size() > 0) {
				String title = results.get(0).title;
				//query = cleanWikipediaTitle(title);
			}
			results = getResults(queryUrl.replace("site:wikipedia.org", ""));

			StringBuilder sb = new StringBuilder();
			for (Result r : results) {
				sb.append(r.title + " " + r.summary);
			}

			//setContext(sb.toString());

		} catch (Exception e) {
			logger.error("Error mapping the query (" + _query
					+ ") to a wikipedia title (" + e.toString() + ")");
			throw new GoogleException("Error mapping the query (" + _query
					+ ") to a wikipedia title (" + e.toString() + ")");
		}

		return query;
	}

	public List<Result> getResults(String query) throws InterruptedException {
		URL mapWikipedia;
		try {
			mapWikipedia = new URL(query);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			return null;
		}
		URLConnection urlc;
		try {
			urlc = mapWikipedia.openConnection();
		} catch (IOException e) {
			logger.error(e.toString());
			return null;
		}
		urlc
				.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11");

		InputStream is;
		try {
			is = urlc.getInputStream();
		} catch (IOException e) {
			logger.error(e.toString());
			if (e.toString().contains("sorry")) {
				logger.error("SLEEP!");
				try {
					Thread.sleep(60000*30);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return null;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String inputLine;
		boolean bodyFound = false;
		StringBuilder sb = new StringBuilder();

		try {
			while ((inputLine = in.readLine()) != null) {
				// Process each line.
				if (inputLine.contains("<body"))
					bodyFound = true;
				if (bodyFound)
					sb.append(inputLine).append(" ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			if (e.toString().contains("sorry")) {
				logger.error("SLEEP!");
				try {
					Thread.sleep(60000*30);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return null;
		}
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			
			return null;
		}

		return parseResults(sb.toString());
	}

	/**
	 * @param string
	 * @return
	 */
	private List<Result> parseResults(String string) {
		Scanner scanner = new Scanner(string).useDelimiter("<h3");
		List<Result> results = new ArrayList<Result>(10);
		while (scanner.hasNext()) {
			Result r = getResult(scanner.next());
			if (r != null)
				results.add(r);
		}
		return results;
	}

	private Result getResult(String string) {
		String patternUrl = "href=\"([^\"]+)";
		String patternTitle = "(.+)</h3>";
		String patternSummary = "(.+)Cached";
		Pattern pattern = Pattern.compile(patternUrl + patternTitle
				+ patternSummary);
		Matcher matcher = pattern.matcher(string);
		boolean matchFound = matcher.find();

		if (matchFound) {
			String url = matcher.group(1);
			String title = cleanHtml("<" + matcher.group(2));
			String summary = cleanHtml("<" + matcher.group(3));
			Result r = new Result(title, summary, url);
			return r;
		}
		return null;

	}

	private static String cleanHtml(String string) {
		string = string.replaceAll("[<][^>]+[>]", "");
//		try {
//			string = java.net.URLDecoder.decode(string, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			logger.debug("problems converting the string " + string
//					+ " in utf-8");
//		}
		return string;

	}

	public class Result {
		/**
		 * @param title
		 * @param summary
		 * @param url
		 */
		public Result(String title, String summary, String url) {
			this.title = title;
			this.summary = summary;
			this.url = url;
		}

		String title;
		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @param title the title to set
		 */
		public void setTitle(String title) {
			this.title = title;
		}

		/**
		 * @return the summary
		 */
		public String getSummary() {
			return summary;
		}

		/**
		 * @param summary the summary to set
		 */
		public void setSummary(String summary) {
			this.summary = summary;
		}

		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @param url the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		String summary;
		String url;

		public String toString() {
			return title + "\n" + summary + "\n" + url;
		}
	}

}
