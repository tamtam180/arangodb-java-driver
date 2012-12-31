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

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.CursorEntity;
import at.orz.arangodb.entity.DefaultEntity;
import at.orz.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverCursorTest extends BaseTest {
	
	@Test
	public void test_validateQuery() throws ArangoException {
		
		CursorEntity<?> entity = driver.validateQuery(
				//"SELECT t FROM unit_test_cursor t WHERE t.name == @name@ && t.age >= @age@"
				"FOR t IN unit_test_cursor FILTER t.name == @name && t.age >= @age RETURN t"
				);
		
		assertThat(entity.getCode(), is(200));
		assertThat(entity.getBindVars().size(), is(2));
		assertThat(entity.getBindVars().get(0), is("name"));
		assertThat(entity.getBindVars().get(1), is("age"));
		
	}

	@Test
	public void test_validateQuery_400_1() throws ArangoException {
		
		// =じゃなくて==じゃないとダメ。文法間違いエラー
		CursorEntity<?> entity = driver.validateQuery(
				//"SELECT t FROM unit_test_cursor t WHERE t.name = @name@"
				"FOR t IN unit_test_cursor FILTER t.name = @name@"
				);
		
		assertThat(entity.getCode(), is(400));
		assertThat(entity.getErrorNumber(), is(1501));
		
	}

	@Test
	@Ignore
	public void test_validateQuery_400_2() throws ArangoException {
	}

	@Test
	public void test_executeQuery() throws ArangoException {
		
		// Collectionを作る
		String collectionName = "unit_test_query_test";
		try {
			driver.createCollection(collectionName);
		} catch (ArangoException e) {}
		driver.truncateCollection(collectionName);
		
		// テストデータを作る
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01(
					"user_" + (i % 10), 
					"desc" + (i % 10), 
					i);
			driver.createDocument(collectionName, value, null, null);
		}
		
		//String query = "SELECT t FROM unit_test_query_test t WHERE t.age >= @age@";
		String query = "FOR t IN unit_test_query_test FILTER t.age >= @age RETURN t";
		Map<String, Object> bindVars = new MapBuilder().put("age", 90).get();
		
		// 全件とれる範囲
		{
			CursorEntity<TestComplexEntity01> result = driver.<TestComplexEntity01>executeQuery(
					query, bindVars, TestComplexEntity01.class, true, 20);
			assertThat(result.size(), is(10));
			assertThat(result.getCount(), is(10));
			assertThat(result.hasMore(), is(false));
		}
		
	}

	@Test
	public void test_executeQuery_2() throws ArangoException {
		
		// Collectionを作る
		String collectionName = "unit_test_query_test";
		try {
			driver.createCollection(collectionName);
		} catch (ArangoException e) {}
		driver.truncateCollection(collectionName);
		
		// テストデータを作る
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01(
					"user_" + (i % 10), 
					"desc" + (i % 10), 
					i);
			driver.createDocument(collectionName, value, null, null);
		}
		
		//String query = "SELECT t FROM unit_test_query_test t WHERE t.age >= @age@";
		String query = "FOR t IN unit_test_query_test FILTER t.age >= @age RETURN t";
		Map<String, Object> bindVars = new MapBuilder().put("age", 90).get();
		
		// ちまちまとる範囲
		long cursorId;
		{
			CursorEntity<TestComplexEntity01> result = driver.executeQuery(
					query, bindVars, TestComplexEntity01.class, true, 3);
			assertThat(result.size(), is(3));
			assertThat(result.getCount(), is(10));
			assertThat(result.hasMore(), is(true));
			assertThat(result.getCursorId(), is(not(-1L)));
			assertThat(result.getCursorId(), is(not(0L)));
			
			cursorId = result.getCursorId();
		}
		
		// 次のRoundTrip
		{
			CursorEntity<TestComplexEntity01> result = driver.continueQuery(
					cursorId, TestComplexEntity01.class);
			assertThat(result.size(), is(3));
			assertThat(result.getCount(), is(10));
			assertThat(result.hasMore(), is(true));
		}

		// 次のRoundTrip
		{
			CursorEntity<TestComplexEntity01> result = driver.continueQuery(
					cursorId, TestComplexEntity01.class);
			assertThat(result.size(), is(3));
			assertThat(result.getCount(), is(10));
			assertThat(result.hasMore(), is(true));
		}

		// 次のRoundTrip
		{
			CursorEntity<TestComplexEntity01> result = driver.continueQuery(
					cursorId, TestComplexEntity01.class);
			assertThat(result.size(), is(1));
			assertThat(result.getCount(), is(10));
			assertThat(result.hasMore(), is(false));
		}
		
		// 削除
		{
			DefaultEntity result = driver.finishQuery(cursorId);
		}
		
	}

	
}
