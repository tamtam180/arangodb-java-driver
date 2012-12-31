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

import org.junit.AfterClass;
import org.junit.BeforeClass;

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoDriver;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class BaseTest {

	protected static ArangoConfigure configure;
	protected static ArangoDriver driver;
	
	@BeforeClass
	public static void _setup() {
		configure = new ArangoConfigure();
		configure.init();
		driver = new ArangoDriver(configure);
	}
	
	@AfterClass
	public static void _shutdown() {
		configure.shutdown();
	}

	
}
