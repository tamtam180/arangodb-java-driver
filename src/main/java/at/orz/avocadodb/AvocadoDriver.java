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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpStatus;

import at.orz.avocadodb.entity.AdminConfigDescriptionEntity;
import at.orz.avocadodb.entity.AdminConfigurationEntity;
import at.orz.avocadodb.entity.AdminLogEntity;
import at.orz.avocadodb.entity.AdminStatusEntity;
import at.orz.avocadodb.entity.BaseEntity;
import at.orz.avocadodb.entity.CollectionEntity;
import at.orz.avocadodb.entity.CollectionsEntity;
import at.orz.avocadodb.entity.CursorEntity;
import at.orz.avocadodb.entity.DefaultEntity;
import at.orz.avocadodb.entity.Direction;
import at.orz.avocadodb.entity.DocumentEntity;
import at.orz.avocadodb.entity.DocumentsEntity;
import at.orz.avocadodb.entity.EdgeEntity;
import at.orz.avocadodb.entity.EdgesEntity;
import at.orz.avocadodb.entity.EntityFactory;
import at.orz.avocadodb.entity.IndexEntity;
import at.orz.avocadodb.entity.IndexType;
import at.orz.avocadodb.entity.IndexesEntity;
import at.orz.avocadodb.entity.KeyValueEntity;
import at.orz.avocadodb.entity.Policy;
import at.orz.avocadodb.entity.Version;
import at.orz.avocadodb.http.HttpManager;
import at.orz.avocadodb.http.HttpResponseEntity;
import at.orz.avocadodb.util.CollectionUtils;
import at.orz.avocadodb.util.DateUtils;
import at.orz.avocadodb.util.MapBuilder;
import at.orz.avocadodb.util.ReflectionUtils;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AvocadoDriver {
	
	// TODO UTF-8 URLEncode
	// TODO Cas Operation as eTAG
	// TODO Should fixed a Double check args.
	// TODO Null check httpResponse.
	
	private AvocadoConfigure configure;
	private HttpManager httpManager;
	private String baseUrl;
	
	public AvocadoDriver(AvocadoConfigure configure) {
		this.configure = configure;
		this.baseUrl = "http://" + configure.host + ":" + configure.clinetPort;
		
		this.httpManager = new HttpManager();
		// TODO Configure そのものを渡す方がよい
		this.httpManager.setDefaultMaxPerRoute(configure.maxPerConnection);
		this.httpManager.setMaxTotal(configure.maxTotalConnection);
		this.httpManager.setProxyHost(configure.proxyHost);
		this.httpManager.setProxyPort(configure.proxyPort);
		
		this.httpManager.init();
	}
	
	public void shutdown() {
		if (httpManager != null) {
			httpManager.destroy();
			httpManager = null;
		}
	}
	
	public Version getVersion() throws AvocadoException {
		HttpResponseEntity res = httpManager.doGet(baseUrl + "/version");
		if (res == null) {
			// TODO
		}
		return createEntityImpl(res, Version.class);
	}

	// ---------------------------------------- start of collection ----------------------------------------
	
	public CollectionEntity createCollection(String name) throws AvocadoException {
		return createCollection(name, null, null);
	}
	
	public CollectionEntity createCollection(String name, Boolean waitForSync, Mode mode) throws AvocadoException {
		try {
			return createCollectionImpl(name, waitForSync);
		} catch (AvocadoException e) {
			if (HttpManager.is400Error(e) && e.getErrorNumber() == 1207) { // Duplicate
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
				if (mode == Mode.DUP_GET) {
					// TODO get Document. 別スレッドから消されているかもしれないので取得できるとは限らない。
					return getCollection(name, Mode.RETURN_NULL);
				}
			}
			throw e;
		}
	}
	
	private CollectionEntity createCollectionImpl(String name, Boolean waitForSync) throws AvocadoException {
		
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/collection", 
				null,
				EntityFactory.toJsonString(new MapBuilder()
					.put("name", name)
					.put("waitForSync", waitForSync)
					.get())
					);
		
		return createEntity(res, CollectionEntity.class);
		
	}
	
	public CollectionEntity getCollection(long id, Mode mode) throws AvocadoException {
		return getCollection(String.valueOf(id), mode);
	}
	public CollectionEntity getCollection(String name, Mode mode) throws AvocadoException {
		validateCollectionName(name);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/collection/" + name,
				null);
		try {
			return createEntity(res, CollectionEntity.class);
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
	}
	
	public CollectionEntity getCollectionProperties(long id, Mode mode) throws AvocadoException {
		return getCollectionProperties(String.valueOf(id), mode);
	}
	public CollectionEntity getCollectionProperties(String name, Mode mode) throws AvocadoException {
		validateCollectionName(name);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/collection/" + name + "/properties",
				null);
		try {
			return createEntity(res, CollectionEntity.class);
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
	}
	
	public CollectionEntity getCollectionCount(long id, Mode mode) throws AvocadoException {
		return getCollectionCount(String.valueOf(id), mode);
	}
	public CollectionEntity getCollectionCount(String name, Mode mode) throws AvocadoException {
		validateCollectionName(name);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/collection/" + name + "/count",
				null);
		try {
			return createEntity(res, CollectionEntity.class);
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}

	}
	
	public CollectionEntity getCollectionFigures(long id, Mode mode) throws AvocadoException {
		return getCollectionFigures(String.valueOf(id), mode);
	}
	public CollectionEntity getCollectionFigures(String name, Mode mode) throws AvocadoException {
		
		validateCollectionName(name);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/collection/" + name + "/figures",
				null);

		try {
			return createEntity(res, CollectionEntity.class);
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}

	}
	
	public CollectionsEntity getCollections() throws AvocadoException {

		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/collection",
				null);
		
		return createEntity(res, CollectionsEntity.class);
		
	}
	
	public CollectionEntity loadCollection(long id, Mode mode) throws AvocadoException {
		return loadCollection(String.valueOf(id), mode);
	}
	public CollectionEntity loadCollection(String name, Mode mode) throws AvocadoException {
		
		validateCollectionName(name);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/collection/" + name + "/load", 
				null, 
				null);
		
		try {
			return createEntity(res, CollectionEntity.class);
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
		
	}

	public CollectionEntity unloadCollection(long id, Mode mode) throws AvocadoException {
		return unloadCollection(String.valueOf(id), mode);
	}
	public CollectionEntity unloadCollection(String name, Mode mode) throws AvocadoException {
		
		validateCollectionName(name);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/collection/" + name + "/unload",
				null, 
				null);
		
		try {
			return createEntity(res, CollectionEntity.class);
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
		
	}
	
	public CollectionEntity truncateCollection(long id, Mode mode) throws AvocadoException {
		return truncateCollection(String.valueOf(id), mode);
	}
	public CollectionEntity truncateCollection(String name, Mode mode) throws AvocadoException {
		
		validateCollectionName(name);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/collection/" + name + "/truncate", 
				null, null);
		
		try {
			return createEntity(res, CollectionEntity.class);
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
		
	}
	
	public CollectionEntity setCollectionProperties(long id, boolean newWaitForSync, Mode mode) throws AvocadoException {
		return setCollectionProperties(String.valueOf(id), newWaitForSync, mode);
	}
	public CollectionEntity setCollectionProperties(String name, boolean newWaitForSync, Mode mode) throws AvocadoException {
		
		validateCollectionName(name);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/collection/" + name + "/properties",
				null,
				EntityFactory.toJsonString(
						new MapBuilder("waitForSync", newWaitForSync).get()
				)
		);
		
		try {
			return createEntity(res, CollectionEntity.class);
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
		
	}
	
	public CollectionEntity renameCollection(long id, String newName, Mode mode) throws AvocadoException {
		return renameCollection(String.valueOf(id), newName, mode);
	}
	public CollectionEntity renameCollection(String name, String newName, Mode mode) throws AvocadoException {
		
		validateCollectionName(newName);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/collection/" + name + "/rename", 
				null,
				EntityFactory.toJsonString(
						new MapBuilder("name", newName).get()
				)
		);
		
		try {
			return createEntity(res, CollectionEntity.class);
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			} else if (HttpManager.is400Error(e) && e.entity.getErrorNumber() == 1207) { // DuplicateError
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
		
	}
	
	public CollectionEntity deleteCollection(long id, Mode mode) throws AvocadoException {
		return deleteCollection(String.valueOf(id), mode);
	}
	public CollectionEntity deleteCollection(String name, Mode mode) throws AvocadoException {
		
		validateCollectionName(name);
		HttpResponseEntity res = httpManager.doDelete(
				baseUrl + "/_api/collection/" + name,
				null);
		
		try {
			return createEntity(res, CollectionEntity.class);
		} catch (AvocadoException e) {
			if (e.getCode() == HttpStatus.SC_NOT_FOUND) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
		
	}
	// ---------------------------------------- end of collection ----------------------------------------

	
	// ---------------------------------------- start of document ----------------------------------------
	
	public DocumentEntity<?> createDocument(long collectionId, Object value, Boolean createCollection, Boolean waitForSync, Mode mode) throws AvocadoException {
		return createDocument(String.valueOf(collectionId), value, createCollection, waitForSync, mode);
	}
	public <T> DocumentEntity<T> createDocument(String collectionName, Object value, Boolean createCollection, Boolean waitForSync, Mode mode) throws AvocadoException {
		
		validateCollectionName(collectionName);
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/document", 
				new MapBuilder()
					.put("collection", collectionName)
					.put("createCollection", (createCollection == null) ? null : createCollection.booleanValue())
					.put("waitForSync", waitForSync == null ? null : waitForSync.booleanValue())
					.get(),
				EntityFactory.toJsonString(value));
		
		try {
			DocumentEntity<T> entity = createEntity(res, DocumentEntity.class);
			return entity;
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
		
	}
	
	public DocumentEntity<?> updateDocument(long collectionId, long documentId, Object value, long rev, Policy policy, Boolean waitForSync, Mode mode) throws AvocadoException {
		return updateDocument(createDocumentHandle(collectionId, documentId), value, rev, policy, waitForSync, mode);
	}
	public DocumentEntity<?> updateDocument(String collectionName, long documentId, Object value, long rev, Policy policy, Boolean waitForSync, Mode mode) throws AvocadoException {
		return updateDocument(createDocumentHandle(collectionName, documentId), value, rev, policy, waitForSync, mode);
	}
	public <T> DocumentEntity<T> updateDocument(String documentHandle, Object value, long rev, Policy policy, Boolean waitForSync, Mode mode) throws AvocadoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/document/" + documentHandle, 
				new MapBuilder()
					.put("rev", rev == -1 ? null : rev)
					.put("waitForSync", waitForSync == null ? null : waitForSync.booleanValue())
					.get(),
				EntityFactory.toJsonString(value));
		
		try {
			DocumentEntity<T> entity = createEntity(res, DocumentEntity.class);
			return entity;
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
		
	}
	
	
	public List<String> getDocuments(long collectionId) throws AvocadoException {
		return getDocuments(String.valueOf(collectionId));
	}
	public List<String> getDocuments(String collectionName) throws AvocadoException {
		
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/document", 
				new MapBuilder("collection", collectionName).get()
				);
		
		DocumentsEntity entity = createEntity(res, DocumentsEntity.class);
		return CollectionUtils.safety(entity.getDocuments());
		
	}
	
	
	public long checkDocument(long collectionId, long documentId) throws AvocadoException {
		return checkDocument(createDocumentHandle(collectionId, documentId));
	}
	public long checkDocument(String collectionName, long documentId) throws AvocadoException {
		return checkDocument(createDocumentHandle(collectionName, documentId));
	}
	public long checkDocument(String documentHandle) throws AvocadoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doHead(
				baseUrl + "/document/" + documentHandle,
				null
				);
		
		DefaultEntity entity = createEntity(res, DefaultEntity.class);
		return entity.getEtag();
		
	}

	public <T> DocumentEntity<T> getDocument(long collectionId, long documentId, Class<T> clazz, Mode mode) throws AvocadoException {
		return getDocument(createDocumentHandle(collectionId, documentId), clazz, mode);
	}
	public <T> DocumentEntity<T> getDocument(String collectionName, long documentId, Class<T> clazz, Mode mode) throws AvocadoException {
		return getDocument(createDocumentHandle(collectionName, documentId), clazz, mode);
	}
	public <T> DocumentEntity<T> getDocument(String documentHandle, Class<T> clazz, Mode mode) throws AvocadoException {
		
		// TODO If-None-Match http-header
		// TODO CAS
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/document/" + documentHandle,
				null);
		
		// TODO Case of StatusCode=304
		
		try {
			T obj = createEntityImpl(res, clazz);
			DocumentEntity<T> entity = createEntity(res, DocumentEntity.class);
			if (entity == null) {
				entity = new DocumentEntity<T>();
			}
			entity.setEntity(obj);
			return entity;
		} catch (AvocadoException e) {
			// TODO 404
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
	}

	public DocumentEntity<?> deleteDocument(long collectionId, long documentId, long rev, Policy policy, Mode mode) throws AvocadoException {
		return deleteDocument(createDocumentHandle(collectionId, documentId), rev, policy, mode);
	}
	public DocumentEntity<?> deleteDocument(String collectionName, long documentId, long rev, Policy policy, Mode mode) throws AvocadoException {
		return deleteDocument(createDocumentHandle(collectionName, documentId), rev, policy, mode);
	}
	public DocumentEntity<?> deleteDocument(String documentHandle, long rev, Policy policy, Mode mode) throws AvocadoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doDelete(
				baseUrl + "/document/" + documentHandle, 
				new MapBuilder()
				.put("rev", rev == -1 ? null : rev)
				.put("policy", policy == null ? null : policy.name().toLowerCase(Locale.US))
				.get());
		
		try {
			DocumentEntity<?> entity = createEntity(res, DocumentEntity.class);
			return entity;
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) {
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			} else if (HttpManager.is412Error(e)) {
				// TODO mode
				return (DocumentEntity<?>) e.entity;
			}
			throw e;
		}
		
	}
	
	// ---------------------------------------- end of document ----------------------------------------
	

	// ---------------------------------------- start of cursor ----------------------------------------

	public CursorEntity<?> validateQuery(String query) throws AvocadoException {
		
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/query", 
				null,
				EntityFactory.toJsonString(new MapBuilder("query", query).get())
				);
		try {
			CursorEntity<?> entity = createEntity(res, CursorEntity.class);
			return entity;
		} catch (AvocadoException e) {
			return (CursorEntity<?>) e.entity;
		}
		
	}
	
	// ※Iteratorで綺麗に何回もRoundtripもしてくれる処理はClientのレイヤーで行う。
	// ※ここでは単純にコールするだけ
	
	// TODO Mode
	public <T> CursorEntity<T> executeQuery(
			String query, Map<String, Object> bindVars,
			Class<T> clazz,
			Boolean calcCount, Integer batchSize) throws AvocadoException {
		
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/cursor", 
				null,
				EntityFactory.toJsonString(
						new MapBuilder()
						.put("query", query)
						.put("bindVars", bindVars == null ? Collections.emptyMap() : bindVars)
						.put("count", calcCount)
						.put("batchSize", batchSize)
						.get())
				);
		try {
			CursorEntity<T> entity = createEntity(res, CursorEntity.class);
			// resultを処理する
			EntityFactory.createResult(entity, clazz);
			return entity;
		} catch (AvocadoException e) {
			// TODO
			throw e;
		}
		
	}
	
	// TODO Mode
	public <T> CursorEntity<T> continueQuery(long cursorId, Class<T> clazz) throws AvocadoException {
		
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/cursor/" + cursorId, 
				null,
				null
				);
		
		try {
			CursorEntity<T> entity = createEntity(res, CursorEntity.class);
			// resultを処理する
			EntityFactory.createResult(entity, clazz);
			return entity;
		} catch (AvocadoException e) {
			// TODO
			throw e;
		}
		
	}
	
	// TODO Mode
	public DefaultEntity finishQuery(long cursorId) throws AvocadoException {
		HttpResponseEntity res = httpManager.doDelete(
				baseUrl + "/_api/cursor/" + cursorId, 
				null
				);
		
		try {
			DefaultEntity entity = createEntity(res, DefaultEntity.class);
			return entity;
		} catch (AvocadoException e) {
			// TODO Mode
			if (e.getErrorNumber() == 500) {
				// 既に削除されている
				return (DefaultEntity) e.entity;
			}
			throw e;
		}
	}
	
	public <T> CursorResultSet<T> executeQueryWithResultSet(
			String query, Map<String, Object> bindVars,
			Class<T> clazz,
			Boolean calcCount, Integer batchSize) throws AvocadoException {
		
		CursorEntity<T> entity = executeQuery(query, bindVars, clazz, calcCount, batchSize);
		CursorResultSet<T> rs = new CursorResultSet<T>(this, clazz, entity);
		return rs;
		
	}
	
	// ---------------------------------------- end of cursor ----------------------------------------

	// ---------------------------------------- start of kvs ----------------------------------------
	
	@Deprecated
	public KeyValueEntity createKeyValue(
			String collectionName, String key, Object value, 
			Map<String, Object> attributes, Date expiredDate,
			Mode mode) throws AvocadoException {
		
		// TODO Sanitize Key
		
		validateCollectionName(collectionName);
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/key/" + collectionName + "/" + key, 
				new MapBuilder()
					.put("x-voc-expires", expiredDate == null ? null : DateUtils.format(expiredDate, "yyyy-MM-dd'T'HH:mm:ss'Z'"))
					.put("x-voc-extended", attributes == null ? null : EntityFactory.toJsonString(attributes))
					.get(),
				null, 
				EntityFactory.toJsonString(value));
		
		try {
			KeyValueEntity entity = createEntity(res, KeyValueEntity.class);
			return entity;
		} catch (AvocadoException e) {
			if (HttpManager.is404Error(e)) { // コレクションが存在しないか、キーが既に存在する。
				if (mode == null || mode == Mode.RETURN_NULL) {
					return null;
				}
			}
			throw e;
		}
		
	}
	
	// ---------------------------------------- end of kvs ----------------------------------------

	
	// ---------------------------------------- start of index ----------------------------------------
	// IndexはModeなしにする。

	public IndexEntity createIndex(long collectionId, IndexType type, boolean unique, String... fields) throws AvocadoException {
		return createIndex(String.valueOf(collectionId), type, unique, fields);
	}
	public IndexEntity createIndex(String collectionName, IndexType type, boolean unique, String... fields) throws AvocadoException {
		
		if (type == IndexType.PRIMARY) {
			throw new IllegalArgumentException("cannot create primary index.");
		}
		if (type == IndexType.CAP) {
			throw new IllegalArgumentException("cannot create cap index. use createCappedIndex.");
		}
		
		validateCollectionName(collectionName);
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/index", 
				new MapBuilder("collection", collectionName).get(),
				EntityFactory.toJsonString(
						new MapBuilder()
						.put("type", type.name().toLowerCase(Locale.US))
						.put("unique", unique)
						.put("fields", fields)
						.get()));
		
		// HTTP:200,201,404
		
		try {
			IndexEntity entity = createEntity(res, IndexEntity.class);
			return entity;
		} catch (AvocadoException e) {
			return null;
		}
		
	}

	public IndexEntity createCappedIndex(long collectionId, int size) throws AvocadoException {
		return createCappedIndex(String.valueOf(collectionId), size);
	}
	public IndexEntity createCappedIndex(String collectionName, int size) throws AvocadoException {
		
		validateCollectionName(collectionName);
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/index", 
				new MapBuilder("collection", collectionName).get(),
				EntityFactory.toJsonString(
						new MapBuilder()
						.put("type", IndexType.CAP.name().toLowerCase(Locale.US))
						.put("size", size)
						.get()));
		
		// HTTP:200,201,404
		
		try {
			IndexEntity entity = createEntity(res, IndexEntity.class);
			return entity;
		} catch (AvocadoException e) {
			return null;
		}
	}
	
	public IndexEntity deleteIndex(String indexHandle) throws AvocadoException {
		
		validateDocumentHandle(indexHandle); // 書式同じなので
		HttpResponseEntity res = httpManager.doDelete(
				baseUrl + "/_api/index/" + indexHandle, 
				null);
		
		try {
			IndexEntity entity = createEntity(res, IndexEntity.class);
			return entity;
		} catch (AvocadoException e) {
			return null;
		}
		
	}

	public IndexEntity getIndex(String indexHandle) throws AvocadoException {
		
		validateDocumentHandle(indexHandle);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/index/" + indexHandle);
		
		try {
			IndexEntity entity = createEntity(res, IndexEntity.class);
			return entity;
		} catch (AvocadoException e) {
			return null;
		}
		
	}

	public IndexesEntity getIndexes(long collectionId) throws AvocadoException {
		return getIndexes(String.valueOf(collectionId));
	}
	public IndexesEntity getIndexes(String collectionName) throws AvocadoException {
		
		validateCollectionName(collectionName);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/index",
				new MapBuilder("collection", collectionName).get());
		
		try {
			IndexesEntity entity = createEntity(res, IndexesEntity.class);
			return entity;
		} catch (AvocadoException e) {
			return null;
		}
		
	}
	
