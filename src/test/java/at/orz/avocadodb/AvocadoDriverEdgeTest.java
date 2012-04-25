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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import at.orz.avocadodb.entity.Direction;
import at.orz.avocadodb.entity.DocumentEntity;
import at.orz.avocadodb.entity.EdgeEntity;
import at.orz.avocadodb.entity.EdgesEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AvocadoDriverEdgeTest extends BaseTest {

	public static class TestEdgeAttribute {
		public String a;
		public int b;
		public TestEdgeAttribute(){}
		public TestEdgeAttribute(String a, int b) {
			this.a = a;
			this.b = b;
		}
	}
	public static class TestVertex {
		public String name;
	}
	
	@Test
	public void test1() throws AvocadoException {
		
		ArrayList<DocumentEntity<TestVertex>> docs = new ArrayList<DocumentEntity<TestVertex>>();
		for (int i = 0; i < 10; i++) {
			TestVertex value = new TestVertex();
			value.name = "vvv" + i;
			DocumentEntity<TestVertex> doc = client.createDocument("unit_test", value, true, false, null);
			docs.add(doc);
		}
		
		// 0 -> 1
		// 0 -> 2
		// 2 -> 3
		
		EdgeEntity<TestEdgeAttribute> edge1 = client.createEdge(
				"unit_test", docs.get(0).getDocumentHandle(), docs.get(1).getDocumentHandle(), 
				new TestEdgeAttribute("edge1", 100));
		assertThat(edge1.isError(), is(false));
		assertThat(edge1.getEdgeHandle(), is(notNullValue()));
		assertThat(edge1.getRevision(), is(not(0L)));

		EdgeEntity<TestEdgeAttribute> edge2 = client.createEdge(
				"unit_test", docs.get(0).getDocumentHandle(), docs.get(2).getDocumentHandle(), 
				new TestEdgeAttribute("edge2", 200));
		assertThat(edge2.isError(), is(false));
		assertThat(edge2.getEdgeHandle(), is(notNullValue()));
		assertThat(edge2.getRevision(), is(not(0L)));

		EdgeEntity<TestEdgeAttribute> edge3 = client.createEdge(
				"unit_test", docs.get(2).getDocumentHandle(), docs.get(3).getDocumentHandle(), 
				new TestEdgeAttribute("edge3", 300));
		assertThat(edge3.isError(), is(false));
		assertThat(edge3.getEdgeHandle(), is(notNullValue()));
		assertThat(edge3.getRevision(), is(not(0L)));

		EdgeEntity<TestEdgeAttribute> edge1ex = client.getEdge(edge1.getEdgeHandle(), TestEdgeAttribute.class);
		assertThat(edge1ex.isError(), is(false));
		assertThat(edge1ex.getEdgeHandle(), is(notNullValue()));
		assertThat(edge1ex.getRevision(), is(not(0L)));
		assertThat(edge1ex.getAttributes().a, is("edge1"));
		assertThat(edge1ex.getAttributes().b, is(100));
		
		
		EdgesEntity<TestEdgeAttribute> edges = client.getEdges("unit_test", docs.get(0).getDocumentHandle(), Direction.ANY, TestEdgeAttribute.class);
		assertThat(edges.size(), is(2));
		assertThat(edges.get(0).getEdgeHandle(), is(edge1.getEdgeHandle()));
		assertThat(edges.get(0).getRevision(), is(edge1.getRevision()));
		assertThat(edges.get(0).getFromHandle(), is(docs.get(0).getDocumentHandle()));
		assertThat(edges.get(0).getToHandle(), is(docs.get(1).getDocumentHandle()));
		assertThat(edges.get(0).getAttributes().a, is("edge1"));
		assertThat(edges.get(0).getAttributes().b, is(100));

		assertThat(edges.get(1).getEdgeHandle(), is(edge2.getEdgeHandle()));
		assertThat(edges.get(1).getRevision(), is(edge2.getRevision()));
		assertThat(edges.get(1).getFromHandle(), is(docs.get(0).getDocumentHandle()));
		assertThat(edges.get(1).getToHandle(), is(docs.get(2).getDocumentHandle()));
		assertThat(edges.get(1).getAttributes().a, is("edge2"));
		assertThat(edges.get(1).getAttributes().b, is(200));
		
		
		// delete edge2
		EdgeEntity<?> ret1 = client.deleteEdge("unit_test", edge2.getEdgeHandle());
		assertThat(ret1.isError(), is(false));
		assertThat(ret1.getEdgeHandle(), is(edge2.getEdgeHandle()));
		
		EdgesEntity<TestEdgeAttribute> edges2 = client.getEdges("unit_test", docs.get(0).getDocumentHandle(), Direction.ANY, TestEdgeAttribute.class);
		assertThat(edges2.size(), is(1));
		assertThat(edges2.get(0).getAttributes().a, is("edge1"));
		
		// head
		long etag = client.checkEdge(edge1.getEdgeHandle());
		assertThat(etag, is(not(0L)));
		
	}

	// TODO テストを分離する
	// TODO UpdateEdgeのテストを追加する
	
}
