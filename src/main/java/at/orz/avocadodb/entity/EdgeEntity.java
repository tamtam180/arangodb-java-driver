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

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class EdgeEntity<T> extends BaseEntity {

	String edgeHandle;
	String fromHandle;
	String toHandle;
	long revision;
	
	T attributes;
	//long edgeId;
	//long fromId;
	//long toId;

	public String getEdgeHandle() {
		return edgeHandle;
	}

	public String getFromHandle() {
		return fromHandle;
	}

	public String getToHandle() {
		return toHandle;
	}

	public long getRevision() {
		return revision;
	}

	public T getAttributes() {
		return attributes;
	}

	public void setEdgeHandle(String edgeHandle) {
		this.edgeHandle = edgeHandle;
	}

	public void setFromHandle(String fromHandle) {
		this.fromHandle = fromHandle;
	}

	public void setToHandle(String toHandle) {
		this.toHandle = toHandle;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public void setAttributes(T attributes) {
		this.attributes = attributes;
	}
	
}
