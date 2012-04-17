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

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.orz.avocadodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AvocadoDriverKeyValueTest extends BaseTest {

	private static Logger logger = LoggerFactory.getLogger(AvocadoDriverKeyValueTest.class);
	
	final String collectionName = "unit_test_avocado_001"; // 通常ケースで使うコレクション名
	final String collectionName404 = "unit_test_avocado_404"; // 存在しないコレクション名
	
	@Before
	public void before() throws AvocadoException {
		
		logger.debug("----------");
		
		// 事前に消しておく
		client.deleteCollection(collectionName, null);
		client.deleteCollection(collectionName404, null);

		logger.debug("--");
		
	}
	
	@After
	public void after() {
		logger.debug("----------");
	}
	
	@Test
	public void test_createValue_no_collection() throws AvocadoException {
		
		// TODO キーに"/"がある場合
		
		String key = "aaa";
		Object value = "12345";
		client.createKeyValue(
				collectionName404, 
				key, value, new MapBuilder("opt1", "aa").get(), new Date(System.currentTimeMillis() + 1000L * 60), null);
		
	}
	
	@Test
	public void test_create_dup() {
		
	}
	
}
