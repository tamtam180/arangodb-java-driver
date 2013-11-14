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

import java.util.Collections;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import at.orz.arangodb.entity.GraphEntity;
import at.orz.arangodb.entity.GraphsEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverGraphTest extends BaseTest {

	public ArangoDriverGraphTest(ArangoConfigure configure, ArangoDriver driver) {
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
	public void test_create_graph() throws ArangoException {

		// create
		GraphEntity entity1 = driver.createGraph("g1", "vcol1", "ecol1", true);
		assertThat(entity1.getCode(), is(201));
		assertThat(entity1.getDocumentRevision(), is(not(0L)));
		assertThat(entity1.getDocumentHandle(), is("_graphs/g1"));
		assertThat(entity1.getDocumentKey(), is("g1"));
		assertThat(entity1.getVertices(), is("vcol1"));
		assertThat(entity1.getEdges(), is("ecol1"));
		
	}
	
	@Test
	public void test_create_graph_202() throws ArangoException {

		// in 1.4.0 manual
		// 202 is returned if the graph was created successfully and waitForSync was false.

		GraphEntity entity = driver.createGraph("g1", "vcol1", "ecol1", false);
		assertThat(entity.getCode(), is(202));
		assertThat(entity.getDocumentRevision(), is(not(0L)));
		assertThat(entity.getDocumentHandle(), is("_graphs/g1"));
		assertThat(entity.getDocumentKey(), is("g1"));
		assertThat(entity.getVertices(), is("vcol1"));
		assertThat(entity.getEdges(), is("ecol1"));
		
	}

	@Test
	public void test_create_graph_dup() throws ArangoException {

		GraphEntity entity = driver.createGraph("g1", "vcol1", "ecol1", true);
		assertThat(entity.getCode(), is(201));
		assertThat(entity.getDocumentRevision(), is(not(0L)));
		assertThat(entity.getDocumentHandle(), is("_graphs/g1"));
		assertThat(entity.getDocumentKey(), is("g1"));
		assertThat(entity.getVertices(), is("vcol1"));
		assertThat(entity.getEdges(), is("ecol1"));

		try {
			driver.createGraph("g1", "vcol1", "ecol1", false);
			fail();
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(400));
			assertThat(e.getErrorNumber(), is(1902)); // graph with the name already exists
		}

	}

	// TODO: errorNum: 1902 : "found graph but has different <name>"

	@Test
	public void get_graphs() throws ArangoException {
		
		driver.createGraph("g1", "v1", "e1", null);
		driver.createGraph("g2", "v2", "e2", null);
		driver.createGraph("g3", "v3", "e3", null);
		
		GraphsEntity graphs = driver.getGraphs();
		assertThat(graphs.isError(), is(false));
		assertThat(graphs.getCode(), is(200));
		assertThat(graphs.getGraphs().size(), is(3));
		
		Collections.sort(graphs.getGraphs(), new Comparator<GraphEntity>() {
			public int compare(GraphEntity o1, GraphEntity o2) {
				return o1.getDocumentKey().compareTo(o2.getDocumentKey());
			}
		});
		
		GraphEntity g = graphs.getGraphs().get(0);
		assertThat(g.getDocumentRevision(), is(not(0L)));
		assertThat(g.getDocumentHandle(), is("_graphs/g1"));
		assertThat(g.getDocumentKey(), is("g1"));
		assertThat(g.getVertices(), is("v1"));
		assertThat(g.getEdges(), is("e1"));

		g = graphs.getGraphs().get(1);
		assertThat(g.getDocumentRevision(), is(not(0L)));
		assertThat(g.getDocumentHandle(), is("_graphs/g2"));
		assertThat(g.getDocumentKey(), is("g2"));
		assertThat(g.getVertices(), is("v2"));
		assertThat(g.getEdges(), is("e2"));

		g = graphs.getGraphs().get(2);
		assertThat(g.getDocumentRevision(), is(not(0L)));
		assertThat(g.getDocumentHandle(), is("_graphs/g3"));
		assertThat(g.getDocumentKey(), is("g3"));
		assertThat(g.getVertices(), is("v3"));
		assertThat(g.getEdges(), is("e3"));

		
	}
	

}
