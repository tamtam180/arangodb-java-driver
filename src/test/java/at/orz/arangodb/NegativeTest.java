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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoDriver;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.DefaultEntity;
import at.orz.arangodb.entity.DocumentEntity;
import at.orz.arangodb.entity.EntityFactory;
import at.orz.arangodb.http.HttpManager;
import at.orz.arangodb.http.HttpResponseEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class NegativeTest extends BaseTest {

	/**
	 * 開発途中にあった命令だけど、今は存在しない。
	 * きとんとエラーになること。
	 * @throws ArangoException
	 */
	@Test
	public void test_collections() throws ArangoException {
		
		HttpManager httpManager = new HttpManager();
		httpManager.init();
		
		// TODO Create configure of common test.
		HttpResponseEntity res = httpManager.doGet(
				"http://localhost:8529/_api/collections",
				null);
		
		DefaultEntity entity = EntityFactory.createEntity(res.getText(), DefaultEntity.class);
		assertThat(entity.isError(), is(true));
		assertThat(entity.getCode(), is(501));
		assertThat(entity.getErrorNumber(), is(9));
		
		httpManager.destroy();
		
	}
	
	public static class TestComplex {
		private String name;
	}
	
	@Test
	public void test_issue_35_and_41() throws Exception {
		
		ArangoConfigure configure = new ArangoConfigure();
		configure.init();
		ArangoDriver driver = new ArangoDriver(configure);
		
		TestComplex value = new TestComplex();
		value.name = "A\"A'@:///A";
		
		//String value = "AAA";
		DocumentEntity<?> doc = driver.createDocument("unit_test_issue35", value, true, true);
		String documentHandle = doc.getDocumentHandle();
		DocumentEntity<TestComplex> doc2 = driver.getDocument(documentHandle, TestComplex.class);
		
		configure.shutdown();
		
	}
	
	@Test
	public void test_primitive() throws Exception {
		
		ArangoConfigure configure = new ArangoConfigure();
		configure.init();
		ArangoDriver driver = new ArangoDriver(configure);
		
		String value = "AAA";
		DocumentEntity<?> doc = driver.createDocument("unit_test_issue35", value, true, true);
		String documentHandle = doc.getDocumentHandle();
		DocumentEntity<String> doc2 = driver.getDocument(documentHandle, String.class);
		
		configure.shutdown();
		
	}

	
}
