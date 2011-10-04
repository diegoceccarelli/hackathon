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


/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
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

package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TwitterProperties.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 25/set/2011
 */
public class SuggestionProperties {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(SuggestionProperties.class);

	private static final String PROPERTIES_FILE = "suggestion.properties";

	Properties properties;
	private static SuggestionProperties suggestionProperties;

	private SuggestionProperties()  {
		properties = new Properties();
		try {
			properties.load(new FileInputStream(PROPERTIES_FILE));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find the property file "
					+ PROPERTIES_FILE + " in the current folder ");
			System.exit(-1);
		}catch (IOException e1) {
			System.err.println("Problems with the property file "
					+ PROPERTIES_FILE +  " ( "+ e1 +" ) ");
			System.exit(-1);
		}
		
	}

	public static SuggestionProperties getInstance() {
		if (suggestionProperties == null)
			suggestionProperties = new SuggestionProperties();
		return suggestionProperties;
	}

	public String getProperty(String key) {
		logger.info("loading property {} from {} {}", key,
				PROPERTIES_FILE);
		return properties.getProperty(key);
	}

	
}
