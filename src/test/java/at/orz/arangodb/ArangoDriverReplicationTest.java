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

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.orz.arangodb.entity.EntityFactory;
import at.orz.arangodb.entity.ReplicationInventoryEntity;
import at.orz.arangodb.entity.ReplicationInventoryEntity.Collection;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverReplicationTest extends BaseTest {

	public ArangoDriverReplicationTest(ArangoConfigure configure, ArangoDriver driver) {
		super(configure, driver);
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

}
