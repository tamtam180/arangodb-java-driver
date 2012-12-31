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

package at.orz.arangodb.entity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import at.orz.arangodb.entity.AdminConfigDescriptionEntity.DescriptionEntry;
import at.orz.arangodb.entity.CollectionEntity.Figures;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class EntityDeserializers {
	
	private static <T extends BaseEntity> T deserializeBaseParameter(JsonObject obj, T entity) {
		
		if (obj.has("error")) {
			entity.error = obj.getAsJsonPrimitive("error").getAsBoolean();
		}
		if (obj.has("code")) {
			entity.code = obj.getAsJsonPrimitive("code").getAsInt();
		}
		if (obj.has("errorNum")) {
			entity.errorNumber = obj.getAsJsonPrimitive("errorNum").getAsInt();
		}
		if (obj.has("errorMessage")) {
			entity.errorMessage = obj.getAsJsonPrimitive("errorMessage").getAsString();
		}
		if (obj.has("etag")) {
			entity.etag = obj.getAsJsonPrimitive("etag").getAsLong();
		}
		
		return entity;
	}
	
	public static class DefaultEntityDeserializer implements JsonDeserializer<DefaultEntity> {
		public DefaultEntity deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonNull()) {
				return null;
			}
			return deserializeBaseParameter(json.getAsJsonObject(), new DefaultEntity());
		}
	}
	
	public static class VersionDeserializer implements JsonDeserializer<V8Version> {
		public V8Version deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			V8Version entity = deserializeBaseParameter(obj, new V8Version());
			
			if (obj.has("version")) {
				entity.version = obj.getAsJsonPrimitive("version").getAsString();
			}
				
			return entity;
		}
	}
	
	public static class FiguresDeserializer implements JsonDeserializer<Figures> {
		public Figures deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			Figures entity = new Figures();
			
			if (obj.has("alive")) {
				JsonObject alive = obj.getAsJsonObject("alive");
				entity.aliveCount = alive.getAsJsonPrimitive("count").getAsLong();
				entity.aliveSize = alive.getAsJsonPrimitive("size").getAsLong();
			}

			if (obj.has("dead")) {
				JsonObject dead = obj.getAsJsonObject("dead");
				entity.deadCount = dead.getAsJsonPrimitive("count").getAsLong();
				entity.deadSize = dead.getAsJsonPrimitive("size").getAsLong();
				entity.deadDeletion = dead.getAsJsonPrimitive("deletion").getAsLong();
			}
			
			if (obj.has("datafiles")) {
				JsonObject datafiles = obj.getAsJsonObject("datafiles");
				entity.datafileCount = datafiles.getAsJsonPrimitive("count").getAsLong();
			}
			
			if (obj.has("journals")) {
				JsonObject journals = obj.getAsJsonObject("journals");
				entity.journalsCount = journals.getAsJsonPrimitive("count").getAsLong();
				entity.journalsFileSize = journals.getAsJsonPrimitive("fileSize").getAsLong();
			}
			
			return entity;
		}
	}
	
	public static class CollectionEntityDeserializer implements JsonDeserializer<CollectionEntity> {
		public CollectionEntity deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {

			if (json.isJsonNull()) {
				return null;
			}

			JsonObject obj = json.getAsJsonObject();
			CollectionEntity entity = deserializeBaseParameter(obj, new CollectionEntity());
			
			if (obj.has("name")) {
				entity.name = obj.getAsJsonPrimitive("name").getAsString();
			}
			
			if (obj.has("id")) {
				entity.id = obj.getAsJsonPrimitive("id").getAsLong();
			}
			
			if (obj.has("status")) {
				entity.status = context.deserialize(obj.get("status"), CollectionStatus.class);
			}
			
			if (obj.has("waitForSync")) {
				entity.waitForSync = obj.getAsJsonPrimitive("waitForSync").getAsBoolean();
			}

			if (obj.has("journalSize")) {
				entity.journalSize = obj.getAsJsonPrimitive("journalSize").getAsLong();
			}

			if (obj.has("count")) {
				entity.count = obj.getAsJsonPrimitive("count").getAsLong();
			}
			
			if (obj.has("figures")) {
				entity.figures = context.deserialize(obj.get("figures"), Figures.class);
			}

			return entity;
		}
	}
	
	public static class CollectionsEntityDeserializer implements JsonDeserializer<CollectionsEntity> {
		private Type collectionsType = new TypeToken<List<CollectionEntity>>(){}.getType();
		private Type namesType = new TypeToken<Map<String, CollectionEntity>>(){}.getType();
		public CollectionsEntity deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			CollectionsEntity entity = deserializeBaseParameter(obj, new CollectionsEntity());
			
			if (obj.has("collections")) {
				entity.collections = context.deserialize(obj.get("collections"), collectionsType);
			}
			if (obj.has("names")) {
				entity.names = context.deserialize(obj.get("names"), namesType);
			}
			
			return entity;
		}
	}
	
	public static class CursorEntityDeserializer implements JsonDeserializer<CursorEntity<?>> {
		private Type bindVarsType = new TypeToken<List<String>>(){}.getType();
		public CursorEntity<?> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
	
			
			JsonObject obj = json.getAsJsonObject();
			CursorEntity<?> entity = deserializeBaseParameter(obj, new CursorEntity<Object>());
			
			// resultは処理しない。後で処理をする。
			if (obj.has("result")) {
				entity._array = obj.getAsJsonArray("result");
			}
			
			if (obj.has("hasMore")) {
				entity.hasMore = obj.getAsJsonPrimitive("hasMore").getAsBoolean();
			}
			
			if (obj.has("count")) {
				entity.count = obj.getAsJsonPrimitive("count").getAsInt();
			}
			
			if (obj.has("id")) {
				entity.cursorId = obj.getAsJsonPrimitive("id").getAsLong();
			}
			
			if (obj.has("bindVars")) {
				entity.bindVars = context.deserialize(obj.get("bindVars"), bindVarsType);
			}
			
			return entity;
		}
	}
	
	public static class DocumentEntityDeserializer implements JsonDeserializer<DocumentEntity<?>> {

		public DocumentEntity<?> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return new DocumentEntity<Object>();
			}
			
			if (json.isJsonPrimitive()) {
				return new DocumentEntity<Object>();
			}
			
			if (json.isJsonArray()) {
				return new DocumentEntity<Object>();
			}
			
			JsonObject obj = json.getAsJsonObject();
			DocumentEntity<?> entity = deserializeBaseParameter(obj, new DocumentEntity<Object>());
			
			if (obj.has("_rev")) {
				entity.documentRevision = obj.getAsJsonPrimitive("_rev").getAsLong();
			}
			
			if (obj.has("_id")) {
				entity.documentHandle = obj.getAsJsonPrimitive("_id").getAsString();
			}
			
			// 他のフィールドはリフレクションで。
			
			return entity;
		}
		
	}
		
	public static class DocumentsEntityDeserializer implements JsonDeserializer<DocumentsEntity> {
		private Type documentsType = new TypeToken<List<String>>(){}.getType();
		public DocumentsEntity deserialize(JsonElement json,
				Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			DocumentsEntity entity = deserializeBaseParameter(obj, new DocumentsEntity());
			
			if (obj.has("documents")) {
				entity.documents = context.deserialize(obj.get("documents"), documentsType);
			}
			
			return entity;
		}
		
	}

	public static class IndexEntityDeserializer implements JsonDeserializer<IndexEntity> {
		private Type fieldsType = new TypeToken<List<String>>(){}.getType();
		public IndexEntity deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
	
			JsonObject obj = json.getAsJsonObject();
			IndexEntity entity = deserializeBaseParameter(obj, new IndexEntity());
			
			if (obj.has("id")) {
				entity.id = obj.getAsJsonPrimitive("id").getAsString();
			}
			
			if (obj.has("type")) {
				String type = obj.getAsJsonPrimitive("type").getAsString().toUpperCase(Locale.US);
				if (type.startsWith(IndexType.GEO.name())) {
					entity.type = IndexType.GEO;
				} else {
					entity.type = IndexType.valueOf(type);
				}
			}

			if (obj.has("fields")) {
				entity.fields = context.deserialize(obj.getAsJsonArray("fields"), fieldsType);
			}
			
			if (obj.has("getJson")) {
				entity.getJson = obj.getAsJsonPrimitive("getJson").getAsBoolean();
			}
			
			if (obj.has("isNewlyCreated")) {
				entity.isNewlyCreated = obj.getAsJsonPrimitive("isNewlyCreated").getAsBoolean();
			}

			if (obj.has("unique")) {
				entity.unique = obj.getAsJsonPrimitive("unique").getAsBoolean();
			}
			
			if (obj.has("size")) {
				entity.size = obj.getAsJsonPrimitive("size").getAsInt();
			}
			
			return entity;
		}
	}

	public static class IndexesEntityDeserializer implements JsonDeserializer<IndexesEntity> {
		private Type indexesType = new TypeToken<List<IndexEntity>>(){}.getType();
		private Type identifiersType = new TypeToken<Map<String, IndexEntity>>(){}.getType();
		public IndexesEntity deserialize(JsonElement json,
				Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			IndexesEntity entity = deserializeBaseParameter(obj, new IndexesEntity());

			if (obj.has("indexes")) {
				entity.indexes = context.deserialize(obj.get("indexes"), indexesType);
			}

			if (obj.has("identifiers")) {
				entity.identifiers = context.deserialize(obj.get("identifiers"), identifiersType);
			}

			return entity;
		}
		
	}

	public static class EdgeEntityDeserializer implements JsonDeserializer<EdgeEntity<?>> {
		public EdgeEntity<?> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
	
			JsonObject obj = json.getAsJsonObject();
			EdgeEntity<?> entity = deserializeBaseParameter(obj, new EdgeEntity<Object>());
			
			if (obj.has("_rev")) {
				entity.revision = obj.getAsJsonPrimitive("_rev").getAsLong();
			}
			
			if (obj.has("_id")) {
				entity.edgeHandle = obj.getAsJsonPrimitive("_id").getAsString();
			}
			
			if (obj.has("_from")) {
				entity.fromHandle = obj.getAsJsonPrimitive("_from").getAsString();
			}
			
			if (obj.has("_to")) {
				entity.toHandle = obj.getAsJsonPrimitive("_to").getAsString();
			}
			
			// attributeは処理しない
			
			return entity;
		}
	}

	public static class EdgesEntityDeserializer implements JsonDeserializer<EdgesEntity<?>> {
		public EdgesEntity<?> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
	
			JsonObject obj = json.getAsJsonObject();
			EdgesEntity<?> entity = deserializeBaseParameter(obj, new EdgesEntity<Object>());
			if (obj.has("edges")) {
				entity._edges = obj.getAsJsonArray("edges");
			}
			
			return entity;
		}
	}

	public static class AdminLogEntryEntityDeserializer implements JsonDeserializer<AdminLogEntity> {
		public AdminLogEntity deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}

			JsonObject obj = json.getAsJsonObject();
			AdminLogEntity entity = deserializeBaseParameter(obj, new AdminLogEntity());
			// 全ての要素は必ずあることが前提なのでhasチェックはしない
			int[] lids = context.deserialize(obj.getAsJsonArray("lid"), int[].class);
			int[] levels = context.deserialize(obj.getAsJsonArray("level"), int[].class);
			long[] timestamps = context.deserialize(obj.getAsJsonArray("timestamp"), long[].class);
			String[] texts = context.deserialize(obj.getAsJsonArray("text"), String[].class);
			
			// 配列のサイズが全て同じであること
			if (lids.length != levels.length || lids.length != timestamps.length || lids.length != texts.length) {
				throw new IllegalStateException("each parameters returns wrong length.");
			}
			
			entity.logs = new ArrayList<AdminLogEntity.LogEntry>(lids.length);
			for (int i = 0; i < lids.length; i++) {
				AdminLogEntity.LogEntry entry = new AdminLogEntity.LogEntry();
				entry.lid = lids[i];
				entry.level = levels[i];
				entry.timestamp = new Date(timestamps[i] * 1000L);
				entry.text = texts[i];
				entity.logs.add(entry);
			}

			if (obj.has("totalAmount")) {
				entity.totalAmount = obj.getAsJsonPrimitive("totalAmount").getAsInt();
			}

			return entity;
		}
	}
	
	public static class AdminStatusEntityDeserializer implements JsonDeserializer<AdminStatusEntity> {
		public AdminStatusEntity deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			AdminStatusEntity entity = deserializeBaseParameter(obj, new AdminStatusEntity());
			
			if (obj.has("system")) {
				JsonObject system = obj.getAsJsonObject("system");
				if (system.has("minorPageFaults")) {
					entity.minorPageFaults = system.getAsJsonPrimitive("minorPageFaults").getAsLong();
				}
				if (system.has("majorPageFaults")) {
					entity.majorPageFaults = system.getAsJsonPrimitive("majorPageFaults").getAsLong();
				}
				if (system.has("userTime")) {
					entity.userTime = system.getAsJsonPrimitive("userTime").getAsDouble();
				}
				if (system.has("systemTime")) {
					entity.systemTime = system.getAsJsonPrimitive("systemTime").getAsDouble();
				}
				if (system.has("numberThreads")) {
					entity.numberThreads = system.getAsJsonPrimitive("numberThreads").getAsInt();
				}
				if (system.has("residentSize")) {
					entity.residentSize = system.getAsJsonPrimitive("residentSize").getAsLong();
				}
				if (system.has("virtualSize")) {
					entity.virtualSize = system.getAsJsonPrimitive("virtualSize").getAsLong();
				}
			}
			
			return entity;
		}
	}
	
	public static class AdminConfigurationEntityDeserializer implements JsonDeserializer<AdminConfigurationEntity> {

		public AdminConfigurationEntity deserialize(JsonElement json,
				Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			AdminConfigurationEntity entity = deserializeBaseParameter(obj, new AdminConfigurationEntity());
			
			convertMap(context, entity, null, obj);

			return entity;
		}
		
		private void convertMap(JsonDeserializationContext context, AdminConfigurationEntity entity, String prefixKey, JsonObject obj) {
			
			for (Entry<String, JsonElement> entry : obj.entrySet()) {
				String key = entry.getKey();
				JsonElement value = entry.getValue();
				String actualKey = (prefixKey == null ? key : prefixKey + "." + key);
				if (value.isJsonObject()) {
					JsonObject childObj = value.getAsJsonObject();
					if (childObj.has("value")) {
						if (childObj.isJsonPrimitive()) {
							JsonPrimitive prim = childObj.getAsJsonPrimitive("value");
							if (prim.isNumber()) {
								entity.put(actualKey, prim.getAsNumber());
							} else if (prim.isBoolean()) {
								entity.put(actualKey, prim.getAsBoolean());
							} else if (prim.isString()) {
								entity.put(actualKey, prim.getAsString());
							} else if (prim.isJsonNull()) {
								entity.put(actualKey, null);
							}
						} else if (childObj.isJsonNull()) {
							entity.put(actualKey, null);
						} else if (childObj.isJsonArray()) {
							entity.put(actualKey, context.deserialize(childObj, List.class));
						} else if (childObj.isJsonObject()) {
							entity.put(actualKey, context.deserialize(childObj, Map.class));
						}
					} else {
						// value属性が無い場合はセクション扱いなので再帰的に処理する
						convertMap(context, entity, actualKey, childObj);
					}
				// 形式としてObject配下のvalue属性に値が入るので、以下は無視する
				} else if (value.isJsonArray()) {
				} else if (value.isJsonNull()) {
				} else if (value.isJsonPrimitive()) {
				}
			}
		}
		
	}
	
	public static class AdminConfigDescriptionEntityDeserializer implements JsonDeserializer<AdminConfigDescriptionEntity> {
		public AdminConfigDescriptionEntity deserialize(JsonElement json,
				Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			AdminConfigDescriptionEntity entity = deserializeBaseParameter(obj, new AdminConfigDescriptionEntity());
			
			convertMap(context, entity, null, obj);
			return entity;

		}
		
		private void convertMap(JsonDeserializationContext context, AdminConfigDescriptionEntity entity, String prefixKey, JsonObject obj) {
			
			for (Entry<String, JsonElement> entry : obj.entrySet()) {
				String key = entry.getKey();
				JsonElement value = entry.getValue();
				String actualKey = (prefixKey == null ? key : prefixKey + "." + key);
				if (value.isJsonObject()) {
					JsonObject childObj = value.getAsJsonObject();
					String type = childObj.getAsJsonPrimitive("type").getAsString();
					if ("section".equals(type)) {
						// 他のFieldを全て処理する
						convertMap(context, entity, actualKey, childObj);
					} else {
						DescriptionEntry description = new DescriptionEntry();
						// 設定項目
						description.type = type;
						if (childObj.has("name")) {
							description.name = childObj.getAsJsonPrimitive("name").getAsString();
						}
						if (childObj.has("readonly")) {
							description.readonly = childObj.getAsJsonPrimitive("readonly").getAsBoolean();
						}
						if (childObj.has("values")) { // pull-downの時
							description.values = context.deserialize(childObj.getAsJsonArray("values"), Object[].class);
						}
						entity.put(actualKey, description);
					}
				} else if (value.isJsonArray()) {
				} else if (value.isJsonNull()) {
				} else if (value.isJsonPrimitive()) {
				}
			}
		}
		
	}
	
//	public static class ScalarExampleEntityDeserializer implements JsonDeserializer<DocumentEntity<?>> {
//
//		public ScalarExampleEntity<?> deserialize(JsonElement json, Type typeOfT,
//				JsonDeserializationContext context) throws JsonParseException {
//			
//			if (json.isJsonNull()) {
//				return null;
//			}
//			
//			JsonObject obj = json.getAsJsonObject();
//			ScalarExampleEntity<?> entity = deserializeBaseParameter(obj, new ScalarExampleEntity<Object>());
//			
//			// document属性は別のレイヤーで
//			
//			return entity;
//		}
//		
//	}

	
}
