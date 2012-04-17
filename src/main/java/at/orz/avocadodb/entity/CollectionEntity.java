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

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;


/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class CollectionEntity extends BaseEntity {
	
	String name;
	
	long id;
	
	CollectionStatus status;
	
	Boolean waitForSync;
	
	long journalSize;
	
	long count;
	
	Figures figures;
	
	public String getName() {
		return name;
	}
	public long getId() {
		return id;
	}
	public CollectionStatus getStatus() {
		return status;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setStatus(CollectionStatus status) {
		this.status = status;
	}
	public Boolean getWaitForSync() {
		return waitForSync;
	}
	public long getJournalSize() {
		return journalSize;
	}
	public long getCount() {
		return count;
	}
	public Figures getFigures() {
		return figures;
	}
	public void setWaitForSync(Boolean waitForSync) {
		this.waitForSync = waitForSync;
	}
	public void setJournalSize(long journalSize) {
		this.journalSize = journalSize;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public void setFigures(Figures figures) {
		this.figures = figures;
	}
	

	public static class Figures implements Serializable {
		
		long aliveCount;
		long aliveSize;
		long deadCount;
		long deadSize;
		long datafileCount;
		public long getAliveCount() {
			return aliveCount;
		}
		public long getAliveSize() {
			return aliveSize;
		}
		public long getDeadCount() {
			return deadCount;
		}
		public long getDeadSize() {
			return deadSize;
		}
		public long getDatafileCount() {
			return datafileCount;
		}
		public void setAliveCount(long aliveCount) {
			this.aliveCount = aliveCount;
		}
		public void setAliveSize(long aliveSize) {
			this.aliveSize = aliveSize;
		}
		public void setDeadCount(long deadCount) {
			this.deadCount = deadCount;
		}
		public void setDeadSize(long deadSize) {
			this.deadSize = deadSize;
		}
		public void setDatafileCount(long datafileCount) {
			this.datafileCount = datafileCount;
		}
		
	}
	
}
