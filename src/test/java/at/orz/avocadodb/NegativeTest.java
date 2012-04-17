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
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import at.orz.avocadodb.AvocadoDriver.Mode;
import at.orz.avocadodb.entity.DefaultEntity;
import at.orz.avocadodb.entity.DocumentEntity;
import at.orz.avocadodb.entity.EntityFactory;
import at.orz.avocadodb.http.HttpManager;
import at.orz.avocadodb.http.HttpResponseEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class NegativeTest extends BaseTest {

	/**
	 * 開発途中にあった命令だけど、今は存在しない。
	 * きとんとエラーになること。
	 * @throws AvocadoException
	 */
	@Test
	public void test_collections() throws AvocadoException {
		
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
		
		AvocadoConfigure configure = new AvocadoConfigure();
		AvocadoDriver driver = new AvocadoDriver(configure);
		
		TestComplex value = new TestComplex();
		value.name = "A\"A'@:///A";
		
		//String value = "AAA";
		DocumentEntity<?> doc = driver.createDocument("unit_test_issue35", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = doc.getDocumentHandle();
		DocumentEntity<TestComplex> doc2 = driver.getDocument(documentHandle, TestComplex.class, Mode.RAISE_ERROR);
		
		driver.shutdown();
		
	}
	
	@Test
	public void test_primitive() throws Exception {
		
		AvocadoConfigure configure = new AvocadoConfigure();
		AvocadoDriver driver = new AvocadoDriver(configure);
		
		String value = "AAA";
		DocumentEntity<?> doc = driver.createDocument("unit_test_issue35", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = doc.getDocumentHandle();
		DocumentEntity<String> doc2 = driver.getDocument(documentHandle, String.class, Mode.RAISE_ERROR);
		
		driver.shutdown();
		
	}

	
}
