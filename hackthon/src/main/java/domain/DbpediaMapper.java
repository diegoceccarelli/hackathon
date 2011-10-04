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

import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.slf4j.Logger;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
public class DbpediaMapper {
	/**
 * Logger for this class
 */
private static final Logger logger = LoggerFactory.getLogger(DbpediaMapper.class);

		private static final float HASH_LOAD_FACTOR = 0.75f;
		private static final int MAX_CACHE_SIZE = 100;
		private static final int HASH_CAPACITY = (int) Math.ceil(MAX_CACHE_SIZE
				/ HASH_LOAD_FACTOR) + 1;
		private static Map<String, Entity> entityCache = new LinkedHashMap<String, Entity>(
				HASH_CAPACITY, .75F, true) {
			protected boolean removeEldestEntry(Map.Entry<String, Entity> eldest) {
				return size() > MAX_CACHE_SIZE;
			}
		};


		private final static String MAX_RESULT_PARAM = "MaxHits";
		private final static String QUERY_STRING_PARAM = "QueryString";

		private final static double SIMILARITY_THRESHOLD = 0.045d;

		private String label;
		private String description;
		private String context;
		private Set<String> terms;
		private List<String> classes;

		private static Set<String> termsToFilter;

		public DbpediaMapper() {
			
			termsToFilter = new HashSet(StopAnalyzer.ENGLISH_STOP_WORDS_SET);

			String[] toFilter = new String[] { "en.wikipedia.org", "encyclopedia",
					"free", "from", "makes", "more", "official", "site", "welcome",
					"wiki", "wikipedia", "world" };
			termsToFilter.addAll(Arrays.asList(toFilter));

			try {
				InputStream is = getClass().getResourceAsStream(
						"/session-cleaner.properties");
				properties.load(new InputStreamReader(is));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger
						.debug("Cannot retrieve the session-clearner.properties file ");
				return;
			}
			mapper = new GoogleQuery2Entity();
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

		private static synchronized Entity getFromCache(String query) {
			if (entityCache.containsKey(query)) {
				logger.debug("Hit entity for query " + query);
				return entityCache.get(query);
			}
			return null;
		}

		private static synchronized void pushInCache(String query, Entity e) {
			entityCache.put(query, e);
			return;
		}

		public boolean load(String _query) throws EntityException {
			// FIXME refactoring
			// try to clean the query and correct spelling errors
			// using yahoo
			_query = _query.trim();
			Entity entity = getFromCache(_query);
			boolean success = true;
			if (entity != null) {
				entity.clone(this);
				return true;
			}
			try{
			Thread.sleep(400);
			}catch(Exception e){
			
			}
			String query = mapper.resolve(_query);

			context = mapper.getContext();
			logger.debug("context = " + context);
			logger.debug("query resolved per " + _query + " = " + query);
			String queryUrl = produceQueryUrl(query);

			label = "";
			description = "";

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

						description = ((String) xPath.evaluate("Description", nodes
								.item(0), XPathConstants.STRING)).toLowerCase();

						NodeList classesList = (NodeList) xPath.evaluate(
								"Classes/Class", nodes.item(0),
								XPathConstants.NODESET);

						classes = new ArrayList<String>(classesList.getLength());
						for (int i = 0; i < classesList.getLength(); i++) {
							Node n = classesList.item(i);
							String clazz = (String) xPath.evaluate("Label", n,
									XPathConstants.STRING);
							classes.add(clazz);
							logger.debug("read class " + clazz);
						}
					}
					if (label.isEmpty()) {
						success = false;
						label = _query;
					}
					terms = new HashSet<String>();
					StringBuilder sb = new StringBuilder(label).append(" ");
					sb.append(description).append(" ");
					sb.append(context).append(" ");

					// add all terms and classes to the set
					tokenizer = new StandardTokenizer(Version.LUCENE_30,
							new StringReader(sb.toString()));
					tokenizer = new StopFilter(true, tokenizer, termsToFilter);
					tokenizer = new LowerCaseFilter(tokenizer);
					tokenizer = new LengthFilter(tokenizer, 4, 100);

					TermAttribute ta = tokenizer.getAttribute(TermAttribute.class);

					while (tokenizer.incrementToken()) {
						terms.add(ta.term());
					}
					// not discriminant
					if (classes != null) {
						classes.remove("owl#Thing");
						terms.addAll(classes);
					}

					pushInCache(_query, this);

				}
			} catch (Exception e) {
				logger.error("Error during the mapping of the query " + query
						+ " on dbPedia (" + e.toString() + ")");
				
				if (e.toString().contains("sorry")) {
					try {
						Thread.sleep(60000*5);
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
						Thread.sleep(60000*5);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				success = false;

			}

			if (!success) {
				logger.warn("mapping to dbpedia failed for query " + _query);
			}
			return success;
		}

		protected String produceQueryUrl(String query) {
			String dbpediaUri = properties.getProperty("dbpedia.keyworksearch.uri");
			String encodedQuery = "";
			try {
				encodedQuery = URIUtil.encodeQuery(query);
			} catch (URIException e) {
				logger.error("Error producing the enconded query for " + query);
				return null;
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

		public Set<String> getTermSet() {
			return terms;
		}

		public boolean containsTerms(String query) {
			if (query == null) return false;
			for (String term : query.split(" ")) {
				if (terms.contains(term))
					return true;
			}
			return false;
		}

		public double getSimilarity(Entity e) {
			Set<String> thisTermSet = this.getTermSet();
			Set<String> eTermSet = e.getTermSet();
			// compute simple jaccard, could be improved in future
			Set<String> union = new TreeSet<String>(thisTermSet);
			union.addAll(eTermSet);

			Set<String> intersection = new TreeSet<String>(thisTermSet);
			intersection.retainAll(eTermSet);
			logger.debug("intersection (" + e.getLabel() + "," + this.getLabel()
					+ ")" + " = " + intersection.toString());
			return intersection.size() / (double) union.size();
		}

		public boolean isSimilar(Entity e) {
			return getSimilarity(e) >= SIMILARITY_THRESHOLD;
		}

		public Entity clone() {
			Entity e = new Entity();
			e.label = label;
			e.description = description;
			e.context = context;
			e.terms = new HashSet<String>(terms);
			e.classes = new ArrayList<String>(classes);
			return e;
		}

		public void clone(Entity e) {
			e.label = label;
			e.description = description;
			e.context = context;
			e.terms = new HashSet<String>(terms);
			if (classes == null)
				e.classes = new ArrayList<String>();
			else
				e.classes = new ArrayList<String>(classes);
			return;
		}

	}

}
