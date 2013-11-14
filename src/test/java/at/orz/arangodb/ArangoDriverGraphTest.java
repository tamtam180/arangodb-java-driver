/*
 * Copyright (C) 2012,2013 tamtam180
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.orz.arangodb;

import org.junit.Test;

import at.orz.arangodb.entity.GraphEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverGraphTest extends BaseTest {

	public ArangoDriverGraphTest(ArangoConfigure configure, ArangoDriver driver) {
		super(configure, driver);
	}

	@Test
	public void do_nothing() {
		
	}
	
//	@Test
//	public void test_create_graph() throws ArangoException {
//		
//		GraphEntity entity = driver.createGraph("graph3", "vcol1", "ecol1", false);
//		
//	}
	
	// TODO: errorNum: 1902 : "found graph but has different <name>"
	
}
