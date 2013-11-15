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

package at.orz.arangodb.entity.marker;

import java.util.HashMap;

import at.orz.arangodb.entity.BaseEntity;
import at.orz.arangodb.entity.DocumentEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * @since 1.4.0
 */
public class MissingInstanceCreater {

	private static HashMap<Class<?>, Class<? extends BaseEntity>> mapping = new HashMap<Class<?>, Class<? extends BaseEntity>>();
	static {
		mapping.put(VertexEntity.class, DocumentEntity.class);
	}
	public static <T extends BaseEntity> Class<?> getMissingClass(Class<T> clazz) {
		System.out.println(clazz.getName());
		System.out.println(mapping.get(clazz));
		Class<T> c = (Class<T>) mapping.get(clazz);
		if (c == null) {
			c = clazz;
		}
		return c;
	}
	
}
