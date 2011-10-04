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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.EntityException;
import util.NoResultException;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 */
public class EuropeanaEntityMapper {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(EuropeanaEntityMapper.class);

	public static EuropeanaEntity getInstanceFromQuery(String query) {
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
			for(Type t: fb.getTypes()){
				if (t.getLabel().contains("artist")){
					types.add("Artist");
				}
			}
		}
		if (db != null) {
			ee.setDbpediaLabel(db.getLabel());
			ee.setDbpediaUri(db.getUri());
			ee.setDbpediaDescription(db.getDescription());
			for(Type t: db.getTypes()){
				if (t.getLabel().contains("artist")){
					types.add("Artist");
				}
			}
		}
		ee.setTypes(types);
		return ee;
		

	}

}
