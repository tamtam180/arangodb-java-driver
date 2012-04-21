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

package at.orz.avocadodb.entity;

import java.util.Iterator;
import java.util.List;

import at.orz.avocadodb.util.CollectionUtils;

import com.google.gson.JsonArray;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class EdgesEntity<T> extends BaseEntity implements Iterable<EdgeEntity<T>> {
	
	List<EdgeEntity<T>> edges;
	transient JsonArray _edges;

	public Iterator<EdgeEntity<T>> iterator() {
		return CollectionUtils.safetyIterator(edges);
	}
	
	public int size() {
		return (edges == null) ? 0 : edges.size();
	}
	
	public EdgeEntity<T> get(int index) {
		return edges.get(index);
	}

	public List<EdgeEntity<T>> getEdges() {
		return edges;
	}
	public void setEdges(List<EdgeEntity<T>> edges) {
		this.edges = edges;
	}
	
}
