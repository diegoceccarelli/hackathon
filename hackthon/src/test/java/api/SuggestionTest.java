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
package api;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
public class SuggestionTest {

	@Test
	public void test() {
		Suggestion s = Suggestion.getInstance();
		for (String sugg : s.getSuggestion("leonardo da vinci"))
				System.out.println(sugg);
	}

}