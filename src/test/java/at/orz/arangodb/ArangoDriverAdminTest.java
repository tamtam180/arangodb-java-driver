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

import org.junit.Test;

import at.orz.arangodb.entity.AdminLogEntity;
import at.orz.arangodb.entity.ArangoUnixTime;
import at.orz.arangodb.entity.ArangoVersion;
import at.orz.arangodb.entity.DefaultEntity;
import at.orz.arangodb.entity.StatisticsDescriptionEntity;
import at.orz.arangodb.entity.StatisticsEntity;

import com.google.gson.Gson;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverAdminTest extends BaseTest {

	public ArangoDriverAdminTest(ArangoConfigure configure, ArangoDriver driver) {
		super(configure, driver);
	}

	@Test
	public void test_version() throws ArangoException {
		
		ArangoVersion version = driver.getVersion();
		assertThat(version.getServer(), is("arango"));
		assertThat(version.getVersion(), is("1.4.3"));
		
	}
	
	@Test
	public void test_time() throws ArangoException {
		
		ArangoUnixTime time = driver.getTime();
		assertThat(time.getSecond(), is(not(0)));
		assertThat(time.getMicrosecond(), is(not(0)));
		
		System.out.println("unixtime=" + time.getSecond());
		System.out.println("unixtime_micros=" + time.getMicrosecond());
		System.out.println("unixtime_millis=" + time.getTimeMillis());

	}
	
	@Test
	public void test_log_all() throws ArangoException {
		
		AdminLogEntity entity = driver.getServerLog(
				null, null, null, null, null, null, null);
		
		assertThat(entity, is(notNullValue()));
		assertThat(entity.getTotalAmount(), is(not(0)));
		assertThat(entity.getLogs().size(), is(entity.getTotalAmount()));
		
		// debug
		for (AdminLogEntity.LogEntry log : entity.getLogs()) {
			System.out.printf("%d\t%d\t%tF %<tT\t%s%n", log.getLid(), log.getLevel(), log.getTimestamp(), log.getText());
		}
		
	}

	@Test
	public void test_log_text() throws ArangoException {
		
		AdminLogEntity entity = driver.getServerLog(
				null, null, null, null, null, null, "Fun");
		
		assertThat(entity, is(notNullValue()));
		// debug
		for (AdminLogEntity.LogEntry log : entity.getLogs()) {
			System.out.printf("%d\t%d\t%tF %<tT\t%s%n", log.getLid(), log.getLevel(), log.getTimestamp(), log.getText());
		}
		
	}

	// TODO テスト増やす
	
	@Test
	public void test_statistics() throws ArangoException {
		
		StatisticsEntity stat = driver.getStatistics();
		
		// debug
		Gson gson = new Gson();
		System.out.println(gson.toJson(stat));
		System.out.println(gson.toJson(stat.getSystem()));
		System.out.println(gson.toJson(stat.getClient()));
		System.out.println(gson.toJson(stat.getServer()));

		// TODO: assert null

	}

	@Test
	public void test_statistics_description() throws ArangoException {

		StatisticsDescriptionEntity desc = driver.getStatisticsDescription();

		// debug
		Gson gson = new Gson();
		System.out.println(gson.toJson(desc));
		System.out.println(gson.toJson(desc.getGroups()));
		System.out.println(gson.toJson(desc.getFigures()));
	
		// TODO: assert null
		
	}
	
	@Test
	public void test_flush_modules() throws ArangoException {
		
		DefaultEntity entity = driver.flushModules();
		assertThat(entity.getStatusCode(), is(200));
		assertThat(entity.isError(), is(false));
		
	}

	@Test
	public void test_reload_routing() throws ArangoException {
		
		DefaultEntity entity = driver.reloadRouting();
		assertThat(entity.getStatusCode(), is(200));
		assertThat(entity.isError(), is(false));
		
	}

	@Test
	public void test_execute_do_nothing() throws ArangoException {
		
		DefaultEntity entity = driver.executeScript("");
		assertThat(entity.isError(), is(false));
		assertThat(entity.getCode(), is(200));
		assertThat(entity.getStatusCode(), is(200));
		
	}

	@Test
	public void test_execute() throws ArangoException {
		
		DefaultEntity entity = driver.executeScript(
				"cols = db._collections();\n" +
				"len = cols.length;\n"
				);
		assertThat(entity.isError(), is(false));
		assertThat(entity.getCode(), is(200));
		assertThat(entity.getStatusCode(), is(200));
		
	}

	@Test
	public void test_execute_delete_collection() throws ArangoException {
		
		DefaultEntity entity1 = driver.executeScript("db._drop(\"" + "col-execute-delete-test" + "\")");
		assertThat(entity1.isError(), is(false));
		assertThat(entity1.getCode(), is(200));
		assertThat(entity1.getStatusCode(), is(200));
		
		driver.createCollection("col-execute-delete-test");
		driver.getCollection("col-execute-delete-test");

		DefaultEntity entity2 = driver.executeScript("db._drop(\"" + "col-execute-delete-test" + "\")");
		assertThat(entity2.isError(), is(false));
		assertThat(entity2.getCode(), is(200));
		assertThat(entity2.getStatusCode(), is(200));
		
		try {
			driver.getCollection("col-execute-delete-test");
			fail();
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(404));
			assertThat(e.getErrorNumber(), is(1203));
		}
	}

	@Test
	public void test_execute_error() throws ArangoException {
		try {
			driver.executeScript("xxx");
			fail();
		} catch (ArangoException e) {
			String t = 
				"JavaScript exception in file 'undefined' at 1,14: ReferenceError: xxx is not defined\n" +
				"!(function() {xxx}());\n" +
				"!             ^\n" +
				"stacktrace: ReferenceError: xxx is not defined\n";
			assertThat(e.getErrorMessage(), startsWith(t));
			assertThat(e.getEntity().getStatusCode(), is(500));
		}
		
	}


}
