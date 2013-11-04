/*
 * Copyright (C) 2012,2013 tamtam180
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

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.orz.arangodb.entity.ReplicationDumpRecord;
import at.orz.arangodb.entity.ReplicationInventoryEntity;
import at.orz.arangodb.entity.ReplicationInventoryEntity.Collection;
import at.orz.arangodb.util.DumpHandler;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverReplicationTest extends BaseTest {

	public ArangoDriverReplicationTest(ArangoConfigure configure, ArangoDriver driver) {
		super(configure, driver);
	}
	
	@Before
	public void before() {
		driver.setDefaultDatabase(null);
	}

	@Test
	public void test_get_inventory() throws ArangoException {
		
		ReplicationInventoryEntity entity = driver.getReplicationInventory();
		assertThat(entity.getCode(), is(0));
		assertThat(entity.getStatusCode(), is(200));
		
		assertThat(entity.getTick(), is(not(0L)));
		assertThat(entity.getState().isRunning(), is(false));
		assertThat(entity.getCollections().size(), is(not(0)));
		
	}

	@Test
	public void test_get_inventory_includeSystem() throws ArangoException {
		
		ReplicationInventoryEntity entity = driver.getReplicationInventory(true);
		assertThat(entity.getCode(), is(0));
		assertThat(entity.getStatusCode(), is(200));

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		for (Collection col: entity.getCollections()) {
			System.out.println(gson.toJson(col.getParameter()));
			System.out.println(gson.toJson(col.getIndexes()));
		}

	}

	@Test
	public void test_get_inventory_404() throws ArangoException {
		
		driver.setDefaultDatabase("database-404");
		try {
			driver.getReplicationInventory();
			fail();
		} catch(ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1228)); // database not found
		}
		
	}
	
	@Test
	public void test_get_dump() throws ArangoException {
		
		String collectionName = "rep_dump_test";
		
		try {
			driver.deleteCollection(collectionName);
		} catch (ArangoException e) {}
		try {
			driver.createCollection(collectionName);
		} catch (ArangoException e) {}

		// create 10 document
		for (int i = 0; i < 10; i++) {
			TestComplexEntity01 entity = new TestComplexEntity01("user-" + i, "desc-" + i, 20+i);
			driver.createDocument(collectionName, entity, true, null);
		}
		// truncate
		try {
			driver.truncateCollection(collectionName);
		} catch (ArangoException e) {}
		// create 1 document
		TestComplexEntity01 entity = new TestComplexEntity01("user-99", "desc-99", 99);
		driver.createDocument(collectionName, entity, true, null);
		
		final AtomicInteger upsertCount = new AtomicInteger(0);
		final AtomicInteger deleteCount = new AtomicInteger(0);
		driver.getReplicationDump(collectionName, null, null, null, null, TestComplexEntity01.class, new DumpHandler<TestComplexEntity01>() {
			public boolean handle(ReplicationDumpRecord<TestComplexEntity01> entity) {
				switch (entity.getType()) {
				case DOCUMENT_UPSERT:
				case EDGE_UPSERT:
					int x = upsertCount.getAndIncrement();
					assertThat(entity.getTick(), is(not(0L)));
					assertThat(entity.getKey(), is(not(nullValue())));
					assertThat(entity.getRev(), is(not(0L)));
					assertThat(entity.getData(), is(notNullValue()));
					assertThat(entity.getData().getDocumentKey(), is(notNullValue()));
					assertThat(entity.getData().getDocumentRevision(), is(not(0L)));
					assertThat(entity.getData().getDocumentHandle(), is(nullValue()));
					assertThat(entity.getData().getEntity().getAge(), is(not(0)));
					break;
				case DELETION:
					deleteCount.incrementAndGet();
					assertThat(entity.getTick(), is(not(0L)));
					assertThat(entity.getKey(), is(not(nullValue())));
					assertThat(entity.getRev(), is(not(0L)));
					assertThat(entity.getData(), is(nullValue()));
					break;
				}
				return true;
			}
		});
		
		assertThat(upsertCount.get(), is(11));
		assertThat(deleteCount.get(), is(10));
		
	}

	@Test
	public void test_get_dump_noticks() throws ArangoException {
		
		String collectionName = "rep_dump_test";
		
		try {
			driver.deleteCollection(collectionName);
		} catch (ArangoException e) {}
		try {
			driver.createCollection(collectionName);
		} catch (ArangoException e) {}

		// create 10 document
		for (int i = 0; i < 10; i++) {
			TestComplexEntity01 entity = new TestComplexEntity01("user-" + i, "desc-" + i, 20+i);
			driver.createDocument(collectionName, entity, true, null);
		}
		// truncate
		try {
			driver.truncateCollection(collectionName);
		} catch (ArangoException e) {}
		// create 1 document
		TestComplexEntity01 entity = new TestComplexEntity01("user-99", "desc-99", 99);
		driver.createDocument(collectionName, entity, true, null);
		
		final AtomicInteger upsertCount = new AtomicInteger(0);
		final AtomicInteger deleteCount = new AtomicInteger(0);
		driver.getReplicationDump(collectionName, null, null, null, false, TestComplexEntity01.class, new DumpHandler<TestComplexEntity01>() {
			public boolean handle(ReplicationDumpRecord<TestComplexEntity01> entity) {
				switch (entity.getType()) {
				case DOCUMENT_UPSERT:
				case EDGE_UPSERT:
					int x = upsertCount.getAndIncrement();
					assertThat(entity.getTick(), is((0L)));
					assertThat(entity.getKey(), is(not(nullValue())));
					assertThat(entity.getRev(), is(not(0L)));
					assertThat(entity.getData(), is(notNullValue()));
					assertThat(entity.getData().getDocumentKey(), is(notNullValue()));
					assertThat(entity.getData().getDocumentRevision(), is(not(0L)));
					assertThat(entity.getData().getDocumentHandle(), is(nullValue()));
					assertThat(entity.getData().getEntity().getAge(), is(not(0)));
					break;
				case DELETION:
					deleteCount.incrementAndGet();
					assertThat(entity.getTick(), is((0L)));
					assertThat(entity.getKey(), is(not(nullValue())));
					assertThat(entity.getRev(), is(not(0L)));
					assertThat(entity.getData(), is(nullValue()));
					break;
				}
				return true;
			}
		});
		
		assertThat(upsertCount.get(), is(11));
		assertThat(deleteCount.get(), is(10));
		
	}

	// TODO: Dump from-to
	
//	public void test_sync() throws ArangoException {
//		
//		driver.syncReplication(endpoint, database, username, password, restrictType, restrictCollections);
//		
//	}

	@Test
	public void test_server_id() throws ArangoException {
		
		String serverId = driver.getReplicationServerId();
		assertThat(serverId, is(notNullValue()));
		
	}
	
	@Test
	public void test_start_logger() throws ArangoException {
		
		boolean running = driver.startReplicationLogger();
		assertThat(running, is(true));

		boolean running2 = driver.startReplicationLogger();
		assertThat(running2, is(true));

	}

	@Test
	public void test_stop_logger() throws ArangoException {

		boolean running = driver.stopReplicationLogger();
		assertThat(running, is(false));

		boolean running2 = driver.stopReplicationLogger();
		assertThat(running2, is(false));

	}

}
