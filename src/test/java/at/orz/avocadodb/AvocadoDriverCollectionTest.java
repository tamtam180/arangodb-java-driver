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
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.orz.avocadodb.AvocadoDriver.Mode;
import at.orz.avocadodb.entity.CollectionEntity;
import at.orz.avocadodb.entity.CollectionStatus;
import at.orz.avocadodb.entity.CollectionsEntity;
import at.orz.avocadodb.entity.DocumentEntity;

/**
 * UnitTest for REST API "collections"
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AvocadoDriverCollectionTest extends BaseTest {
	
	private static Logger logger = LoggerFactory.getLogger(AvocadoDriverCollectionTest.class);
	
	final String collectionName = "unit_test_avocado_001"; // 通常ケースで使うコレクション名
	final String collectionName2 = "unit_test_avocado_002";
	final String collectionName404 = "unit_test_avocado_404"; // 存在しないコレクション名
	
	@Before
	public void before() throws AvocadoException {
		
		logger.debug("----------");
		
		// 事前に消しておく
		client.deleteCollection(collectionName, null);
		client.deleteCollection(collectionName2, null);
		client.deleteCollection(collectionName404, null);

		logger.debug("--");
		
	}
	
	@After
	public void after() {
		logger.debug("----------");
	}
	
	/**
	 * 正常系のテスト。
	 * @throws AvocadoException
	 */
	@Test
	@Parameters
	public void test_create_01() throws AvocadoException {
		
		CollectionEntity res1 = client.createCollection(collectionName, null, null);
		assertThat(res1, is(notNullValue()));
		assertThat(res1.getCode(), is(200));
		
	}
	
	/**
	 * 既に存在する場合の挙動確認。
	 * @throws AvocadoException
	 */
	@Test
	public void test_create_dup() throws AvocadoException {

		CollectionEntity res1 = client.createCollection(collectionName, null, null);
		assertThat(res1, is(notNullValue()));
		assertThat(res1.getCode(), is(200));
		assertThat(res1.getName(), is(collectionName));
		assertThat(res1.getWaitForSync(), is(false));
		assertThat(res1.getId(), is(not(0L)));
		assertThat(res1.getStatus(), is(CollectionStatus.LOADED));
		
		// 重複した時にNULLを返すよ
		{
			CollectionEntity res = client.createCollection(collectionName, true, Mode.RETURN_NULL);
			assertThat(res, is(nullValue()));
		}
		
		// 重複した時に例外を飛ばすよ
		{
			try {
				CollectionEntity res = client.createCollection(collectionName, true, Mode.RAISE_ERROR);
				fail("ここに来てはダメー！");
			} catch (AvocadoException e) {
				assertThat(e.getCode(), is(400));
				assertThat(e.getErrorNumber(), is(1207));
			}
		}
		
	}
	
	@Test
	public void test_getCollection_01() throws AvocadoException {
		
		CollectionEntity res1 = client.createCollection(collectionName, null, null);
		assertThat(res1.getCode(), is(200));
		
		long collectionId = res1.getId();
		
		// IDで取得
		CollectionEntity entity1 = client.getCollection(collectionId, null);
		// 名前で取得
		CollectionEntity entity2 = client.getCollection(collectionName, null);
		assertThat(entity1.getId(), is(collectionId));
		assertThat(entity2.getId(), is(collectionId));
		assertThat(entity1.getName(), is(collectionName));
		assertThat(entity2.getName(), is(collectionName));
		
	}
	
	/**
	 * 存在しないコレクションを取得する場合
	 * @throws AvocadoException
	 */
	@Test
	public void test_getCollection_404() throws AvocadoException {
		
		CollectionEntity collection = client.getCollection(collectionName404, null);
		assertThat(collection, is(nullValue()));
		
		try {
			client.getCollection(collectionName404, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}
	
	@Test
	public void test_getCollectionProperties_01() throws AvocadoException {

		CollectionEntity res1 = client.createCollection(collectionName, null, null);
		assertThat(res1.getCode(), is(200));
		
		CollectionEntity collection = client.getCollectionProperties(collectionName, null);
		assertThat(collection.getCode(), is(200));
		assertThat(collection.getId(), is(res1.getId()));
		assertThat(collection.getName(), is(collectionName));
		assertThat(collection.getWaitForSync(), is(Boolean.FALSE));
		assertThat(collection.getJournalSize(), is(32L * 1024 * 1024)); // 32MB
		// TODO Countがないこと
		
	}

	/**
	 * 存在しないコレクションを指定した場合。
	 * @throws AvocadoException
	 */
	@Test
	public void test_getCollectionProperties_404() throws AvocadoException {

		CollectionEntity collection = client.getCollectionProperties(collectionName404, null);
		assertThat(collection, is(nullValue()));
		
		try {
			client.getCollectionProperties(collectionName404, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}

	
	@Test
	public void test_getCollectionCount_01() throws AvocadoException {

		CollectionEntity res1 = client.createCollection(collectionName, null, null);
		assertThat(res1.getCode(), is(200));
		
		CollectionEntity collection = client.getCollectionCount(collectionName, null);
		assertThat(collection.getCode(), is(200));
		assertThat(collection.getId(), is(res1.getId()));
		assertThat(collection.getName(), is(collectionName));
		assertThat(collection.getWaitForSync(), is(Boolean.FALSE));
		assertThat(collection.getJournalSize(), is(32L * 1024 * 1024)); // 32MB
		assertThat(collection.getCount(), is(0L)); // 何も入っていないのでゼロ
		
		// 100個ほどドキュメントを入れてみる
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01(
					"test_user" + i, "テストユーザー:" + i, 20 + i);
			client.createDocument(collectionName, value, false, true, null);
		}
		
		// もっかいアクセスして10になっているか確認する
		collection = client.getCollectionCount(collectionName, null);
		assertThat(collection.getCount(), is(100L));
		
	}

	/**
	 * 存在しないコレクションを指定した場合。
	 * @throws AvocadoException
	 */
	@Test
	public void test_getCollectionCount_404() throws AvocadoException {

		CollectionEntity collection = client.getCollectionCount(collectionName404, null);
		assertThat(collection, is(nullValue()));
		
		try {
			client.getCollectionCount(collectionName404, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}

	
	@Test
	public void test_getCollectionFigures_01() throws AvocadoException {
		
		// コレクションを作る
		CollectionEntity res1 = client.createCollection(collectionName, null, null);
		assertThat(res1.getCode(), is(200));
		
		// 100個ほどドキュメントを入れてみる
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01(
					"test_user" + i, "テストユーザー:" + i, 20 + i);
			DocumentEntity<TestComplexEntity01> entity = client.createDocument(collectionName, value, false, true, null);
			if (i == 50) {
				// 1個消す
				client.deleteDocument(entity.getDocumentHandle(), -1, null, null);
			}
		}
		
		CollectionEntity collection = client.getCollectionFigures(collectionName, null);
		assertThat(collection.getCode(), is(200));
		assertThat(collection.getId(), is(res1.getId()));
		assertThat(collection.getName(), is(collectionName));
		assertThat(collection.getWaitForSync(), is(Boolean.FALSE));
		assertThat(collection.getJournalSize(), is(32L * 1024 * 1024)); // 32MB
		assertThat(collection.getCount(), is(99L)); // 何も入っていないのでゼロ
		
		assertThat(collection.getFigures().getAliveCount(), is(99L));
		assertThat(collection.getFigures().getAliveSize(), is(not(0L))); // 7603L // 1つ77バイト
		assertThat(collection.getFigures().getDeadCount(), is(1L));
		assertThat(collection.getFigures().getDeadSize(), is(not(0L)));
		assertThat(collection.getFigures().getDatafileCount(), is(not(0L)));
		
	}
	
	/**
	 * 存在しないコレクションを指定した場合。
	 * @throws AvocadoException
	 */
	@Test
	public void test_getCollectionFigures_404() throws AvocadoException {

		CollectionEntity collection = client.getCollectionFigures(collectionName404, null);
		assertThat(collection, is(nullValue()));
		
		try {
			client.getCollectionFigures(collectionName404, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}

	@Test
	public void test_getCollections() throws AvocadoException {
		
		for (int i = 0; i < 10; i++) {
			client.createCollection("unit_test_avocado_" + (1000 + i), null, null);
		}
		
		CollectionsEntity collections = client.getCollections();
		assertThat(collections.getCode(), is(200));
		Map<String, CollectionEntity> map = collections.getNames();
		for (int i = 0; i < 10; i++) {
			String collectionName = "unit_test_avocado_" + (1000 + i);
			CollectionEntity collection = map.get(collectionName);
			// id, name, status
			assertThat(collection, is(notNullValue()));
			assertThat(collection.getId(), is(not(0L)));
			assertThat(collection.getName(), is(collectionName));
		}
		
	}
	
	@Test
	public void test_load_unload() throws AvocadoException {
		
		CollectionEntity collection = client.createCollection(collectionName, null, null);
		assertThat(collection, is(notNullValue()));
		assertThat(collection.getCode(), is(200));

		CollectionEntity collection1 = client.unloadCollection(collectionName, null);
		assertThat(collection1, is(notNullValue()));
		assertThat(collection1.getCode(), is(200));
		assertThat(collection1.getStatus(), is(CollectionStatus.IN_THE_PROCESS_OF_BEING_UNLOADED));

		CollectionEntity collection2 = client.loadCollection(collectionName, null);
		assertThat(collection2, is(notNullValue()));
		assertThat(collection2.getCode(), is(200));
		assertThat(collection2.getStatus(), is(CollectionStatus.LOADED));

	}
	
	@Test
	public void test_load_404() throws AvocadoException {
		
		CollectionEntity collection = client.loadCollection(collectionName404, null);
		assertThat(collection, is(nullValue()));
		
		try {
			client.loadCollection(collectionName404, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}

	}	

	@Test
	public void test_unload_404() throws AvocadoException {
		
		CollectionEntity collection = client.unloadCollection(collectionName404, null);
		assertThat(collection, is(nullValue()));
		
		try {
			client.unloadCollection(collectionName404, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}

	}	
	
	@Test
	public void test_truncate() throws AvocadoException {
		
		CollectionEntity collection = client.createCollection(collectionName, null, null);
		assertThat(collection, is(notNullValue()));
		assertThat(collection.getCode(), is(200));
		
		// 100個ほどドキュメントを入れてみる
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01(
					"test_user" + i, "テストユーザー:" + i, 20 + i);
			assertThat(client.createDocument(collectionName, value, false, true, null).getStatusCode(), is(202));
		}
		// 100個入ったよね？
		assertThat(client.getCollectionCount(collectionName, null).getCount(), is(100L));

		// 抹殺じゃー！
		CollectionEntity collection2 = client.truncateCollection(collectionName, Mode.RETURN_NULL);
		assertThat(collection2, is(notNullValue()));
		assertThat(collection2.getCode(), is(200));
		
		// 0件になってるか確認
		assertThat(client.getCollectionCount(collectionName, null).getCount(), is(0L));
		
	}
	
	@Test
	public void test_truncate_404() throws AvocadoException {
		
		CollectionEntity collection = client.truncateCollection(collectionName404, null);
		assertThat(collection, is(nullValue()));
		
		try {
			client.unloadCollection(collectionName404, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}
	
	@Test
	public void test_setCollectionProperties() throws AvocadoException {
	
		CollectionEntity collection = client.createCollection(collectionName, null, null);
		assertThat(collection, is(notNullValue()));
		assertThat(collection.getCode(), is(200));
		assertThat(collection.getWaitForSync(), is(Boolean.FALSE));
		
		// waitForSyncをFalseからTrueに設定
		CollectionEntity col = client.setCollectionProperties(collectionName, true, null);
		assertThat(col.getCode(), is(200));
		assertThat(col.getWaitForSync(), is(Boolean.TRUE));
	
	}
	
	@Test
	public void test_setCollectionProperties_404() throws AvocadoException {
		
		CollectionEntity collection = client.setCollectionProperties(collectionName404, true, null);
		assertThat(collection, is(nullValue()));
		
		try {
			client.setCollectionProperties(collectionName404, true, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}
	
	@Test
	public void test_delete() throws AvocadoException {
		
		// コレクションを適当に10個作る
		TreeSet<String> collectionNames = new TreeSet<String>();
		for (int i = 0; i < 10; i++) {
			CollectionEntity col = client.createCollection("unit_test_avocado_" + (1000 + i), null, Mode.DUP_GET);
			long collectionId = col.getId();
			if (i == 5) {
				// 1個だけ消す
				CollectionEntity res = client.deleteCollection(collectionId, Mode.RAISE_ERROR);
				assertThat(res.getCode(), is(200));
				assertThat(res.getId(), is(collectionId));
			} else {
				collectionNames.add(col.getName());
			}
		}
		
		// 残りの9個は残っていること
		Map<String, CollectionEntity> collections = client.getCollections().getNames();
		for (String name : collectionNames) {
			assertThat(collections.containsKey(name), is(true));
		}
		
	}
	
	@Test
	public void test_delete_404() throws AvocadoException {
		
		CollectionEntity collection = client.deleteCollection(collectionName404, null);
		assertThat(collection, is(nullValue()));
		
		try {
			client.deleteCollection(collectionName404, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}

	}
	
	@Test
	public void test_rename_404() throws AvocadoException {

		CollectionEntity collection = client.createCollection(collectionName, null, null);
		assertThat(collection.getCode(), is(200));
		
		CollectionEntity collection1 = client.renameCollection(collectionName404, collectionName, null);
		assertThat(collection1, is(nullValue()));
		
		try {
			client.renameCollection(collectionName404, collectionName, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
		
	}
	
	/**
	 * Rename先が既に存在する場合
	 * @throws AvocadoException
	 */
	@Test
	public void test_rename_dup() throws AvocadoException {

		CollectionEntity collection1 = client.createCollection(collectionName, null, null);
		assertThat(collection1.getCode(), is(200));

		CollectionEntity collection2 = client.createCollection(collectionName2, null, null);
		assertThat(collection2.getCode(), is(200));

		
		CollectionEntity collection3 = client.renameCollection(collectionName, collectionName2, null);
		assertThat(collection3, is(nullValue()));
		
		try {
			client.renameCollection(collectionName, collectionName2, Mode.RAISE_ERROR);
			fail("ここに来てはダメー！");
		} catch (AvocadoException e) {
			assertThat(e.getCode(), is(400));
			assertThat(e.getErrorNumber(), is(1207));
		}
		
	}
	
}