//	public IndexEntity deleteIndexByFields(long collectionId, String... fields) throws AvocadoException {
//	}
//	public IndexEntity deleteIndexByFields(String collectionName, String... fields) throws AvocadoException {
//		
//	}
	
	// ---------------------------------------- end of index ----------------------------------------

	// ---------------------------------------- start of edge ----------------------------------------

	public <T> EdgeEntity<T> createEdge(
			long collectionId, 
			String fromHandle, String toHandle, 
			T attribute) throws AvocadoException {
		return createEdge(String.valueOf(collectionId), fromHandle, toHandle, attribute);
	}
	
	public <T> EdgeEntity<T> createEdge(
			String collectionName, 
			String fromHandle, String toHandle, 
			T attribute) throws AvocadoException {
		
		validateCollectionName(collectionName);
		validateDocumentHandle(fromHandle);
		validateDocumentHandle(toHandle);
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/edge", 
				new MapBuilder()
					.put("collection", collectionName)
					.put("from", fromHandle)
					.put("to", toHandle)
					.get(), 
				EntityFactory.toJsonString(attribute)
				);
		
		try {
			EdgeEntity<T> entity = createEntity(res, EdgeEntity.class);
			return entity;
		} catch (AvocadoException e) {
			return null;
		}
		
	}

	// TODO UpdateEdge
	public <T> EdgeEntity<T> updateEdge(
			String collectionName, 
			String fromHandle, String toHandle, 
			T attribute) throws AvocadoException {
		
		validateCollectionName(collectionName);
		validateDocumentHandle(fromHandle);
		validateDocumentHandle(toHandle);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/edge", 
				new MapBuilder()
					.put("collection", collectionName)
					.put("from", fromHandle)
					.put("to", toHandle)
					.get(), 
				EntityFactory.toJsonString(attribute)
				);
		
		try {
			EdgeEntity<T> entity = createEntity(res, EdgeEntity.class);
			return entity;
		} catch (AvocadoException e) {
			return null;
		}
		
	}
	
	public long checkEdge(String edgeHandle) throws AvocadoException {
		
		validateDocumentHandle(edgeHandle);
		HttpResponseEntity res = httpManager.doHead(
				baseUrl + "/edge/" + edgeHandle,
				null
				);
		
		EdgeEntity<?> entity = createEntity(res, EdgeEntity.class);
		return entity.getEtag();

	}
	
	/**
	 * エッジハンドルを指定して、エッジの情報を取得する。
	 * @param edgeHandle
	 * @param attributeClass
	 * @return
	 * @throws AvocadoException
	 */
	public <T> EdgeEntity<T> getEdge(String edgeHandle, Class<T> attributeClass) throws AvocadoException {
		
		validateDocumentHandle(edgeHandle);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/edge/" + edgeHandle
				);
		
		try {
			EdgeEntity<T> entity = createEntity(res, EdgeEntity.class);
			if (entity != null) {
				T obj = createEntityImpl(res, attributeClass);
				entity.setAttributes(obj);
			}
			return entity;
		} catch (AvocadoException e) {
			return null;
		}
		
	}

	public EdgeEntity<?> deleteEdge(long collectionId, String edgeHandle) throws AvocadoException {
		return deleteEdge(String.valueOf(collectionId), edgeHandle);
	}
	public EdgeEntity<?> deleteEdge(String collectionName, String edgeHandle) throws AvocadoException {
		
		validateDocumentHandle(edgeHandle);
		HttpResponseEntity res = httpManager.doDelete(
				baseUrl + "/edge/" + edgeHandle,
				null);
		
		try {
			EdgeEntity<?> entity = createEntity(res, EdgeEntity.class);
			return entity;
		} catch (AvocadoException e) {
			return null;
		}
		
	}
	
	public <T> EdgesEntity<T> getEdges(String collectionName, String vertexHandle, Direction direction, Class<T> edgeAttributeClass) throws AvocadoException {
		
		validateCollectionName(collectionName);
		validateDocumentHandle(vertexHandle);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/edges/" + collectionName, 
				new MapBuilder()
					.put("vertex", vertexHandle)
					.put("direction", direction.name().toLowerCase(Locale.US))
					.get()
				);
		
		try {
			EdgesEntity<T> entity = EntityFactory.createEdges(res.getText(), edgeAttributeClass);
			validateAndSetStatusCode(res, entity);
			return entity;
		} catch (AvocadoException e) {
			return null;
		}
		
	}
	
	
	// ---------------------------------------- end of edge ----------------------------------------

	
	// ---------------------------------------- start of admin ----------------------------------------

	public AdminLogEntity getServerLog(
			Integer logLevel, Boolean logLevelUpTo,
			Integer start,
			Integer size, Integer offset,
			Boolean sortAsc,
			String text
			) throws AvocadoException {
		
		// パラメータを作る
		MapBuilder param = new MapBuilder();
		if (logLevel != null) {
			if (logLevelUpTo != null && logLevelUpTo.booleanValue()) {
				param.put("upto", logLevel);
			} else {
				param.put("level", logLevel);
			}
		}
		param.put("start", start);
		param.put("size", size);
		param.put("offset", offset);
		if (sortAsc != null) {
			param.put("sort", sortAsc.booleanValue() ?  "asc" : "desc");
		}
		param.put("search", text);
		
		// 実行
		HttpResponseEntity res = httpManager.doGet(baseUrl + "/_admin/log", param.get());
		
		// 結果変換
		try {
			AdminLogEntity entity = createEntity(res, AdminLogEntity.class);
			return entity;
		} catch (AvocadoException e) {
			throw e;
			//return null;
		}
		
	}
	
	public AdminStatusEntity getServerStatus() throws AvocadoException {
		
		HttpResponseEntity res = httpManager.doGet(baseUrl + "/_admin/status");
		
		try {
			return createEntity(res, AdminStatusEntity.class);
		} catch (AvocadoException e) {
			throw e;
		}
		
	}

	public AdminConfigurationEntity getServerConfiguration() throws AvocadoException {
		
		HttpResponseEntity res = httpManager.doGet(baseUrl + "/_admin/config/configuration");
		
		try {
			return createEntity(res, AdminConfigurationEntity.class);
		} catch (AvocadoException e) {
			throw e;
		}
		
	}
	
	public AdminConfigDescriptionEntity getServerConfigurationDescription() throws AvocadoException {
		
		HttpResponseEntity res = httpManager.doGet(baseUrl + "/_admin/config/description");
		return createEntity(res, AdminConfigDescriptionEntity.class);
		
	}
	

	// ---------------------------------------- end of admin ----------------------------------------


	// ---------------------------------------- start of xxx ----------------------------------------

	// ---------------------------------------- end of xxx ----------------------------------------

	
	private String createDocumentHandle(long collectionId, long documentId) {
		// validateCollectionNameは不要
		return collectionId + "/" + documentId;
	}

	private String createDocumentHandle(String collectionName, long documentId) throws AvocadoException {
		validateCollectionName(collectionName);
		return collectionName + "/" + documentId;
	}
	
	private void validateCollectionName(String name) throws AvocadoException {
		if (name.indexOf('/') != -1) {
			throw new AvocadoException("does not allow '/' in name.");
		}
	}
	
	private void validateDocumentHandle(String documentHandle) throws AvocadoException {
		int pos = documentHandle.indexOf('/');
		if (pos > 0) {
			try {
				String collectionName = documentHandle.substring(0, pos);
				validateCollectionName(collectionName);
				long collectionId = Long.parseLong(documentHandle.substring(pos + 1));
				return;
			} catch (Exception e) {
			}
		}
		throw new AvocadoException("invalid format documentHandle:" + documentHandle);
	}
	
	/**
	 * HTTPレスポンスから指定した型へ変換する。
	 * レスポンスがエラーであるかを確認して、エラーの場合は例外を投げる。
	 * @param res
	 * @param type
	 * @return
	 * @throws AvocadoException
	 */
	private <T extends BaseEntity> T createEntity(HttpResponseEntity res, Class<T> clazz) throws AvocadoException {
		T entity = createEntityImpl(res, clazz);
		if (entity == null) {
			entity = ReflectionUtils.newInstance(clazz);
		}
		validateAndSetStatusCode(res, entity);
		return entity;
	}
	private void validateAndSetStatusCode(HttpResponseEntity res, BaseEntity entity) throws AvocadoException {
		if (entity != null) {
			if (res.getEtag() > 0) {
				entity.setEtag(res.getEtag());
			}
			entity.setStatusCode(res.getStatusCode());
			if (entity.isError()) {
				throw new AvocadoException(entity);
			}
		}
	}
	private <T> T createEntityImpl(HttpResponseEntity res, Class<T> clazz) throws AvocadoException {
		T entity = EntityFactory.createEntity(res.getText(), clazz);
		return entity;
	}

	public static enum Mode {
		RETURN_NULL,
		RETURN_ERROR_ENTITY,
		RAISE_ERROR,
		DUP_GET,
		CREATE
	}
	
}
