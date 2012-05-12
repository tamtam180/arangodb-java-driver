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
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.orz.avocadodb.AvocadoDriver.Mode;
import at.orz.avocadodb.entity.CollectionEntity;
import at.orz.avocadodb.entity.IndexEntity;
import at.orz.avocadodb.entity.IndexType;
import at.orz.avocadodb.entity.IndexesEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AvocadoDriverIndexTest extends BaseTest {

	private static Logger logger = LoggerFactory.getLogger(AvocadoDriverCollectionTest.class);
	
	final String collectionName = "unit_test_avocado_index"; // 
	final String collectionName404 = "unit_test_avocado_404"; // 存在しないコレクション名
	
	CollectionEntity col1;
	
	@Before
	public void before() throws AvocadoException {
		
		logger.debug("----------");
		
		// 事前に消しておく
		client.deleteCollection(collectionName, null);
		client.deleteCollection(collectionName404, null);

		// 1は作る
		col1 =  client.createCollection(collectionName, false, Mode.RAISE_ERROR);
		
		logger.debug("--");
		
	}
	
	@After
	public void after() {
		logger.debug("----------");
	}

	@Test
	public void test_create_index() throws AvocadoException {
		
		{
			IndexEntity entity = client.createIndex(collectionName, IndexType.GEO, false, "a");
			
			assertThat(entity, is(notNullValue()));
			assertThat(entity.getCode(), is(201));
			assertThat(entity.isError(), is(false));
			assertThat(entity.isNewlyCreated(), is(true));
			assertThat(entity.isGetJson(), is(false));
			assertThat(entity.getId(), is(notNullValue()));
			assertThat(entity.getType(), is(IndexType.GEO));
		}
		
		// 重複して作成する
		{
			IndexEntity entity = client.createIndex(collectionName, IndexType.GEO, false, "a");
			
			assertThat(entity, is(notNullValue()));
			assertThat(entity.getCode(), is(200));
			assertThat(entity.isError(), is(false));
			assertThat(entity.isNewlyCreated(), is(false));
			assertThat(entity.isGetJson(), is(false));
			assertThat(entity.getId(), is(notNullValue()));
			assertThat(entity.getType(), is(IndexType.GEO));
		}
		
	}

	@Test
	public void test_create_index_404() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName404, IndexType.GEO, false, "a");
		assertThat(entity, is(nullValue()));
		
	}

	@Test
	public void test_create_geo_index_unique() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName, IndexType.GEO, true, "a", "b");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGetJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.GEO));
		
	}

	@Test
	public void test_create_geo_index_over_columnnum() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName, IndexType.GEO, true, "a", "b", "c");

		assertThat(entity, is(nullValue()));
		
	}

	@Test
	public void test_create_hash_index() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName, IndexType.HASH, false, "a", "b", "c", "d", "e", "f", "g");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGetJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.HASH));
		
	}

	@Test
	public void test_create_hash_index_unique() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName, IndexType.HASH, true, "a", "b", "c", "d", "e", "f", "g");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGetJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.HASH));
		
	}

	
	@Test
	public void test_create_skiplist_index() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName, IndexType.SKIPLIST, false, "a", "b", "c", "d", "e", "f", "g");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGetJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.SKIPLIST));
		
	}

	@Test
	public void test_create_skiplist_index_unique() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName, IndexType.SKIPLIST, true, "a", "b", "c", "d", "e", "f", "g");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGetJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.SKIPLIST));
		
	}

	@Test
	public void test_create_hash_index_with_document() throws AvocadoException {
		
		for (int i = 0; i < 100; i++) {
			TestComplexEntity01 value = new TestComplexEntity01(
					"user_" + i,
					"",
					i
					);

			assertThat(client.createDocument(collectionName, value, false, false, null), is(notNullValue()));
		}
		
		IndexEntity entity = client.createIndex(collectionName, IndexType.HASH, true, "name", "age");

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGetJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.HASH));
		
	}

	@Test
	public void test_delete_index() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName, IndexType.HASH, true, "name", "age");
		assertThat(entity, is(notNullValue()));
		assertThat(entity.getId(), is(notNullValue()));
		
		String id = entity.getId();
		
		IndexEntity entity2 = client.deleteIndex(id);

		assertThat(entity2, is(notNullValue()));
		assertThat(entity2.getCode(), is(200));
		assertThat(entity2.isError(), is(false));
		assertThat(entity2.getId(), is(id));

	}

	@Test
	public void test_delete_index_pk() throws AvocadoException {
		
		IndexEntity entity2 = client.deleteIndex(collectionName + "/0");
		assertThat(entity2, is(nullValue()));

	}
	
	@Test
	public void test_delete_index_404_1() throws AvocadoException {
		
		IndexEntity entity2 = client.deleteIndex(collectionName + "/1");
		assertThat(entity2, is(nullValue()));

	}

	@Test
	public void test_delete_index_404_2() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName404, IndexType.HASH, true, "name", "age");
		assertThat(entity, is(nullValue()));
		
	}

	
	/**
	 * ユニークインデックスの列が重複した場合。
	 * TODO: あとで
	 * @throws AvocadoException
	 */
	@Test
	@Ignore
	public void test_create_hash_index_dup_unique() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName, IndexType.HASH, true, "user", "age");

		assertThat(client.createDocument(collectionName, new TestComplexEntity01("寿司天ぷら", "", 18), false, false, null), is(notNullValue()));
		assertThat(client.createDocument(collectionName, new TestComplexEntity01("寿司天ぷら", "", 18), false, false, null), is(notNullValue()));
		
		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.isGetJson(), is(false));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.HASH));
		
	}

	@Test
	public void test_create_cap_index() throws AvocadoException {
		
		IndexEntity entity = client.createCappedIndex(collectionName, 10);

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getCode(), is(201));
		assertThat(entity.isError(), is(false));
		assertThat(entity.isNewlyCreated(), is(true));
		assertThat(entity.getSize(), is(10));
		assertThat(entity.getId(), is(notNullValue()));
		assertThat(entity.getType(), is(IndexType.CAP));
		
		// 確認 ピンポイントで取得
		IndexEntity entity2 = client.getIndex(entity.getId());
		assertThat(entity2.getCode(), is(200));
		assertThat(entity2.isError(), is(false));
		assertThat(entity2.isNewlyCreated(), is(false));
		assertThat(entity2.getSize(), is(10));
		assertThat(entity2.getId(), is(entity.getId()));
		assertThat(entity2.getType(), is(IndexType.CAP));
		
		// 確認 インデックス一覧を取得
		IndexesEntity indexes = client.getIndexes(collectionName);
		assertThat(indexes.getCode(), is(200));
		assertThat(indexes.isError(), is(false));
		assertThat(indexes.getIndexes().size(), is(2));
		
		String pkHandle = col1.getId() + "/0";
		IndexEntity pk = indexes.getIdentifiers().get(pkHandle);
		assertThat(pk.getType(), is(IndexType.PRIMARY));
		assertThat(pk.getFields().size(), is(1));
		assertThat(pk.getFields().get(0), is("_id"));
		
		IndexEntity idx1 = indexes.getIdentifiers().get(entity.getId());
		assertThat(idx1.getType(), is(IndexType.CAP));
		assertThat(idx1.getFields(), is(nullValue()));
		assertThat(idx1.getSize(), is(10));
		
	}

	@Test
	public void test_create_cap_index_404() throws AvocadoException {
		
		IndexEntity entity = client.createCappedIndex(collectionName404, 10);

		assertThat(entity, is(nullValue()));
		//assertThat(entity.getCode(), is(404));
		//assertThat(entity.getErrorNumber(), is(1203));
		
	}

	
	@Test
	public void test_getIndexes() throws AvocadoException {
		
		IndexEntity entity = client.createIndex(collectionName, IndexType.HASH, true, "name", "age");
		assertThat(entity, is(notNullValue()));
		
		IndexesEntity indexes = client.getIndexes(collectionName);
		
		assertThat(indexes, is(notNullValue()));
		
		assertThat(indexes.getIndexes().size(), is(2));
		assertThat(indexes.getIndexes().get(0).getType(), is(IndexType.PRIMARY));
		assertThat(indexes.getIndexes().get(0).getFields().size(), is(1));
		assertThat(indexes.getIndexes().get(0).getFields().get(0), is("_id"));
		assertThat(indexes.getIndexes().get(1).getType(), is(IndexType.HASH));
		assertThat(indexes.getIndexes().get(1).getFields().size(), is(2));
		assertThat(indexes.getIndexes().get(1).getFields().get(0), is("name"));
		assertThat(indexes.getIndexes().get(1).getFields().get(1), is("age"));

		String id1 = indexes.getIndexes().get(0).getId();
		String id2 = indexes.getIndexes().get(1).getId();
		
		assertThat(indexes.getIdentifiers().size(), is(2));
		assertThat(indexes.getIdentifiers().get(id1).getType(), is(IndexType.PRIMARY));
		assertThat(indexes.getIdentifiers().get(id1).getFields().size(), is(1));
		assertThat(indexes.getIdentifiers().get(id1).getFields().get(0), is("_id"));
		assertThat(indexes.getIdentifiers().get(id2).getType(), is(IndexType.HASH));
		assertThat(indexes.getIdentifiers().get(id2).getFields().size(), is(2));
		assertThat(indexes.getIdentifiers().get(id2).getFields().get(0), is("name"));
		assertThat(indexes.getIdentifiers().get(id2).getFields().get(1), is("age"));

	}
	
}
