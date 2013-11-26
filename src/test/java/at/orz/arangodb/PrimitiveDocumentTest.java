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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;

import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.DocumentEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class PrimitiveDocumentTest extends BaseTest {
	
	public PrimitiveDocumentTest(ArangoConfigure configure, ArangoDriver driver) {
		super(configure, driver);
	}

	@Test
	public void test_string() throws ArangoException {
		
		String value = "AAA";
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = driver.getDocument(documentHandle, String.class);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_string_quote() throws ArangoException {
		
		String value = "AA\"A";
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = driver.getDocument(documentHandle, String.class);
		assertThat(doc.getEntity(), is(value));
		
	}
	
	@Test
	public void test_string_multibyte1() throws ArangoException {
		
		String value = "AA☆A";
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = driver.getDocument(documentHandle, String.class);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_string_multibyte2() throws ArangoException {
		
		TestComplexEntity01 value = new TestComplexEntity01("寿司", "", 10);
		System.out.println(new Gson().toJson(value));
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<TestComplexEntity01> doc = driver.getDocument(documentHandle, TestComplexEntity01.class);
		System.out.println(doc.getEntity().getUser());
		System.out.println(doc.getEntity().getDesc());
		System.out.println(doc.getEntity().getAge());
	}

	@Test
	public void test_string_escape() throws ArangoException {
		
		String value = "\\\\";
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = driver.getDocument(documentHandle, String.class);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_string_spchar() throws ArangoException {
		
		String value = "AA\t\nA;/@*:='&%$#!~\\";
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = driver.getDocument(documentHandle, String.class);
		assertThat(doc.getEntity(), is(value));
		
	}
	
	@Test
	public void test_null() throws ArangoException {
		
		String value = null;
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = driver.getDocument(documentHandle, String.class);
		assertThat(doc.getEntity(), is(nullValue()));
		
	}

	@Test
	public void test_boolean_true() throws ArangoException {
		
		boolean value = true;
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<Boolean> doc = driver.getDocument(documentHandle, boolean.class);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_boolean_false() throws ArangoException {
		
		boolean value = false;
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<Boolean> doc = driver.getDocument(documentHandle, boolean.class);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_number_int() throws ArangoException {
		
		int value = 1000000;
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<Integer> doc = driver.getDocument(documentHandle, int.class);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_number_long() throws ArangoException {
		
		long value = Long.MAX_VALUE;
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<Long> doc = driver.getDocument(documentHandle, long.class);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	@Ignore
	public void test_number_double() throws ArangoException {
		
		double value = Double.MAX_VALUE;
		
		DocumentEntity<?> res = driver.createDocument("unit_test_primitive", value, true, true);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<Double> doc = driver.getDocument(documentHandle, double.class);
		assertThat(doc.getEntity(), is(value));
		
	}

}
