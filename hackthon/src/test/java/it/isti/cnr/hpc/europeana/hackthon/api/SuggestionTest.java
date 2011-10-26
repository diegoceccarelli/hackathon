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
package it.isti.cnr.hpc.europeana.hackthon.api;

import static org.junit.Assert.*;

import it.isti.cnr.hpc.europeana.hackthon.api.Suggestion;
import it.isti.cnr.hpc.europeana.hackthon.api.Suggestion.SuggestedItem;

import java.util.List;
import java.util.Map;

import org.junit.Test;


/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
public class SuggestionTest {

	@Test
	public void test() {
		Suggestion s = Suggestion.getInstance();
		List<String> suggestions = s.getSuggestion("leonardo da vinci");
		for (String sugg : suggestions)
				System.out.println(sugg);
				
		List<SuggestedItem> res = s.filterSuggestions("leonardo da vinci", suggestions);
		for (SuggestedItem item : res){
			System.out.println(item.getLabel()+"\t"+item.getExplain());
		}
	}

}
