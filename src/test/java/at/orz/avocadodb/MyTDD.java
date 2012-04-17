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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;

import at.orz.avocadodb.AvocadoDriver.Mode;
import at.orz.avocadodb.entity.CollectionEntity;
import at.orz.avocadodb.entity.CollectionsEntity;
import at.orz.avocadodb.entity.DocumentEntity;
import at.orz.avocadodb.entity.Version;

/**
 * UnitTest for {@link AvocadoDriver}.
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class MyTDD {

	private static Logger logger = LoggerFactory.getLogger(MyTDD.class);
	
	private static AvocadoConfigure configure;
	private static AvocadoDriver client;
	
	@BeforeClass
	public static void setup() {
		configure = new AvocadoConfigure();
		client = new AvocadoDriver(configure);
	}
	
	@AfterClass
	public static void shutdown() {
		client.shutdown();
	}
	
	public static class TestComplexEntity {
		private String user = "testUser01";
		private String desc = "テストユーザーです。";
		private int age = 18;
		public TestComplexEntity() {
		}
		public TestComplexEntity(String user, String desc, int age) {
			this.user = user;
			this.desc = desc;
			this.age = age;
		}
	}
	
	@Test
	public void for_tdd() throws Exception {
		
//		CollectionEntity res1 = client.createCollection("unit_test_aaa");
//		//CollectionEntity res2 = client.getCollection("unit_test_aaa");
//		CollectionEntity res3 = client.getCollectionParameter("unit_test_aaa");
//		CollectionEntity res4 = client.getCollectionCount("unit_test_aaa");
//		CollectionEntity res5 = client.getCollectionFigures("unit_test_aaa");
//		CollectionsEntity res6 = client.getCollections();
//		
//		CollectionEntity res7 = client.loadCollection("unit_test_aaa");
//		CollectionEntity res8 = client.unloadCollection("unit_test_aaa");
//		CollectionEntity res9 = client.truncateCollection("unit_test_aaa");
//		
//		CollectionEntity res10 = client.setCollectionParameter("unit_test_aaa", false);
//		//client.getCollection("unit_test_aaa");
//
//		CollectionEntity res11 = client.deleteCollection("unit_test_bbb", null);
//
//		CollectionEntity res12 = client.renameCollection("unit_test_aaa", "unit_test_bbb");
//		client.getCollections();
//		client.deleteCollection("unit_test_bbb", null);
//		
//		DocumentEntity<Map<String, Object>> x = client.getDocument(
//				"tamtam", 2097000,  new TypeToken<Map<String, Object>>(){}.getType(), Mode.RETURN_NULL);
//		System.out.println(x.getDocumentRevision());
//		System.out.println(x.getDocumentHandle());
//		System.out.println(x.getEntity());
//		
	}
	
	
}
