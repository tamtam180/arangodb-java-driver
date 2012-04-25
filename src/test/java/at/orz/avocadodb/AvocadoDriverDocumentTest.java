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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.orz.avocadodb.AvocadoDriver.Mode;
import at.orz.avocadodb.entity.CollectionEntity;
import at.orz.avocadodb.entity.DocumentEntity;
/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AvocadoDriverDocumentTest extends BaseTest {

	private static Logger logger = LoggerFactory.getLogger(AvocadoDriverCollectionTest.class);
	
	final String collectionName = "unit_test_avocado_001"; // 通常ケースで使うコレクション名
	final String collectionName2 = "unit_test_avocado_002";
	final String collectionName404 = "unit_test_avocado_404"; // 存在しないコレクション名
	
	CollectionEntity col1;
	CollectionEntity col2;
	
	@Before
	public void before() throws AvocadoException {
		
		logger.debug("----------");
		
		// 事前に消しておく
		client.deleteCollection(collectionName, null);
		client.deleteCollection(collectionName2, null);
		client.deleteCollection(collectionName404, null);

		// 1と2は作る
		col1 = client.createCollection(collectionName, false, Mode.RAISE_ERROR);
		col2 = client.createCollection(collectionName2, true, Mode.RAISE_ERROR);
		
		logger.debug("--");
		
	}
	
	@After
	public void after() {
		logger.debug("----------");
	}
	
	@Test
	public void test_create_normal() throws AvocadoException {
		
		// 適当にドキュメントを作る
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01("user-" + i, "説明:" + i, i);
			client.createDocument(collectionName, value, null, false, Mode.RETURN_NULL);
		}
		
		// 100個格納できていることを確認する
		assertThat(client.getCollectionCount(collectionName, null).getCount(), is(100L));
		
	}
	
	@Test
	public void test_create_sameobject() throws AvocadoException {
		// 適当にドキュメントを作る
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01("user", "説明:", 10);
			client.createDocument(collectionName, value, null, true, Mode.RETURN_NULL);
		}
		
		// 100個格納できていることを確認する
		assertThat(client.getCollectionCount(collectionName, null).getCount(), is(100L));
	}
	
	/**
	 * 存在しないコレクションに追加しようとするテスト
	 * @throws AvocadoException
	 */
	@Test
	public void test_create_404() throws AvocadoException {
		
		TestComplexEntity01 value = new TestComplexEntity01("test-user", "テスト☆ユーザー", 22);
		// 存在しないコレクションに追加しようとする
		DocumentEntity<?> res = client.createDocument(collectionName404, value, false, true, null);
		assertThat(res, is(nullValue()));
		
		try {
			client.createDocument(collectionName404, value, false, true, Mode.RAISE_ERROR);
			fail("例外が発生しないといけないの");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}
	
	/**
	 * コレクションがない場合、コレクションを勝手に作ってくれることを確認。
	 * @throws AvocadoException
	 */
	@Test
	public void test_create_404_insert() throws AvocadoException {

		TestComplexEntity01 value = new TestComplexEntity01("test-user", "テスト☆ユーザー", 22);
		// 存在しないコレクションに追加しようとする
		DocumentEntity<TestComplexEntity01> res = client.createDocument(collectionName404, value, true, true, null);
		assertThat(res, is(notNullValue()));
		
		// コレクションができている
		CollectionEntity col3 = client.getCollection(collectionName404, null);
		assertThat(col3, is(notNullValue()));
		
		assertThat(res.getDocumentHandle().startsWith(col3.getId() + "/"), is(true));
		assertThat(res.getDocumentRevision(), is(not(0L)));
		
	}
	
	@Test
	public void test_update_404() throws AvocadoException {
		
		TestComplexEntity01 value = new TestComplexEntity01("test-user", "テスト☆ユーザー", 22);
		// 存在しないコレクションに追加しようとする
		DocumentEntity<?> res = client.updateDocument(collectionName404, 1, value, -1, null, null, null);
		assertThat(res, is(nullValue()));
		
		try {
			client.updateDocument(collectionName404, 1, value, -1, null, null, Mode.RAISE_ERROR);
			fail("例外が発生しないといけないの");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}

	@Test
	public void test_update_404_2() throws AvocadoException {
		
		TestComplexEntity01 value = new TestComplexEntity01("test-user", "テスト☆ユーザー", 22);
		// 存在するコレクションだが、ドキュメントが存在しない
		DocumentEntity<?> res = client.updateDocument(collectionName, 1, value, -1, null, null, null);
		assertThat(res, is(nullValue()));
		
		try {
			client.updateDocument(collectionName, 1, value, -1, null, null, Mode.RAISE_ERROR);
			fail("例外が発生しないといけないの");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1202));
		}
		
	}

	// TODO Delete
	@Test
	public void test_checkDocument() throws AvocadoException {

		DocumentEntity<String> doc = client.createDocument(collectionName, "xx", null, false, Mode.RETURN_NULL);

		client.checkDocument(doc.getDocumentHandle());
		
	}
	
}
