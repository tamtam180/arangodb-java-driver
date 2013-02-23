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

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.CollectionEntity;
import at.orz.arangodb.entity.DocumentEntity;
/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverDocumentTest extends BaseTest {

	private static Logger logger = LoggerFactory.getLogger(ArangoDriverCollectionTest.class);
	
	final String collectionName = "unit_test_arango_001"; // 通常ケースで使うコレクション名
	final String collectionName2 = "unit_test_arango_002";
	final String collectionName404 = "unit_test_arango_404"; // 存在しないコレクション名
	
	CollectionEntity col1;
	CollectionEntity col2;
	
	@Before
	public void before() throws ArangoException {
		
		logger.debug("----------");
		
		// 事前に消しておく
		for (String col: new String[]{collectionName, collectionName2, collectionName404}) {
			try {
				driver.deleteCollection(col);
			} catch (ArangoException e) {}
		}

		// 1と2は作る
		col1 = driver.createCollection(collectionName, false, null, null, null);
		col2 = driver.createCollection(collectionName2, true, null, null, null);
		
		logger.debug("--");
		
	}
	
	@After
	public void after() {
		logger.debug("----------");
	}

	@Test
	public void test_create_normal() throws ArangoException {
		
		// 適当にドキュメントを作る
		TestComplexEntity01 value = new TestComplexEntity01("user-" + 9999, "説明:" + 9999, 9999);
		DocumentEntity<TestComplexEntity01> doc = driver.createDocument(collectionName, value, null, false);
		
		assertThat(doc.getDocumentKey(), is(notNullValue()));
		assertThat(doc.getDocumentHandle(), is(collectionName + "/" + doc.getDocumentKey()));
		assertThat(doc.getDocumentRevision(), is(not(0L)));
		
	}
	
	@Test
	public void test_create_normal100() throws ArangoException {
		
		// 適当にドキュメントを作る
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01("user-" + i, "説明:" + i, i);
			driver.createDocument(collectionName, value, null, false);
		}
		
		// 100個格納できていることを確認する
		assertThat(driver.getCollectionCount(collectionName).getCount(), is(100L));
		
	}
	
	@Test
	public void test_create_sameobject() throws ArangoException {
		// 適当にドキュメントを作る
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01("user", "説明:", 10);
			driver.createDocument(collectionName, value, null, true);
		}
		
		// 100個格納できていることを確認する
		assertThat(driver.getCollectionCount(collectionName).getCount(), is(100L));
	}
	
	/**
	 * 存在しないコレクションに追加しようとするテスト
	 * @throws ArangoException
	 */
	@Test
	public void test_create_404() throws ArangoException {
		
		TestComplexEntity01 value = new TestComplexEntity01("test-user", "テスト☆ユーザー", 22);
		try {
			driver.createDocument(collectionName404, value, false, true);
			fail("例外が発生しないといけないの");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}
	
	/**
	 * コレクションがない場合、コレクションを勝手に作ってくれることを確認。
	 * @throws ArangoException
	 */
	@Test
	public void test_create_404_insert() throws ArangoException {

		TestComplexEntity01 value = new TestComplexEntity01("test-user", "テスト☆ユーザー", 22);
		// 存在しないコレクションに追加しようとする
		DocumentEntity<TestComplexEntity01> res = driver.createDocument(collectionName404, value, true, true);
		assertThat(res, is(notNullValue()));
		
		// コレクションができている
		CollectionEntity col3 = driver.getCollection(collectionName404);
		assertThat(col3, is(notNullValue()));
		
		assertThat(res.getDocumentHandle().startsWith(collectionName404 + "/"), is(true));
		assertThat(res.getDocumentRevision(), is(not(0L)));
		assertThat(res.getDocumentKey(), is(notNullValue()));
		
	}
	
	@Test
	public void test_update_404() throws ArangoException {
		
		TestComplexEntity01 value = new TestComplexEntity01("test-user", "テスト☆ユーザー", 22);
		// 存在しないコレクションに追加しようとする
		try {
			driver.updateDocument(collectionName404, 1, value, -1, null, null);
			fail("例外が発生しないといけないの");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}

	@Test
	public void test_update_404_2() throws ArangoException {
		
		TestComplexEntity01 value = new TestComplexEntity01("test-user", "テスト☆ユーザー", 22);
		// 存在するコレクションだが、ドキュメントが存在しない
		try {
			driver.updateDocument(collectionName, 1, value, -1, null, null);
			fail("例外が発生しないといけないの");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1202));
		}
		
	}

	// TODO Delete
//	@Test
//	public void test_checkDocument() throws ArangoException {
//
//		DocumentEntity<String> doc = client.createDocument(collectionName, "xx", null, false, Mode.RETURN_NULL);
//
//		client.checkDocument(doc.getDocumentHandle());
//		
//	}
	
}
