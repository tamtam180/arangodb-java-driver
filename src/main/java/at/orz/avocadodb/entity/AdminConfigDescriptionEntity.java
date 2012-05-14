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

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AdminConfigDescriptionEntity extends BaseMapEntity<String, AdminConfigDescriptionEntity.DescriptionEntry> {
	
	public static class DescriptionEntry implements Serializable {
		String name;
		boolean readonly;
		String type;
		Object[] values;
		public String getName() {
			return name;
		}
		public boolean isReadonly() {
			return readonly;
		}
		public String getType() {
			return type;
		}
		public Object[] getValues() {
			return values;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setReadonly(boolean readonly) {
			this.readonly = readonly;
		}
		public void setType(String type) {
			this.type = type;
		}
		public void setValues(Object[] values) {
			this.values = values;
		}
	}
	
}
