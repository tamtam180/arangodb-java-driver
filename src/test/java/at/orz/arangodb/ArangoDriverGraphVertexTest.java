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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import at.orz.arangodb.entity.DocumentEntity;
import at.orz.arangodb.entity.GraphEntity;
import at.orz.arangodb.entity.VertexEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverGraphVertexTest extends BaseTest {

	public ArangoDriverGraphVertexTest(ArangoConfigure configure, ArangoDriver driver) {
		super(configure, driver);
	}
	
	@Before
	public void before() throws ArangoException {

		String deleteAllCollectionAndGraphCode = 
				"var Graph = require('org/arangodb/graph').Graph;\n" +
				"Graph.getAll().forEach(function(g){\n" +
				"  new Graph(g._key).drop();\n" +
				"});\n" +
				"db._collections().forEach(function(col){\n" +
				"  var name = col.name();\n" +
				"  if (name.indexOf('_') != 0) col.drop();\n" +
				"});\n"
				;
		driver.executeScript(deleteAllCollectionAndGraphCode);

	}

	@Test
	public void test_create_vertex() throws ArangoException {
		
		GraphEntity g1 = driver.createGraph("g1","v1", "e1", null);
		
		VertexEntity<TestComplexEntity01> v = driver.createVertex("g1", new TestComplexEntity01("xxx", "yyy", 10), null);
		DocumentEntity<TestComplexEntity01> vertex = v.getVertex();
		assertThat(vertex.getDocumentHandle(), is(notNullValue()));
		assertThat(vertex.getDocumentRevision(), is(not(0L)));
		assertThat(vertex.getDocumentKey(), is(notNullValue()));
		assertThat(vertex.getEntity(), isA(TestComplexEntity01.class));
		assertThat(vertex.getEntity().getUser(), is("xxx"));
		assertThat(vertex.getEntity().getDesc(), is("yyy"));
		assertThat(vertex.getEntity().getAge(), is(10));
		
	}

	// TODO: create with _key
	// TODO: create with _key and duplication error
	

}
