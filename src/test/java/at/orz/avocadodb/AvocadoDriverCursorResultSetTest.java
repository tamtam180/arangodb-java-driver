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

import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import at.orz.avocadodb.entity.CursorEntity;
import at.orz.avocadodb.entity.DefaultEntity;
import at.orz.avocadodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AvocadoDriverCursorResultSetTest extends BaseTest {

	@Before
	public void setup() throws AvocadoException {

		// Collectionを作る
		String collectionName = "unit_test_query_test";
		client.createCollection(collectionName, null, null);
		client.truncateCollection(collectionName, null);
		
		// テストデータを作る
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01(
					"user_" + (i % 10), 
					"desc" + (i % 10), 
					i);
			client.createDocument(collectionName, value, null, null, null);
		}

	}
	
	@Test
	public void test1() throws AvocadoException {
		
		String query = "SELECT t FROM unit_test_query_test t WHERE t.age >= @age@ order by t.age";
		Map<String, Object> bindVars = new MapBuilder().put("age", 90).get();
		
		// 全件とれる範囲
		CursorResultSet<TestComplexEntity01> rs = client.executeQueryWithResultSet(
				query, bindVars, TestComplexEntity01.class, true, 20);
		
		int count = 0;
		for (TestComplexEntity01 obj: rs) {
			assertThat(obj.getAge(), is(90+count));
			count++;
		}
		assertThat(count, is(10));
		
	}

	@Test
	public void test2() throws AvocadoException {
		
		String query = "SELECT t FROM unit_test_query_test t WHERE t.age >= @age@ order by t.age";
		Map<String, Object> bindVars = new MapBuilder().put("age", 90).get();
		
		CursorResultSet<TestComplexEntity01> rs = client.executeQueryWithResultSet(
				query, bindVars, TestComplexEntity01.class, true, 10);
		
		int count = 0;
		for (TestComplexEntity01 obj: rs) {
			assertThat(obj.getAge(), is(90+count));
			count++;
		}
		assertThat(count, is(10));
		
	}

	@Test
	public void test3() throws AvocadoException {
		
		String query = "SELECT t FROM unit_test_query_test t WHERE t.age >= @age@ order by t.age";
		Map<String, Object> bindVars = new MapBuilder().put("age", 90).get();
		
		CursorResultSet<TestComplexEntity01> rs = client.executeQueryWithResultSet(
				query, bindVars, TestComplexEntity01.class, true, 5);
		
		int count = 0;
		for (TestComplexEntity01 obj: rs) {
			assertThat(obj.getAge(), is(90+count));
			count++;
		}
		assertThat(count, is(10));
		
	}

	@Test
	public void test4() throws AvocadoException {
		
		String query = "SELECT t FROM unit_test_query_test t WHERE t.age >= @age@ order by t.age";
		Map<String, Object> bindVars = new MapBuilder().put("age", 90).get();
		
		CursorResultSet<TestComplexEntity01> rs = client.executeQueryWithResultSet(
				query, bindVars, TestComplexEntity01.class, true, 3);
		
		int count = 0;
		for (TestComplexEntity01 obj: rs) {
			assertThat(obj.getAge(), is(90+count));
			count++;
		}
		assertThat(count, is(10));
		
	}
	
	@Test
	public void test5() throws AvocadoException {
		
		String query = "SELECT t FROM unit_test_query_test t WHERE t.age >= @age@ order by t.age";
		Map<String, Object> bindVars = new MapBuilder().put("age", 90).get();
		
		CursorResultSet<TestComplexEntity01> rs = client.executeQueryWithResultSet(
				query, bindVars, TestComplexEntity01.class, true, 1);
		
		int count = 0;
		for (TestComplexEntity01 obj: rs) {
			assertThat(obj.getAge(), is(90+count));
			count++;
		}
		assertThat(count, is(10));
		
	}


}
