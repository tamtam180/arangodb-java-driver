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

package at.orz.avocadodb.example;

import java.util.ArrayList;

import at.orz.avocadodb.AvocadoConfigure;
import at.orz.avocadodb.AvocadoDriver;
import at.orz.avocadodb.AvocadoException;
import at.orz.avocadodb.entity.Direction;
import at.orz.avocadodb.entity.DocumentEntity;
import at.orz.avocadodb.entity.EdgeEntity;
import at.orz.avocadodb.entity.EdgesEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class Example2 {
	
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
	
	public static void main(String[] args) {

		AvocadoConfigure configure = new AvocadoConfigure();
		AvocadoDriver driver = new AvocadoDriver(configure);
		
		final String collectionName = "example";
		try {
			
			// CreateVertex
			ArrayList<DocumentEntity<TestVertex>> docs = new ArrayList<DocumentEntity<TestVertex>>();
			for (int i = 0; i < 10; i++) {
				TestVertex value = new TestVertex();
				value.name = "vvv" + i;
				DocumentEntity<TestVertex> doc = driver.createDocument(collectionName, value, true, false, null);
				docs.add(doc);
			}
			
			// 0 -> 1
			// 0 -> 2
			// 2 -> 3
			
			EdgeEntity<TestEdgeAttribute> edge1 = driver.createEdge(
					collectionName, docs.get(0).getDocumentHandle(), docs.get(1).getDocumentHandle(), 
					new TestEdgeAttribute("edge1", 100));

			EdgeEntity<TestEdgeAttribute> edge2 = driver.createEdge(
					collectionName, docs.get(0).getDocumentHandle(), docs.get(2).getDocumentHandle(), 
					new TestEdgeAttribute("edge2", 200));

			EdgeEntity<TestEdgeAttribute> edge3 = driver.createEdge(
					collectionName, docs.get(2).getDocumentHandle(), docs.get(3).getDocumentHandle(), 
					new TestEdgeAttribute("edge3", 300));
			
			EdgesEntity<TestEdgeAttribute> edges = driver.getEdges(collectionName, docs.get(0).getDocumentHandle(), Direction.ANY, TestEdgeAttribute.class);
			System.out.println(edges.size());
			System.out.println(edges.get(0).getAttributes().a);
			System.out.println(edges.get(1).getAttributes().a);
			
		} catch (AvocadoException e) {
			e.printStackTrace();
		} finally {
			driver.shutdown();
		}
		
	}

}
