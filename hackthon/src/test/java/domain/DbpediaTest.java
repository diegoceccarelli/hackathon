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

import org.junit.Test;

import util.EntityException;
import util.NoResultException;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
public class DbpediaTest {

	@Test
	public void test() throws EntityException, NoResultException {
		Dbpedia db = DbpediaMapper.getInstanceFromQuery("da vinci");
		System.out.println(db);
		 db = DbpediaMapper.getInstanceFromQuery("michelangelo");
		 System.out.println(db);
	}

}
