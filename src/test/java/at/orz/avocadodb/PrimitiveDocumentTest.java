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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.orz.avocadodb.AvocadoDriver.Mode;
import at.orz.avocadodb.entity.DocumentEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class PrimitiveDocumentTest extends BaseTest {
	
	@Test
	public void test_string() throws AvocadoException {
		
		String value = "AAA";
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = client.getDocument(documentHandle, String.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_string_quote() throws AvocadoException {
		
		String value = "AA\"A";
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = client.getDocument(documentHandle, String.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(value));
		
	}
	
	@Test
	public void test_string_multibyte1() throws AvocadoException {
		
		String value = "AA☆A";
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = client.getDocument(documentHandle, String.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_string_multibyte2() throws AvocadoException {
		
		TestComplexEntity01 value = new TestComplexEntity01("寿司", "", 10);
		System.out.println(new Gson().toJson(value));
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<TestComplexEntity01> doc = client.getDocument(documentHandle, TestComplexEntity01.class, Mode.RAISE_ERROR);
		System.out.println(doc.getEntity().getUser());
		System.out.println(doc.getEntity().getDesc());
		System.out.println(doc.getEntity().getAge());
	}

	@Test
	public void test_string_escape() throws AvocadoException {
		
		String value = "\\\\";
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = client.getDocument(documentHandle, String.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_string_spchar() throws AvocadoException {
		
		String value = "AA\t\nA;/@*:='&%$#!~\\";
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = client.getDocument(documentHandle, String.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(value));
		
	}
	
	@Test
	public void test_null() throws AvocadoException {
		
		String value = null;
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<String> doc = client.getDocument(documentHandle, String.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(nullValue()));
		
	}

	@Test
	public void test_boolean_true() throws AvocadoException {
		
		boolean value = true;
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<Boolean> doc = client.getDocument(documentHandle, boolean.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_boolean_false() throws AvocadoException {
		
		boolean value = false;
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<Boolean> doc = client.getDocument(documentHandle, boolean.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_number_int() throws AvocadoException {
		
		int value = 1000000;
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<Integer> doc = client.getDocument(documentHandle, int.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	public void test_number_long() throws AvocadoException {
		
		long value = Long.MAX_VALUE;
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<Long> doc = client.getDocument(documentHandle, long.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(value));
		
	}

	@Test
	@Ignore
	public void test_number_double() throws AvocadoException {
		
		double value = Double.MAX_VALUE;
		
		DocumentEntity<?> res = client.createDocument("unit_test_primitive", value, true, true, Mode.RAISE_ERROR);
		String documentHandle = res.getDocumentHandle();
		
		DocumentEntity<Double> doc = client.getDocument(documentHandle, double.class, Mode.RAISE_ERROR);
		assertThat(doc.getEntity(), is(value));
		
	}

}
