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
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import at.orz.arangodb.entity.CollectionEntity.Figures;
import at.orz.arangodb.entity.ExplainEntity.ExpressionEntity;
import at.orz.arangodb.entity.ExplainEntity.PlanEntity;
import at.orz.arangodb.entity.ReplicationInventoryEntity.Collection;
import at.orz.arangodb.entity.ReplicationInventoryEntity.CollectionParameter;
import at.orz.arangodb.entity.ReplicationInventoryEntity.State;
import at.orz.arangodb.entity.StatisticsDescriptionEntity.Figure;
import at.orz.arangodb.entity.StatisticsDescriptionEntity.Group;
import at.orz.arangodb.entity.StatisticsEntity.FigureValue;
import at.orz.arangodb.util.DateUtils;

import ch.qos.logback.core.joran.action.ParamAction;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class EntityDeserializers {
	
	private static class ClassHolder {
		private Class<?>[] clazz;
		private int idx;
		ClassHolder(Class<?>... clazz) {
			this.clazz = clazz;
			this.idx = 0;
		}
		public boolean isEmpty() {
			return clazz == null || clazz.length == 0;
		}
		public Class<?> get() {
			if (isEmpty()) {
				return null;
			}
			return clazz[idx];
		}
		public Class<?> next() {
			if (isEmpty()) {
				return null;
			}
			if (idx + 1 >= clazz.length) {
				throw new IllegalStateException("idx max-over!! idx=" + (idx+1));
			}
			return clazz[++idx];
		}
		public boolean hasNext() {
			if (isEmpty()) {
				return false;
			}
			if (idx + 1 >= clazz.length) {
				return false;
			}
			return true;
		}
		public Class<?> back() {
			if (isEmpty()) {
				return null;
			}
			if (idx - 1 < 0) {
				throw new IllegalStateException("idx min-over!! idx=" + (idx-1));
			}
			return clazz[--idx];
		}
	}
	private static ThreadLocal<ClassHolder> parameterizedBridger = new ThreadLocal<ClassHolder>();
	public static void setParameterized(Class<?>... clazz) {
		parameterizedBridger.set(new ClassHolder(clazz));
	}
	public static void removeParameterized() {
		parameterizedBridger.remove();
	}
	private static Class<?> getParameterized() {
		ClassHolder holder = parameterizedBridger.get();
		if (holder == null) {
			return null;
		}
		return holder.get();
	}
	private static boolean hasNextParameterized() {
		ClassHolder holder = parameterizedBridger.get();
		if (holder == null) {
			return false;
		}
		return holder.hasNext();
	}
	private static Class<?> nextParameterized() {
		ClassHolder holder = parameterizedBridger.get();
		if (holder == null) {
			return null;
		}
		return holder.next();
	}
	private static Class<?> backParameterized() {
		ClassHolder holder = parameterizedBridger.get();
		if (holder == null) {
			return null;
		}
		return holder.back();
	}

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
	
	public static class VersionDeserializer implements JsonDeserializer<ArangoVersion> {
		public ArangoVersion deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			ArangoVersion entity = deserializeBaseParameter(obj, new ArangoVersion());
			
			if (obj.has("server")) {
				entity.server = obj.getAsJsonPrimitive("server").getAsString();
			}
			
			if (obj.has("version")) {
				entity.version = obj.getAsJsonPrimitive("version").getAsString();
			}
				
			return entity;
		}
	}
	
	public static class ArangoUnixTimeDeserializer implements JsonDeserializer<ArangoUnixTime> {
		public ArangoUnixTime deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {

			if (json.isJsonNull()) {
				return null;
			}

			JsonObject obj = json.getAsJsonObject();
			ArangoUnixTime entity = deserializeBaseParameter(obj, new ArangoUnixTime());

			if (obj.has("time")) {
				entity.time = obj.getAsJsonPrimitive("time").getAsDouble();
				String time = obj.getAsJsonPrimitive("time").getAsString(); // 実際はdoubleだけど精度の問題が心配なので文字列で処理する。
                entity.second = (int) entity.time;
                
                int pos = time.indexOf('.');
                entity.microsecond = (pos >= 0 && pos+1 != time.length()) ? Integer.parseInt(time.substring(pos+1)) : 0;
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
				entity.datafileFileSize = datafiles.getAsJsonPrimitive("fileSize").getAsLong();
			}
			
			if (obj.has("journals")) {
				JsonObject journals = obj.getAsJsonObject("journals");
				entity.journalsCount = journals.getAsJsonPrimitive("count").getAsLong();
				entity.journalsFileSize = journals.getAsJsonPrimitive("fileSize").getAsLong();
			}
			
			if (obj.has("compactors")) {
				JsonObject compactors = obj.getAsJsonObject("compactors");
				entity.compactorsCount = compactors.getAsJsonPrimitive("count").getAsLong();
				entity.compactorsFileSize = compactors.getAsJsonPrimitive("fileSize").getAsLong();
			}
			
			if (obj.has("shapefiles")) {
				JsonObject shapefiles = obj.getAsJsonObject("shapefiles");
				entity.shapefilesCount = shapefiles.getAsJsonPrimitive("count").getAsLong();
				entity.shapefilesFileSize = shapefiles.getAsJsonPrimitive("fileSize").getAsLong();
			}
			
			if (obj.has("shapes")) {
				JsonObject shapes = obj.getAsJsonObject("shapes");
				entity.shapesCount = shapes.getAsJsonPrimitive("count").getAsLong();
			}
			
			if (obj.has("attributes")) {
				JsonObject attributes = obj.getAsJsonObject("attributes");
				entity.attributesCount = attributes.getAsJsonPrimitive("count").getAsLong();
			}
			
			return entity;
		}
	}
	
	public static class CollectionKeyOptionDeserializer implements JsonDeserializer<CollectionKeyOption> {
		public CollectionKeyOption deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

			if (json.isJsonNull()) {
				return null;
			}

			JsonObject obj = json.getAsJsonObject();
			CollectionKeyOption entity = new CollectionKeyOption();

			if (obj.has("type")) {
				entity.type = obj.getAsJsonPrimitive("type").getAsString();
			}
			
			if (obj.has("allowUserKeys")) {
				entity.allowUserKeys = obj.getAsJsonPrimitive("allowUserKeys").getAsBoolean();
			}
			
			if (obj.has("increment")) {
				entity.increment = obj.getAsJsonPrimitive("increment").getAsLong();
			}
			
			if (obj.has("offset")) {
				entity.offset = obj.getAsJsonPrimitive("offset").getAsLong();
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
			
			if (obj.has("isSystem")) {
				entity.isSystem = obj.getAsJsonPrimitive("isSystem").getAsBoolean();
			}
			
			if (obj.has("isVolatile")) {
				entity.isVolatile = obj.getAsJsonPrimitive("isVolatile").getAsBoolean();
			}

			if (obj.has("journalSize")) {
				entity.journalSize = obj.getAsJsonPrimitive("journalSize").getAsLong();
			}

			if (obj.has("count")) {
				entity.count = obj.getAsJsonPrimitive("count").getAsLong();
			}
			
			if (obj.has("revision")) {
				entity.revision = obj.getAsJsonPrimitive("revision").getAsLong();
			}
			
			if (obj.has("figures")) {
				entity.figures = context.deserialize(obj.get("figures"), Figures.class);
			}
			
			if (obj.has("type")) {
				entity.type = CollectionType.valueOf(obj.getAsJsonPrimitive("type").getAsInt());
			}
			
			if (obj.has("keyOptions")) {
				entity.keyOptions = context.deserialize(obj.get("keyOptions"), CollectionKeyOption.class);
			}
			
			if (obj.has("checksum")) {
				entity.checksum = obj.getAsJsonPrimitive("checksum").getAsLong();
			}

			if (obj.has("doCompact")) {
				entity.doCompact = obj.getAsJsonPrimitive("doCompact").getAsBoolean();
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
			CursorEntity<Object> entity = deserializeBaseParameter(obj, new CursorEntity<Object>());
			
			// TODO
			if (obj.has("result")) {
				JsonArray array = obj.getAsJsonArray("result");
				if (array == null || array.isJsonNull() || array.size() == 0) {
					entity.results = Collections.emptyList();
				} else {
					Class<?> clazz = getParameterized();
					boolean withDocument = DocumentEntity.class.isAssignableFrom(clazz);
					if (withDocument) {
						nextParameterized();
					}
					try {
						List<Object> list = new ArrayList<Object>(array.size());
						for (int i = 0, imax = array.size(); i < imax; i++) {
							list.add(context.deserialize(array.get(i), clazz));
						}
						entity.results = list;
					} finally {
						if (withDocument) {
							backParameterized();
						}
					}
				}
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
			
			if (obj.has("_key")) {
				entity.documentKey = obj.getAsJsonPrimitive("_key").getAsString();
			}
			
			// 他のフィールドはリフレクションで。
			// TODO:
			Class<?> clazz = getParameterized();
			if (clazz != null) {
				entity.entity = context.deserialize(obj, clazz);
			}
			
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
			
			if (obj.has("minLength")) {
				entity.minLength = obj.getAsJsonPrimitive("minLength").getAsInt();
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
			
			if (obj.has("_key")) {
				entity.key = obj.getAsJsonPrimitive("_key").getAsString();
			}
			
			// FIXME
			Class<?> clazz = getParameterized();
			if (clazz != null) {
				entity.attributes = context.deserialize(obj, clazz);
			}
			
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
			EdgesEntity<Object> entity = deserializeBaseParameter(obj, new EdgesEntity<Object>());
			
			Class<?> clazz = getParameterized();
			if (obj.has("edges")) {
				JsonArray array = obj.getAsJsonArray("edges");
				
				boolean nest = EdgeEntity.class.isAssignableFrom(clazz);
				if (nest) nextParameterized();
				try {
					List<EdgeEntity<?>> list = new ArrayList<EdgeEntity<?>>(array.size());
					for (int i = 0, imax = array.size(); i < imax; i++) {
						EdgeEntity<Object> edge = context.deserialize(array.get(i), clazz);
						list.add(edge);
					}
					entity.edges = list;
				} finally {
					if (nest) backParameterized();
				}
				
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
	
	public static class StatisticsEntityDeserializer implements JsonDeserializer<StatisticsEntity> {
		Type countsType = new TypeToken<long[]>(){}.getType();
		public StatisticsEntity deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			StatisticsEntity entity = deserializeBaseParameter(obj, new StatisticsEntity());
			
			if (obj.has("system")) {
				StatisticsEntity.System sys = new StatisticsEntity.System();
				entity.system = sys;

				JsonObject system = obj.getAsJsonObject("system");
				if (system.has("minorPageFaults")) {
					sys.minorPageFaults = system.getAsJsonPrimitive("minorPageFaults").getAsLong();
				}
				if (system.has("majorPageFaults")) {
					sys.majorPageFaults = system.getAsJsonPrimitive("majorPageFaults").getAsLong();
				}
				if (system.has("userTime")) {
					sys.userTime = system.getAsJsonPrimitive("userTime").getAsDouble();
				}
				if (system.has("systemTime")) {
					sys.systemTime = system.getAsJsonPrimitive("systemTime").getAsDouble();
				}
				if (system.has("numberOfThreads")) {
					sys.numberOfThreads = system.getAsJsonPrimitive("numberOfThreads").getAsInt();
				}
				if (system.has("residentSize")) {
					sys.residentSize = system.getAsJsonPrimitive("residentSize").getAsLong();
				}
				if (system.has("virtualSize")) {
					sys.virtualSize = system.getAsJsonPrimitive("virtualSize").getAsLong();
				}
			}
			
			if (obj.has("client")) {
				StatisticsEntity.Client cli = new StatisticsEntity.Client();
				cli.figures = new TreeMap<String, StatisticsEntity.FigureValue>();
				entity.client = cli;

				JsonObject client = obj.getAsJsonObject("client");
				if (client.has("httpConnections")) {
					cli.httpConnections = client.getAsJsonPrimitive("httpConnections").getAsInt();
				}
				for (Entry<String, JsonElement> ent : client.entrySet()) {
					if (!ent.getKey().equals("httpConnections")) {
						JsonObject f = ent.getValue().getAsJsonObject();
						FigureValue fv = new FigureValue();
						fv.sum = f.getAsJsonPrimitive("sum").getAsDouble();
						fv.count = f.getAsJsonPrimitive("count").getAsLong();
						fv.counts = context.deserialize(f.getAsJsonArray("counts"), countsType);
						cli.figures.put(ent.getKey(), fv);
					}
				}
			}
			
			if (obj.has("server")) {
				JsonObject svr = obj.getAsJsonObject("server");
				entity.server = new StatisticsEntity.Server();
				
				if (svr.has("uptime")) {
					entity.server.uptime = svr.getAsJsonPrimitive("uptime").getAsDouble();
				}
			}
			
			return entity;
			
		}
	}

	public static class StatisticsDescriptionEntityDeserializer implements JsonDeserializer<StatisticsDescriptionEntity> {
		Type cutsTypes = new TypeToken<BigDecimal[]>(){}.getType();
		public StatisticsDescriptionEntity deserialize(JsonElement json,
				Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			if (json.isJsonNull()) {
				return null;
			}

			JsonObject obj = json.getAsJsonObject();
			StatisticsDescriptionEntity entity = deserializeBaseParameter(obj, new StatisticsDescriptionEntity());

			if (obj.has("groups")) {
				JsonArray groups = obj.getAsJsonArray("groups");
				entity.groups = new ArrayList<StatisticsDescriptionEntity.Group>(groups.size());
				for (int i = 0, imax = groups.size(); i < imax; i++) {
					JsonObject g = groups.get(i).getAsJsonObject();
					
					Group group = new Group();
					group.group = g.getAsJsonPrimitive("group").getAsString();
					group.name = g.getAsJsonPrimitive("name").getAsString();
					group.description = g.getAsJsonPrimitive("description").getAsString();
					
					entity.groups.add(group);
				}
			}
			
			if (obj.has("figures")) {
				JsonArray figures = obj.getAsJsonArray("figures");
				entity.figures = new ArrayList<StatisticsDescriptionEntity.Figure>(figures.size());
				for (int i = 0, imax = figures.size(); i < imax; i++) {
					JsonObject f = figures.get(i).getAsJsonObject();
					
					Figure figure = new Figure();
					figure.group = f.getAsJsonPrimitive("group").getAsString();
					figure.identifier = f.getAsJsonPrimitive("identifier").getAsString();
					figure.name = f.getAsJsonPrimitive("name").getAsString();
					figure.description = f.getAsJsonPrimitive("description").getAsString();
					figure.type = f.getAsJsonPrimitive("type").getAsString();
					figure.units = f.getAsJsonPrimitive("units").getAsString();
					if (f.has("cuts")) {
						figure.cuts = context.deserialize(f.getAsJsonArray("cuts"), cutsTypes);
					}
					
					entity.figures.add(figure);
					
				}
			}
			
			return entity;
		}
	}

	public static class ScalarExampleEntityDeserializer implements JsonDeserializer<ScalarExampleEntity<?>> {

		public ScalarExampleEntity<?> deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			ScalarExampleEntity<?> entity = deserializeBaseParameter(obj, new ScalarExampleEntity<Object>());
			
			// TODO
			// document属性は別のレイヤーで
			if (obj.has("document")) {
				entity.document = context.deserialize(obj.get("document"), DocumentEntity.class);
			}
			
			return entity;
		}
		
	}

	public static class SimpleByResultEntityDeserializer implements JsonDeserializer<SimpleByResultEntity> {
		
		public SimpleByResultEntity deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			SimpleByResultEntity entity = deserializeBaseParameter(obj, new SimpleByResultEntity());
			
			if (obj.has("deleted")) {
				entity.count = entity.deleted = obj.getAsJsonPrimitive("deleted").getAsInt();
			}
			
			if (obj.has("replaced")) {
				entity.count = entity.replaced = obj.getAsJsonPrimitive("replaced").getAsInt();
			}
			
			if (obj.has("updated")) {
				entity.count = entity.updated = obj.getAsJsonPrimitive("updated").getAsInt();
			}
			
			return entity;
		}
		
	}
	
	public static class ExplainEntityDeserializer implements JsonDeserializer<ExplainEntity> {

		public ExplainEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			ExplainEntity entity = deserializeBaseParameter(obj, new ExplainEntity());
			
			if (obj.has("plan")) {
				JsonArray array = obj.getAsJsonArray("plan");
				ArrayList<PlanEntity> planList = new ArrayList<ExplainEntity.PlanEntity>(array.size());
				for (int i = 0; i < array.size(); i++) {
					PlanEntity plan = new PlanEntity();
					JsonObject planObj = array.get(i).getAsJsonObject();
					if (planObj.has("id")) {
						plan.id = planObj.getAsJsonPrimitive("id").getAsLong();
					}
					if (planObj.has("loopLevel")) {
						plan.loopLevel = planObj.getAsJsonPrimitive("loopLevel").getAsInt();
					}
					if (planObj.has("type")) {
						plan.type = planObj.getAsJsonPrimitive("type").getAsString();
					}
					if (planObj.has("resultVariable")) {
						plan.resultVariable = planObj.getAsJsonPrimitive("resultVariable").getAsString();
					}
					if (planObj.has("offset")) {
						plan.offset = planObj.getAsJsonPrimitive("offset").getAsLong();
					}
					if (planObj.has("count")) {
						plan.count = planObj.getAsJsonPrimitive("count").getAsLong();
					}
					if (planObj.has("expression")) {
						plan.expression = new ExpressionEntity();
						JsonObject expObj = planObj.getAsJsonObject("expression");
						if (expObj.has("type")) {
							plan.expression.type = expObj.getAsJsonPrimitive("type").getAsString();
						}
						if (expObj.has("value")) {
							plan.expression.value = expObj.getAsJsonPrimitive("value").getAsString();
						}
						if (expObj.has("extra")) {
							plan.expression.extra = context.deserialize(expObj.getAsJsonObject("extra"), Map.class);
						}
					}
					planList.add(plan);
				}
				entity.plan = planList;
			}
			
			return entity;
		}
		
	}

	
	public static class UserEntityDeserializer implements JsonDeserializer<UserEntity> {

		public UserEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			UserEntity entity = deserializeBaseParameter(obj, new UserEntity());
			
			if (obj.has("user")) {
				entity.user = obj.getAsJsonPrimitive("user").getAsString();
			}
			
			if (obj.has("password")) {
				entity.password = obj.getAsJsonPrimitive("password").getAsString();
			}
			
			if (obj.has("active")) {
				entity.active = obj.getAsJsonPrimitive("active").getAsBoolean();
			}
			
			if (obj.has("extra")) {
				entity.extra = context.deserialize(obj.getAsJsonObject("extra"), Map.class);
			}
			
			return entity;
		}
		
	}

	public static class ImportResultEntityDeserializer implements JsonDeserializer<ImportResultEntity> {
		public ImportResultEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			ImportResultEntity entity = deserializeBaseParameter(obj, new ImportResultEntity());
			
			if (obj.has("created")) {
				entity.created = obj.getAsJsonPrimitive("created").getAsInt();
			}

			if (obj.has("errors")) {
				entity.errors = obj.getAsJsonPrimitive("errors").getAsInt();
			}

			if (obj.has("empty")) {
				entity.empty = obj.getAsJsonPrimitive("empty").getAsInt();
			}

			return entity;
		}
	}

	public static class DatabaseEntityDeserializer implements JsonDeserializer<DatabaseEntity> {
		public DatabaseEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			DatabaseEntity entity = deserializeBaseParameter(obj, new DatabaseEntity());
			
			if (obj.has("result")) {
				JsonObject result = obj.getAsJsonObject("result");
				if (result.has("name")) {
					entity.name = result.getAsJsonPrimitive("name").getAsString();
				}
				if (result.has("id")) {
					entity.id = result.getAsJsonPrimitive("id").getAsString();
				}
				if (result.has("path")) {
					entity.path = result.getAsJsonPrimitive("path").getAsString();
				}
				if (result.has("isSystem")) {
					entity.isSystem = result.getAsJsonPrimitive("isSystem").getAsBoolean();
				}
			}
			
			return entity;
		}
	}

	public static class StringsResultEntityDeserializer implements JsonDeserializer<StringsResultEntity> {
		Type resultType = new TypeToken<ArrayList<String>>(){}.getType();
		public StringsResultEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			StringsResultEntity entity = deserializeBaseParameter(obj, new StringsResultEntity());
			
			if (obj.has("result")) {
				entity.result = context.deserialize(obj.get("result"), resultType);
			}
			
			return entity;
		}
	}

	public static class BooleanResultEntityDeserializer implements JsonDeserializer<BooleanResultEntity> {
		public BooleanResultEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			BooleanResultEntity entity = deserializeBaseParameter(obj, new BooleanResultEntity());
			
			if (obj.has("result")) {
				entity.result = obj.getAsJsonPrimitive("result").getAsBoolean();
			}
			
			return entity;
		}
	}

	public static class EndpointDeserializer implements JsonDeserializer<Endpoint> {
		Type databasesType = new TypeToken<List<String>>(){}.getType();
		public Endpoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			
			Endpoint entity = new Endpoint();
			entity.databases = context.deserialize(obj.getAsJsonArray("databases"), databasesType);
			entity.endpoint = obj.getAsJsonPrimitive("endpoint").getAsString();
			
			return entity;
		}
	}
	
	public static class DocumentResultEntityDeserializer implements JsonDeserializer<DocumentResultEntity<?>> {
		Type documentsType = new TypeToken<List<DocumentEntity<?>>>(){}.getType();
		public DocumentResultEntity<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			DocumentResultEntity<Object> entity = deserializeBaseParameter(obj, new DocumentResultEntity<Object>());
			
			if (obj.has("result")) {
				JsonElement resultElem = obj.get("result");
				if (resultElem.isJsonArray()) {
					entity.result = context.deserialize(resultElem, documentsType);
				} else if (resultElem.isJsonObject()) {
					DocumentEntity<Object> doc = context.deserialize(resultElem, DocumentEntity.class);
					List<DocumentEntity<Object>> list = new ArrayList<DocumentEntity<Object>>(1);
					list.add(doc);
					entity.result = list;
				} else {
					throw new IllegalStateException("result type is not array or object:" + resultElem);
				}
			}
			
			return entity;
		}
	}

	public static class ReplicationInventoryEntityDeserializer implements JsonDeserializer<ReplicationInventoryEntity> {
		private Type indexesType = new TypeToken<List<IndexEntity>>(){}.getType();
		public ReplicationInventoryEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (json.isJsonNull()) {
				return null;
			}
			
			JsonObject obj = json.getAsJsonObject();
			ReplicationInventoryEntity entity = deserializeBaseParameter(obj, new ReplicationInventoryEntity());
			
			if (obj.has("collections")) {
				JsonArray collections = obj.getAsJsonArray("collections");
				entity.collections = new ArrayList<ReplicationInventoryEntity.Collection>(collections.size());
				for (int i = 0, imax = collections.size(); i < imax; i++) {
					JsonObject elem = collections.get(i).getAsJsonObject();
					Collection col = new Collection();
					
					if (elem.has("parameters")) {
						JsonObject parameters = elem.getAsJsonObject("parameters");

						col.parameter = new CollectionParameter();
						if (parameters.has("version")) {
							col.parameter.version = parameters.getAsJsonPrimitive("version").getAsInt();
						}
						if (parameters.has("type")) {
							col.parameter.type = CollectionType.valueOf(parameters.getAsJsonPrimitive("type").getAsInt());
						}
						if (parameters.has("cid")) {
							col.parameter.cid = parameters.getAsJsonPrimitive("cid").getAsLong();
						}
						if (parameters.has("deleted")) {
							col.parameter.deleted = parameters.getAsJsonPrimitive("deleted").getAsBoolean();
						}
						if (parameters.has("doCompact")) {
							col.parameter.doCompact = parameters.getAsJsonPrimitive("doCompact").getAsBoolean();
						}
						if (parameters.has("maximalSize")) {
							col.parameter.maximalSize = parameters.getAsJsonPrimitive("maximalSize").getAsLong();
						}
						if (parameters.has("name")) {
							col.parameter.name = parameters.getAsJsonPrimitive("name").getAsString();
						}
						if (parameters.has("isVolatile")) {
							col.parameter.isVolatile = parameters.getAsJsonPrimitive("isVolatile").getAsBoolean();
						}
						if (parameters.has("waitForSync")) {
							col.parameter.waitForSync = parameters.getAsJsonPrimitive("waitForSync").getAsBoolean();
						}
					}
					
					if (elem.has("indexes")) {
						col.indexes = context.deserialize(elem.getAsJsonArray("indexes"), indexesType);
					}
					
					entity.collections.add(col);
				}
			}
			
			if (obj.has("state")) {
				JsonObject s = obj.getAsJsonObject("state");
				entity.state = new State();
				entity.state.running = s.getAsJsonPrimitive("running").getAsBoolean();
				entity.state.lastLogTick = s.getAsJsonPrimitive("lastLogTick").getAsLong();
				entity.state.totalEvents = s.getAsJsonPrimitive("totalEvents").getAsLong();
				String strTime = s.getAsJsonPrimitive("time").getAsString();
				try {
					entity.state.time = DateUtils.parse(strTime, "yyyy-MM-dd'T'HH:mm:ss'Z'");
				} catch (ParseException e) {
					throw new JsonParseException("time format invalid:" + strTime);
				}
			}
			
			if (obj.has("tick")) {
				entity.tick = obj.getAsJsonPrimitive("tick").getAsLong();
			}
			
			return entity;
		}
	}
	
}
