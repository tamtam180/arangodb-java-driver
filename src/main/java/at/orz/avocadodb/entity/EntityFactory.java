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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.orz.avocadodb.entity.CollectionEntity.Figures;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class EntityFactory {

	private static Gson gson;
	static {
		gson = new GsonBuilder()
			.registerTypeAdapter(CollectionStatus.class, new CollectionStatusTypeAdapter())
			.registerTypeAdapter(CollectionEntity.class, new EntityDeserializers.CollectionEntityDeserializer())
			.registerTypeAdapter(DocumentEntity.class, new EntityDeserializers.DocumentEntityDeserializer())
			.registerTypeAdapter(DocumentsEntity.class, new EntityDeserializers.DocumentsEntityDeserializer())
			.registerTypeAdapter(Version.class, new EntityDeserializers.VersionDeserializer())
			.registerTypeAdapter(DefaultEntity.class, new EntityDeserializers.DefaultEntityDeserializer())
			.registerTypeAdapter(Figures.class, new EntityDeserializers.FiguresDeserializer())
			.registerTypeAdapter(CursorEntity.class, new EntityDeserializers.CursorEntityDeserializer())
			.registerTypeAdapter(IndexEntity.class, new EntityDeserializers.IndexEntityDeserializer())
			.registerTypeAdapter(IndexesEntity.class, new EntityDeserializers.IndexesEntityDeserializer())
			.registerTypeAdapter(EdgeEntity.class, new EntityDeserializers.EdgeEntityDeserializer())
			.registerTypeAdapter(EdgesEntity.class, new EntityDeserializers.EdgesEntityDeserializer())
			.registerTypeAdapter(AdminLogEntity.class, new EntityDeserializers.AdminLogEntryEntityDeserializer())
			.create();
	}
	
	public static <T> CursorEntity<T> createResult(CursorEntity<T> entity, Class<T> clazz) {
		if (entity._array == null) {
			entity.results = Collections.emptyList();
		} else if (entity._array.isJsonNull() || entity._array.size() == 0) {
			entity.results = Collections.emptyList();
			entity._array = null;
		} else {
			ArrayList<T> list = new ArrayList<T>(entity._array.size());
			for (JsonElement elem : entity._array) {
				list.add(gson.fromJson(elem, clazz));
			}
			entity.results = list;
			entity._array = null;
		}
		return entity;
	}
	
	public static <T> T createEntity(String jsonText, Type type) {
		return gson.fromJson(jsonText, type);
	}
	
	public static <T> String toJsonString(T obj) {
		return gson.toJson(obj);
	}
	
	public static <T> EdgesEntity<T> createEdges(String jsonText, Class<T> clazz) {
		EdgesEntity<T> edges = createEntity(jsonText, EdgesEntity.class);
		edges.edges = createEdges(edges._edges, clazz);
		edges._edges = null;
		return edges;
	}
	private static <T> List<EdgeEntity<T>> createEdges(JsonArray array, Class<T> clazz) {
		
		if (array == null) {
			return null;
		}
		
		ArrayList<EdgeEntity<T>> edges = new ArrayList<EdgeEntity<T>>(array.size());
		for (JsonElement elem: array) {
			EdgeEntity<T> edge = gson.fromJson(elem, EdgeEntity.class);
			if (clazz != null) {
				edge.attributes = gson.fromJson(elem, clazz);
			}
			edges.add(edge);
		}
		
		return edges;
	}
	
}
