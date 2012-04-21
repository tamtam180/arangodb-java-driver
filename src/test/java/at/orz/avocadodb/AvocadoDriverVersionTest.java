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

package at.orz.avocadodb;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import at.orz.avocadodb.entity.Version;

/**
 * UnitTest for REST API "version".
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AvocadoDriverVersionTest extends BaseTest {

	@Test
	public void test_version() throws AvocadoException {
		
		Version version = client.getVersion();
		
		assertThat(version, is(notNullValue()));
		assertThat(version.getServer(), is("avocado"));
		assertThat(version.getVersion(), is("0.3.12")); // FIXME 固定はよくないね
		
	}
	
}
