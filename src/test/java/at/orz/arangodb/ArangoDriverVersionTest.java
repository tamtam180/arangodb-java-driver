/*
 * Copyright (C) 2012 tamtam180
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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.V8Version;

/**
 * UnitTest for REST API "version".
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverVersionTest extends BaseTest {

	@Test
	public void test_version() throws ArangoException {
		
		V8Version version = client.getVersion();
		
		assertThat(version, is(notNullValue()));
		assertThat(version.getVersion(), is("V8")); // FIXME 固定はよくないね
		
	}
	
}
