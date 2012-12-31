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
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.CollectionEntity;
import at.orz.arangodb.entity.CollectionStatus;
import at.orz.arangodb.entity.CollectionsEntity;
import at.orz.arangodb.entity.DocumentEntity;

/**
 * UnitTest for REST API "collections"
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverCollectionTest extends BaseTest {
	
	private static Logger logger = LoggerFactory.getLogger(ArangoDriverCollectionTest.class);
	
	final String collectionName = "unit_test_arango_001"; // 通常ケースで使うコレクション名
	final String collectionName2 = "unit_test_arango_002";
	final String collectionName404 = "unit_test_arango_404"; // 存在しないコレクション名
	
	@Before
	public void before() throws ArangoException {
		
		logger.debug("----------");
		
		// 事前に消しておく
		for (String col: new String[]{collectionName, collectionName2, collectionName404}) {
			try {
				driver.deleteCollection(col);
			} catch (ArangoException e) {}
		}

		logger.debug("--");
		
	}
	
	@After
	public void after() {
		logger.debug("----------");
	}
	
	/**
	 * 正常系のテスト。
	 * @throws ArangoException
	 */
	@Test
	@Parameters
	public void test_create_01() throws ArangoException {
		
		CollectionEntity res1 = driver.createCollection(collectionName);
		assertThat(res1, is(notNullValue()));
		assertThat(res1.getCode(), is(200));
		
	}
	
	/**
	 * 既に存在する場合の挙動確認。
	 * @throws ArangoException
	 */
	@Test
	public void test_create_dup() throws ArangoException {

		CollectionEntity res1 = driver.createCollection(collectionName);
		assertThat(res1, is(notNullValue()));
		assertThat(res1.getCode(), is(200));
		assertThat(res1.getName(), is(collectionName));
		assertThat(res1.getWaitForSync(), is(false));
		assertThat(res1.getId(), is(not(0L)));
		assertThat(res1.getStatus(), is(CollectionStatus.LOADED));
		
		{
			try {
				CollectionEntity res = driver.createCollection(collectionName);
				fail("ここに来てはダメー！");
			} catch (ArangoException e) {
				assertThat(e.getCode(), is(400));
				assertThat(e.getErrorNumber(), is(1207));
			}
		}
		
	}
	
	@Test
	public void test_getCollection_01() throws ArangoException {
		
		CollectionEntity res1 = driver.createCollection(collectionName);
		assertThat(res1.getCode(), is(200));
		
		long collectionId = res1.getId();
		
		// IDで取得
		CollectionEntity entity1 = driver.getCollection(collectionId);
		// 名前で取得
		CollectionEntity entity2 = driver.getCollection(collectionName);
		assertThat(entity1.getId(), is(collectionId));
		assertThat(entity2.getId(), is(collectionId));
		assertThat(entity1.getName(), is(collectionName));
		assertThat(entity2.getName(), is(collectionName));
		
	}
	
	/**
	 * 存在しないコレクションを取得する場合
	 * @throws ArangoException
	 */
	@Test
	public void test_getCollection_404() throws ArangoException {
		
//		CollectionEntity collection = client.getCollection(collectionName404);
//		assertThat(collection, is(nullValue()));
		
		try {
			driver.getCollection(collectionName404);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}
	
	@Test
	public void test_getCollectionProperties_01() throws ArangoException {

		CollectionEntity res1 = driver.createCollection(collectionName);
		assertThat(res1.getCode(), is(200));
		
		CollectionEntity collection = driver.getCollectionProperties(collectionName);
		assertThat(collection.getCode(), is(200));
		assertThat(collection.getId(), is(res1.getId()));
		assertThat(collection.getName(), is(collectionName));
		assertThat(collection.getWaitForSync(), is(Boolean.FALSE));
		assertThat(collection.getJournalSize(), is(32L * 1024 * 1024)); // 32MB
		// TODO Countがないこと
		// TODO status
		// TODO type
		
	}

	/**
	 * 存在しないコレクションを指定した場合。
	 * @throws ArangoException
	 */
	@Test
	public void test_getCollectionProperties_404() throws ArangoException {

		try {
			driver.getCollectionProperties(collectionName404);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}

	
	@Test
	public void test_getCollectionCount_01() throws ArangoException {

		CollectionEntity res1 = driver.createCollection(collectionName);
		assertThat(res1.getCode(), is(200));
		
		CollectionEntity collection = driver.getCollectionCount(collectionName);
		assertThat(collection.getCode(), is(200));
		assertThat(collection.getId(), is(res1.getId()));
		assertThat(collection.getName(), is(collectionName));
		assertThat(collection.getWaitForSync(), is(Boolean.FALSE));
		assertThat(collection.getJournalSize(), is(32L * 1024 * 1024)); // 32MB
		assertThat(collection.getCount(), is(0L)); // 何も入っていないのでゼロ
		// TODO type, status
		
		// 100個ほどドキュメントを入れてみる
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01(
					"test_user" + i, "テストユーザー:" + i, 20 + i);
			driver.createDocument(collectionName, value, false, true);
		}
		
		// もっかいアクセスして10になっているか確認する
		collection = driver.getCollectionCount(collectionName);
		assertThat(collection.getCount(), is(100L));
		
	}

	/**
	 * 存在しないコレクションを指定した場合。
	 * @throws ArangoException
	 */
	@Test
	public void test_getCollectionCount_404() throws ArangoException {

		try {
			driver.getCollectionCount(collectionName404);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}

	
	@Test
	public void test_getCollectionFigures_01() throws ArangoException {
		
		// コレクションを作る
		CollectionEntity res1 = driver.createCollection(collectionName);
		assertThat(res1.getCode(), is(200));
		
		// 100個ほどドキュメントを入れてみる
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01(
					"test_user" + i, "テストユーザー:" + i, 20 + i);
			DocumentEntity<TestComplexEntity01> entity = driver.createDocument(collectionName, value, false, true);
			// 1個消す
			if (i == 50) {
				driver.deleteDocument(entity.getDocumentHandle(), -1, null);
			}
		}
		
		CollectionEntity collection = driver.getCollectionFigures(collectionName);
		assertThat(collection.getCode(), is(200));
		assertThat(collection.getId(), is(res1.getId()));
		assertThat(collection.getName(), is(collectionName));
		assertThat(collection.getWaitForSync(), is(Boolean.FALSE));
		assertThat(collection.getJournalSize(), is(32L * 1024 * 1024)); // 32MB
		assertThat(collection.getCount(), is(99L)); // 何も入っていないのでゼロ
		// TODO status, type
		
		assertThat(collection.getFigures().getAliveCount(), is(99L));
		assertThat(collection.getFigures().getAliveSize(), is(not(0L))); // 7603L // 1つ77バイト
		assertThat(collection.getFigures().getDeadCount(), is(1L));
		assertThat(collection.getFigures().getDeadSize(), is(not(0L)));
		// TODO deletion
		assertThat(collection.getFigures().getDatafileCount(), is(not(0L)));
		// TODO journals
		
	}
	
	/**
	 * 存在しないコレクションを指定した場合。
	 * @throws ArangoException
	 */
	@Test
	public void test_getCollectionFigures_404() throws ArangoException {

		try {
			driver.getCollectionFigures(collectionName404);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}

	@Test
	public void test_getCollections() throws ArangoException {
		
		for (int i = 0; i < 10; i++) {
			try {
				driver.createCollection("unit_test_arango_" + (1000 + i));
			} catch (ArangoException e) {}
		}
		
		CollectionsEntity collections = driver.getCollections();
		assertThat(collections.getCode(), is(200));
		Map<String, CollectionEntity> map = collections.getNames();
		for (int i = 0; i < 10; i++) {
			String collectionName = "unit_test_arango_" + (1000 + i);
			CollectionEntity collection = map.get(collectionName);
			// id, name, status
			assertThat(collection, is(notNullValue()));
			assertThat(collection.getId(), is(not(0L)));
			assertThat(collection.getName(), is(collectionName));
		}
		
	}
	
	@Test
	public void test_load_unload() throws ArangoException {
		
		CollectionEntity collection = driver.createCollection(collectionName, null, null, null, null);
		assertThat(collection, is(notNullValue()));
		assertThat(collection.getCode(), is(200));

		CollectionEntity collection1 = driver.unloadCollection(collectionName);
		assertThat(collection1, is(notNullValue()));
		assertThat(collection1.getCode(), is(200));
		
		assertThat(collection1.getStatus(), anyOf(is(CollectionStatus.UNLOADED), is(CollectionStatus.IN_THE_PROCESS_OF_BEING_UNLOADED)));
		
		CollectionEntity collection2 = driver.loadCollection(collectionName);
		assertThat(collection2, is(notNullValue()));
		assertThat(collection2.getCode(), is(200));
		assertThat(collection2.getStatus(), is(CollectionStatus.LOADED));

	}
	
	@Test
	public void test_load_404() throws ArangoException {
		
		try {
			driver.loadCollection(collectionName404);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}

	}	

	@Test
	public void test_unload_404() throws ArangoException {
		
		try {
			driver.unloadCollection(collectionName404);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}

	}	
	
	@Test
	public void test_truncate() throws ArangoException {
		
		CollectionEntity collection = driver.createCollection(collectionName, true, null, null, null);
		assertThat(collection, is(notNullValue()));
		assertThat(collection.getCode(), is(200));
		
		// 100個ほどドキュメントを入れてみる
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01(
					"test_user" + i, "テストユーザー:" + i, 20 + i);
			assertThat(driver.createDocument(collectionName, value, false, true).getStatusCode(), is(201));
		}
		// 100個入ったよね？
		assertThat(driver.getCollectionCount(collectionName).getCount(), is(100L));

		// 抹殺じゃー！
		CollectionEntity collection2 = driver.truncateCollection(collectionName);
		assertThat(collection2, is(notNullValue()));
		assertThat(collection2.getCode(), is(200));
		
		// 0件になってるか確認
		assertThat(driver.getCollectionCount(collectionName).getCount(), is(0L));
		
	}
	
	@Test
	public void test_truncate_404() throws ArangoException {
		
		try {
			driver.unloadCollection(collectionName404);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}
	
	@Test
	public void test_setCollectionProperties() throws ArangoException {
	
		CollectionEntity collection = driver.createCollection(collectionName, false, null, null, null);
		assertThat(collection, is(notNullValue()));
		assertThat(collection.getCode(), is(200));
		assertThat(collection.getWaitForSync(), is(Boolean.FALSE));
		
		// waitForSyncをFalseからTrueに設定
		CollectionEntity col = driver.setCollectionProperties(collectionName, true);
		assertThat(col.getCode(), is(200));
		assertThat(col.getWaitForSync(), is(Boolean.TRUE));
	
	}
	
	@Test
	public void test_setCollectionProperties_404() throws ArangoException {
		
		try {
			driver.setCollectionProperties(collectionName404, true);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}
	
	@Test
	public void test_delete() throws ArangoException {
		
		// コレクションを適当に10個作る
		TreeSet<String> collectionNames = new TreeSet<String>();
		for (int i = 0; i < 10; i++) {
			try {
				CollectionEntity col = driver.createCollection("unit_test_arango_" + (1000 + i), true, null, null, null);
				long collectionId = col.getId();
				if (i == 5) {
					// 1個だけ消す
					CollectionEntity res = driver.deleteCollection(collectionId);
					assertThat(res.getCode(), is(200));
					assertThat(res.getId(), is(collectionId));
				} else {
					collectionNames.add(col.getName());
				}
			} catch (ArangoException e) {}
		}
		
		// 残りの9個は残っていること
		Map<String, CollectionEntity> collections = driver.getCollections().getNames();
		for (String name : collectionNames) {
			assertThat(collections.containsKey(name), is(true));
		}
		
	}
	
	@Test
	public void test_delete_404() throws ArangoException {
		
		try {
			driver.deleteCollection(collectionName404);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}

	}
	
	@Test
	public void test_rename_404() throws ArangoException {

		CollectionEntity collection = driver.createCollection(collectionName, true, null, null, null);
		assertThat(collection.getCode(), is(200));
		
		try {
			driver.renameCollection(collectionName404, collectionName);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}
	
	/**
	 * Rename先が既に存在する場合
	 * @throws ArangoException
	 */
	@Test
	public void test_rename_dup() throws ArangoException {

		CollectionEntity collection1 = driver.createCollection(collectionName, true, null, null, null);
		assertThat(collection1.getCode(), is(200));

		CollectionEntity collection2 = driver.createCollection(collectionName2, true, null, null, null);
		assertThat(collection2.getCode(), is(200));

		try {
			driver.renameCollection(collectionName, collectionName2);
			fail("ここに来てはダメー！");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(400));
			assertThat(e.getErrorNumber(), is(1207));
		}
		
	}
	
}
