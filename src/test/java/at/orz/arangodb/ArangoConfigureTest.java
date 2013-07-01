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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * UnitTest for ArangoConfigure.
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoConfigureTest {
	
	@Test
	public void load_from_property_file() {
		
		// validate file in classpath.
		assertThat(getClass().getResource("/arangodb.properties"), is(notNullValue()));
		
		ArangoConfigure configure = new ArangoConfigure();
		assertThat(configure.getPort(), is(9999));
		assertThat(configure.getHost(), is("arango-test-server"));

	}
	
}
