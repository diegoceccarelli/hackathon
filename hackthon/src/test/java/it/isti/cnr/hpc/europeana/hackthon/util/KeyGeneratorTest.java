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
package it.isti.cnr.hpc.europeana.hackthon.util;

import static org.junit.Assert.*;
import it.isti.cnr.hpc.europeana.hackthon.util.KeyGenerator;
import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
public class KeyGeneratorTest {

	@Test
	public void test() {
		KeyGenerator kg = KeyGenerator.getInstance();
		Assert.assertEquals(kg.getKey("leonardo da Vinci"),kg.getKey("leonarDO Vinci da"));
		Assert.assertEquals(kg.getKey("vinci leonardo"),kg.getKey("leonardo vinci"));
	}

}
