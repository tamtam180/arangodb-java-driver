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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.orz.arangodb.entity.BooleanResultEntity;
import at.orz.arangodb.entity.CollectionEntity;
import at.orz.arangodb.entity.DocumentEntity;
import at.orz.arangodb.entity.ReplicationSyncEntity;
import at.orz.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverReplicationTestScenario1 {

	ArangoConfigure masterConfigure;
	ArangoConfigure slaveConfigure;
	ArangoDriver masterDriver;
	ArangoDriver slaveDriver;
	
	String database = "repl_scenario_test1";
	String collectionName1 = "col1";
	String collectionName2 = "col2";
	
	@Before
	public void before() throws ArangoException {

		masterConfigure = new ArangoConfigure("/arangodb.properties");
		masterConfigure.init();
		masterDriver = new ArangoDriver(masterConfigure);

		slaveConfigure = new ArangoConfigure("/arangodb-slave.properties");
		slaveConfigure.init();
		slaveDriver = new ArangoDriver(slaveConfigure);
		
		// turn off replication logger at master
		masterDriver.stopReplicationLogger();
		masterDriver.stopReplicationApplier();
		
		// turn off replication applier at slave
		slaveDriver.stopReplicationLogger();
		slaveDriver.stopReplicationApplier();
		
	}
	
	@After
	public void after() throws ArangoException {
		masterConfigure.shutdown();
		slaveConfigure.shutdown();
	}
	
	@Test
	public void test_scienario() throws ArangoException, InterruptedException {
		
		// replication: master[db=repl_scenario_test1] -> slave[db=_system]
		
		System.out.println("----------------------------------------");
		
		// create database in master
		try {
			masterDriver.deleteDatabase(database);
		} catch (ArangoException e) {}
		{
			BooleanResultEntity result = masterDriver.createDatabase(database);
			assertThat(result.getResult(), is(true));
		} 
		// configure database
		masterDriver.setDefaultDatabase(database);
		slaveDriver.setDefaultDatabase(null);
		
		try {
			slaveDriver.deleteCollection(collectionName1);
		} catch (ArangoException e) {}
		
		// [Master] add document
		CollectionEntity col1 = masterDriver.createCollection(collectionName1);
		DocumentEntity<Map> doc1 = masterDriver.createDocument(collectionName1, new MapBuilder().put("my-key1", "100").get(), false, false);
		DocumentEntity<Map> doc2 = masterDriver.createDocument(collectionName1, new MapBuilder().put("my-key2", "255").get(), false, false);
		DocumentEntity<Map> doc3 = masterDriver.createDocument(collectionName1, new MapBuilder().put("my-key3", 1234567).get(), false, false);
		
		// [Master] logger property
		masterDriver.setReplicationLoggerConfig(true, null, 1048576L, 0L);
		
		// [Master] turn on replication logger
		masterDriver.startReplicationLogger();
		
		// [Master] get logger state
		//masterDriver.getReplicationLoggerState();

		// [Slave] turn off replication applier
		slaveDriver.stopReplicationApplier();
		
		// [Slave] sync
		ReplicationSyncEntity syncResult = slaveDriver.syncReplication(masterConfigure.getEndpoint(), database, null, null, null, null);
		System.out.println(syncResult.getLastLogTick());
		
		Thread.sleep(3000L);

		slaveDriver.setReplicationApplierConfig(
				masterConfigure.getEndpoint(), database, null, null, 
				null, null, null, null, true, true);
		
		// [Slave] turn on replication applier
		slaveDriver.startReplicationApplier(syncResult.getLastLogTick());

		for (int i = 0; i < 300; i++) {
			masterDriver.createDocument(collectionName1, new MapBuilder().put("my-key" + i, 1234567).get(), false, false);
		}
		
		// wait
		TimeUnit.SECONDS.sleep(3);
		
		// [Slave] check a replication data
		CollectionEntity entity = slaveDriver.getCollectionCount(collectionName1);
		assertThat(entity.getCount(), is(303L));
		
		
	}
	
}
