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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.plaf.ListUI;

import org.apache.lucene.analysis.LengthFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;



/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
public class KeyGenerator {
	TokenStream tokenizer;
	private static String[] toFilter = new String[] { "the", "la", "le" };
	Set<String> termsToFilter;
	
	private static KeyGenerator kg;
	
	public KeyGenerator() {
		termsToFilter = new HashSet(Arrays.asList(toFilter));
	}
	
	public static KeyGenerator getInstance(){
		if (kg == null) kg = new KeyGenerator();
		return kg;
		
	}
	
	public  Integer getKey(String text) {
		List<String> terms = new ArrayList<String>();
		tokenizer = new StandardTokenizer(Version.LUCENE_30,
				new StringReader(text));
		tokenizer = new StopFilter(true, tokenizer, termsToFilter);
		tokenizer = new LowerCaseFilter(tokenizer);
		tokenizer = new LengthFilter(tokenizer, 3, 100);

		TermAttribute ta = tokenizer.getAttribute(TermAttribute.class);

		try {
			while (tokenizer.incrementToken()) {
				terms.add(ta.term());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort(terms);
		StringBuilder sb = new StringBuilder();
		for (String t: terms) sb.append(t).append(" ");
		System.out.println(sb.toString());
		return sb.toString().hashCode();
		
		
		
	}
}
