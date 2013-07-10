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

package at.orz.arangodb;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import at.orz.arangodb.entity.AdminLogEntity;
import at.orz.arangodb.entity.AdminStatusEntity;
import at.orz.arangodb.entity.ArangoUnixTime;
import at.orz.arangodb.entity.ArangoVersion;
import at.orz.arangodb.entity.CollectionEntity;
import at.orz.arangodb.entity.CollectionType;
import at.orz.arangodb.entity.CollectionsEntity;
import at.orz.arangodb.entity.ConnectionStatisticsEntity;
import at.orz.arangodb.entity.CursorEntity;
import at.orz.arangodb.entity.DefaultEntity;
import at.orz.arangodb.entity.Direction;
import at.orz.arangodb.entity.DocumentEntity;
import at.orz.arangodb.entity.EdgeEntity;
import at.orz.arangodb.entity.EdgesEntity;
import at.orz.arangodb.entity.ExplainEntity;
import at.orz.arangodb.entity.Granularity;
import at.orz.arangodb.entity.ImportResultEntity;
import at.orz.arangodb.entity.IndexEntity;
import at.orz.arangodb.entity.IndexType;
import at.orz.arangodb.entity.IndexesEntity;
import at.orz.arangodb.entity.KeyValueEntity;
import at.orz.arangodb.entity.Policy;
import at.orz.arangodb.entity.ScalarExampleEntity;
import at.orz.arangodb.entity.SimpleByResultEntity;
import at.orz.arangodb.entity.UserEntity;
import at.orz.arangodb.http.HttpManager;
import at.orz.arangodb.impl.ImplFactory;
import at.orz.arangodb.impl.InternalAdminDriverImpl;
import at.orz.arangodb.impl.InternalCollectionDriverImpl;
import at.orz.arangodb.impl.InternalCursorDriverImpl;
import at.orz.arangodb.impl.InternalDocumentDriverImpl;
import at.orz.arangodb.impl.InternalEdgeDriverImpl;
import at.orz.arangodb.impl.InternalImportDriverImpl;
import at.orz.arangodb.impl.InternalIndexDriverImpl;
import at.orz.arangodb.impl.InternalKVSDriverImpl;
import at.orz.arangodb.impl.InternalSimpleDriverImpl;
import at.orz.arangodb.impl.InternalUsersDriverImpl;
import at.orz.arangodb.util.ResultSetUtils;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriver extends BaseArangoDriver {
	
	// TODO UTF-8 URLEncode
	// TODO Cas Operation as eTAG
	// TODO Should fixed a Double check args.
	// TODO Null check httpResponse.
	
	private ArangoConfigure configure;
	private HttpManager httpManager;
	private String baseUrl;
	
	private InternalCursorDriverImpl cursorDriver;
	private InternalCollectionDriverImpl collectionDriver;
	private InternalDocumentDriverImpl documentDriver;
	private InternalKVSDriverImpl kvsDriver;
	private InternalIndexDriverImpl indexDriver;
	private InternalEdgeDriverImpl edgeDriver;
	private InternalAdminDriverImpl adminDriver;
	private InternalSimpleDriverImpl simpleDriver;
	private InternalUsersDriverImpl usersDriver;
	private InternalImportDriverImpl importDriver;
	
	public ArangoDriver(ArangoConfigure configure) {
		this.configure = configure;
		this.httpManager = configure.getHttpManager();
		this.baseUrl = configure.getBaseUrl();
		
		this.cursorDriver = ImplFactory.createCursorDriver(configure);
		this.collectionDriver = ImplFactory.createCollectionDriver(configure);
		this.documentDriver = ImplFactory.createDocumentDriver(configure);
		this.kvsDriver = ImplFactory.createKVSDriver(configure);
		this.indexDriver = ImplFactory.createIndexDriver(configure, cursorDriver);
		this.edgeDriver = ImplFactory.createEdgeDriver(configure);
		this.adminDriver = ImplFactory.createAdminDriver(configure);
		this.simpleDriver = ImplFactory.createSimpleDriver(configure, cursorDriver);
		this.usersDriver = ImplFactory.createUsersDriver(configure);
		this.importDriver = ImplFactory.createImportDriver(configure);
	}
	
	// ---------------------------------------- start of collection ----------------------------------------
	
	public CollectionEntity createCollection(String name) throws ArangoException {
		return collectionDriver.createCollection(name, null, null, null, null, null, null);
	}

	public CollectionEntity createCollection(String name, Boolean waitForSync,
			Integer journalSize, Boolean isSystem, Boolean isVolatile, CollectionType type) throws ArangoException {
		return collectionDriver.createCollection(name, waitForSync, journalSize, isSystem, isVolatile, type, null);
	}

	public CollectionEntity createCollection(String name, Boolean waitForSync,
			Integer journalSize, Boolean isSystem, Boolean isVolatile, CollectionType type, Map<String, Object> createOptions) throws ArangoException {
		return collectionDriver.createCollection(name, waitForSync, journalSize, isSystem, isVolatile, type, createOptions);
	}
	
	public CollectionEntity getCollection(long id) throws ArangoException {
		return getCollection(String.valueOf(id));
	}
	public CollectionEntity getCollection(String name) throws ArangoException {
		return collectionDriver.getCollection(name);
	}
	
	public CollectionEntity getCollectionProperties(long id) throws ArangoException {
		return getCollectionProperties(String.valueOf(id));
	}
	public CollectionEntity getCollectionProperties(String name) throws ArangoException {
		return collectionDriver.getCollectionProperties(name);
	}

	public CollectionEntity getCollectionRevision(long id) throws ArangoException {
		return getCollectionRevision(String.valueOf(id));
	}

	public CollectionEntity getCollectionRevision(String name) throws ArangoException {
		return collectionDriver.getCollectionRevision(name);
	}

	public CollectionEntity getCollectionCount(long id) throws ArangoException {
		return getCollectionCount(String.valueOf(id));
	}
	public CollectionEntity getCollectionCount(String name) throws ArangoException {
		return collectionDriver.getCollectionCount(name);
	}
	
	public CollectionEntity getCollectionFigures(long id) throws ArangoException {
		return getCollectionFigures(String.valueOf(id));
	}
	public CollectionEntity getCollectionFigures(String name) throws ArangoException {
		return collectionDriver.getCollectionFigures(name);
	}

	public CollectionsEntity getCollections() throws ArangoException {
		return collectionDriver.getCollections(null);
	}

	public CollectionsEntity getCollections(Boolean excludeSystem) throws ArangoException {
		return collectionDriver.getCollections(excludeSystem);
	}
	
	public CollectionEntity loadCollection(long id) throws ArangoException {
		return collectionDriver.loadCollection(String.valueOf(id), null);
	}
	public CollectionEntity loadCollection(String name) throws ArangoException {
		return collectionDriver.loadCollection(name, null);
	}
	public CollectionEntity loadCollection(long id, Boolean count) throws ArangoException {
		return collectionDriver.loadCollection(String.valueOf(id), count);
	}
	public CollectionEntity loadCollection(String name, Boolean count) throws ArangoException {
		return collectionDriver.loadCollection(name, count);
	}

	public CollectionEntity unloadCollection(long id) throws ArangoException {
		return unloadCollection(String.valueOf(id));
	}
	public CollectionEntity unloadCollection(String name) throws ArangoException {
		return collectionDriver.unloadCollection(name);
	}
	
	public CollectionEntity truncateCollection(long id) throws ArangoException {
		return truncateCollection(String.valueOf(id));
	}
	public CollectionEntity truncateCollection(String name) throws ArangoException {
		return collectionDriver.truncateCollection(name);
	}
	
	public CollectionEntity setCollectionProperties(long id, Boolean newWaitForSync, Long journalSize) throws ArangoException {
		return collectionDriver.setCollectionProperties(String.valueOf(id), newWaitForSync, journalSize);
	}
	public CollectionEntity setCollectionProperties(String name, Boolean newWaitForSync, Long journalSize) throws ArangoException {
		return collectionDriver.setCollectionProperties(name, newWaitForSync, journalSize);
	}
	
	public CollectionEntity renameCollection(long id, String newName) throws ArangoException {
		return renameCollection(String.valueOf(id), newName);
	}
	public CollectionEntity renameCollection(String name, String newName) throws ArangoException {
		return collectionDriver.renameCollection(name, newName);
	}
	
	public CollectionEntity deleteCollection(long id) throws ArangoException {
		return deleteCollection(String.valueOf(id));
	}
	public CollectionEntity deleteCollection(String name) throws ArangoException {
		return collectionDriver.deleteCollection(name);
	}
	
	// ---------------------------------------- end of collection ----------------------------------------

	
	// ---------------------------------------- start of document ----------------------------------------
	
	public DocumentEntity<?> createDocument(long collectionId, Object value, Boolean createCollection, Boolean waitForSync) throws ArangoException {
		return createDocument(String.valueOf(collectionId), value, createCollection, waitForSync);
	}
	public <T> DocumentEntity<T> createDocument(String collectionName, Object value, Boolean createCollection, Boolean waitForSync) throws ArangoException {
		return documentDriver.createDocument(collectionName, value, createCollection, waitForSync);
	}
	
	public DocumentEntity<?> updateDocument(long collectionId, long documentId, Object value, long rev, Policy policy, Boolean waitForSync) throws ArangoException {
		return updateDocument(createDocumentHandle(collectionId, documentId), value, rev, policy, waitForSync);
	}
	public DocumentEntity<?> updateDocument(String collectionName, long documentId, Object value, long rev, Policy policy, Boolean waitForSync) throws ArangoException {
		return updateDocument(createDocumentHandle(collectionName, documentId), value, rev, policy, waitForSync);
	}
	public <T> DocumentEntity<T> updateDocument(String documentHandle, Object value, long rev, Policy policy, Boolean waitForSync) throws ArangoException {
		return documentDriver.updateDocument(documentHandle, value, rev, policy, waitForSync);
	}

	public DocumentEntity<?> updateDocument(long collectionId, long documentId, Object value, long rev, Policy policy, Boolean waitForSync, Boolean keepNull) throws ArangoException {
		return partialUpdateDocument(createDocumentHandle(collectionId, documentId), value, rev, policy, waitForSync, keepNull);
	}
	public DocumentEntity<?> updateDocument(String collectionName, long documentId, Object value, long rev, Policy policy, Boolean waitForSync, Boolean keepNull) throws ArangoException {
		return partialUpdateDocument(createDocumentHandle(collectionName, documentId), value, rev, policy, waitForSync, keepNull);
	}
	public <T> DocumentEntity<T> partialUpdateDocument(String documentHandle, Object value, long rev, Policy policy, Boolean waitForSync, Boolean keepNull) throws ArangoException {
		return documentDriver.partialUpdateDocument(documentHandle, value, rev, policy, waitForSync, keepNull);
	}
	
	public List<String> getDocuments(long collectionId) throws ArangoException {
		return getDocuments(String.valueOf(collectionId));
	}
	public List<String> getDocuments(String collectionName) throws ArangoException {
		return documentDriver.getDocuments(collectionName);
	}
	
	public long checkDocument(long collectionId, long documentId) throws ArangoException {
		return checkDocument(createDocumentHandle(collectionId, documentId));
	}
	public long checkDocument(String collectionName, long documentId) throws ArangoException {
		return checkDocument(createDocumentHandle(collectionName, documentId));
	}
	public long checkDocument(String documentHandle) throws ArangoException {
		return documentDriver.checkDocument(documentHandle);
	}

	public <T> DocumentEntity<T> getDocument(long collectionId, long documentId, Class<T> clazz) throws ArangoException {
		return getDocument(createDocumentHandle(collectionId, documentId), clazz);
	}
	public <T> DocumentEntity<T> getDocument(String collectionName, long documentId, Class<T> clazz) throws ArangoException {
		return getDocument(createDocumentHandle(collectionName, documentId), clazz);
	}
	public <T> DocumentEntity<T> getDocument(String documentHandle, Class<T> clazz) throws ArangoException {
		return documentDriver.getDocument(documentHandle, clazz);
	}

	public DocumentEntity<?> deleteDocument(long collectionId, long documentId, long rev, Policy policy) throws ArangoException {
		return deleteDocument(createDocumentHandle(collectionId, documentId), rev, policy);
	}
	public DocumentEntity<?> deleteDocument(String collectionName, long documentId, long rev, Policy policy) throws ArangoException {
		return deleteDocument(createDocumentHandle(collectionName, documentId), rev, policy);
	}
	public DocumentEntity<?> deleteDocument(String documentHandle, long rev, Policy policy) throws ArangoException {
		return documentDriver.deleteDocument(documentHandle, rev, policy);
	}
	
	// ---------------------------------------- end of document ----------------------------------------
	

	// ---------------------------------------- start of cursor ----------------------------------------

	public CursorEntity<?> validateQuery(String query) throws ArangoException {
		return cursorDriver.validateQuery(query);
	}
	
	public ExplainEntity explainQuery(String query, Map<String, Object> bindVars) throws ArangoException {
		return cursorDriver.explainQuery(query, bindVars);
	}
	
	public <T> CursorEntity<T> executeQuery(
			String query, Map<String, Object> bindVars,
			Class<T> clazz,
			Boolean calcCount, Integer batchSize) throws ArangoException {
		
		return cursorDriver.executeQuery(query, bindVars, clazz, calcCount, batchSize);
		
	}
	
	public <T> CursorEntity<T> continueQuery(long cursorId, Class<T> clazz) throws ArangoException {
		return cursorDriver.continueQuery(cursorId, clazz);
	}
	
	public DefaultEntity finishQuery(long cursorId) throws ArangoException {
		return cursorDriver.finishQuery(cursorId);
	}
	
	public <T> CursorResultSet<T> executeQueryWithResultSet(
			String query, Map<String, Object> bindVars,
			Class<T> clazz,
			Boolean calcCount, Integer batchSize) throws ArangoException {
		return cursorDriver.executeQueryWithResultSet(query, bindVars, clazz, calcCount, batchSize);
	}
	
	// ---------------------------------------- end of cursor ----------------------------------------

	// ---------------------------------------- start of kvs ----------------------------------------
	
	public KeyValueEntity createKeyValue(
			String collectionName, String key, Object value, 
			Map<String, Object> attributes, Date expiredDate
			) throws ArangoException {
		return kvsDriver.createKeyValue(collectionName, key, value, attributes, expiredDate);
	}
	
	public KeyValueEntity updateKeyValue(
			String collectionName, String key, Object value, 
			Map<String, Object> attributes, Date expiredDate,
			boolean create
			) throws ArangoException {
		return kvsDriver.updateKeyValue(collectionName, key, value, attributes, expiredDate, create);
	}
	
	// TODO 全部実装されていないので実装する。ただ、1.1.1の段階ではドキュメントが工事中なんだが。
	
	// ---------------------------------------- end of kvs ----------------------------------------

	
	// ---------------------------------------- start of index ----------------------------------------

	public IndexEntity createIndex(long collectionId, IndexType type, boolean unique, String... fields) throws ArangoException {
		return createIndex(String.valueOf(collectionId), type, unique, fields);
	}
	public IndexEntity createIndex(String collectionName, IndexType type, boolean unique, String... fields) throws ArangoException {
		return indexDriver.createIndex(collectionName, type, unique, fields);
	}

	public IndexEntity createCappedIndex(long collectionId, int size) throws ArangoException {
		return createCappedIndex(String.valueOf(collectionId), size);
	}
	public IndexEntity createCappedIndex(String collectionName, int size) throws ArangoException {
		return indexDriver.createCappedIndex(collectionName, size);
	}

	public IndexEntity createFulltextIndex(long collectionId, String... fields) throws ArangoException {
		return createFulltextIndex(String.valueOf(collectionId), null, fields);
	}
	public IndexEntity createFulltextIndex(String collectionName, String... fields) throws ArangoException {
		return createFulltextIndex(collectionName, null, fields);
	}
	public IndexEntity createFulltextIndex(long collectionId, Integer minLength, String... fields) throws ArangoException {
		return createFulltextIndex(String.valueOf(collectionId), minLength, fields);
	}
	public IndexEntity createFulltextIndex(String collectionName, Integer minLength, String... fields) throws ArangoException {
		return indexDriver.createFulltextIndex(collectionName, minLength, fields);
	}
	
	public IndexEntity deleteIndex(String indexHandle) throws ArangoException {
		return indexDriver.deleteIndex(indexHandle);
	}

	public IndexEntity getIndex(String indexHandle) throws ArangoException {
		return indexDriver.getIndex(indexHandle);
	}

	public IndexesEntity getIndexes(long collectionId) throws ArangoException {
		return getIndexes(String.valueOf(collectionId));
	}
	public IndexesEntity getIndexes(String collectionName) throws ArangoException {
		return indexDriver.getIndexes(collectionName);
	}
	
//	public IndexEntity deleteIndexByFields(long collectionId, String... fields) throws ArangoException {
//	}
//	public IndexEntity deleteIndexByFields(String collectionName, String... fields) throws ArangoException {
//		
//	}
	
	// ---------------------------------------- end of index ----------------------------------------

	// ---------------------------------------- start of edge ----------------------------------------

	public <T> EdgeEntity<T> createEdge(
			long collectionId, 
			String fromHandle, String toHandle, 
			T attribute) throws ArangoException {
		return createEdge(String.valueOf(collectionId), fromHandle, toHandle, attribute);
	}
	
	public <T> EdgeEntity<T> createEdge(
			String collectionName, 
			String fromHandle, String toHandle, 
			T attribute) throws ArangoException {
		
		return edgeDriver.createEdge(collectionName, fromHandle, toHandle, attribute);
	}

	// TODO UpdateEdge
	public <T> EdgeEntity<T> updateEdge(
			String collectionName, 
			String fromHandle, String toHandle, 
			T attribute) throws ArangoException {
		return edgeDriver.updateEdge(collectionName, fromHandle, toHandle, attribute);
	}
	
	public long checkEdge(String edgeHandle) throws ArangoException {
		return edgeDriver.checkEdge(edgeHandle);
	}
	
	/**
	 * エッジハンドルを指定して、エッジの情報を取得する。
	 * @param edgeHandle
	 * @param attributeClass
	 * @return
	 * @throws ArangoException
	 */
	public <T> EdgeEntity<T> getEdge(String edgeHandle, Class<T> attributeClass) throws ArangoException {
		return edgeDriver.getEdge(edgeHandle, attributeClass);
	}

	public EdgeEntity<?> deleteEdge(long collectionId, String edgeHandle) throws ArangoException {
		return deleteEdge(String.valueOf(collectionId), edgeHandle);
	}
	public EdgeEntity<?> deleteEdge(String collectionName, String edgeHandle) throws ArangoException {
		return edgeDriver.deleteEdge(collectionName, edgeHandle);
	}
	
	public <T> EdgesEntity<T> getEdges(String collectionName, String vertexHandle, Direction direction, Class<T> edgeAttributeClass) throws ArangoException {
		return edgeDriver.getEdges(collectionName, vertexHandle, direction, edgeAttributeClass);
	}
	
	
	// ---------------------------------------- end of edge ----------------------------------------

	
	// ---------------------------------------- start of admin ----------------------------------------

	public AdminLogEntity getServerLog(
			Integer logLevel, Boolean logLevelUpTo,
			Integer start,
			Integer size, Integer offset,
			Boolean sortAsc,
			String text
			) throws ArangoException {
		return adminDriver.getServerLog(logLevel, logLevelUpTo, start, size, offset, sortAsc, text);
	}
	
	public AdminStatusEntity getServerStatus() throws ArangoException {
		return adminDriver.getServerStatus();
	}
	
	public ConnectionStatisticsEntity getConnectionStatistics(Granularity granularity, Integer length, String... figures) throws ArangoException {
		return adminDriver.getConnectionStatistics(granularity, length, figures);
	}

	public ArangoVersion getVersion() throws ArangoException {
		return adminDriver.getVersion();
	}

	public ArangoUnixTime getTime() throws ArangoException {
		return adminDriver.getTime();
	}
	
	public DefaultEntity flushModules() throws ArangoException {
		return adminDriver.flushModules();
	}

	public DefaultEntity reloadRouting() throws ArangoException {
		return adminDriver.reloadRouting();
	}
	
	// ---------------------------------------- end of admin ----------------------------------------


	// ---------------------------------------- start of simple ----------------------------------------
	
	public <T> CursorEntity<T> executeSimpleByExample(String collectionName, Map<String, Object> example, int skip, int limit, Class<T> clazz) throws ArangoException {
		return simpleDriver.executeSimpleByExample(collectionName, example, skip, limit, clazz);
	}

	public <T> CursorResultSet<T> executeSimpleByExampleWithResusltSet(String collectionName, Map<String, Object> example, int skip, int limit, Class<T> clazz) throws ArangoException {
		return simpleDriver.executeSimpleByExampleWithResultSet(collectionName, example, skip, limit, clazz);
	}

	public <T> CursorEntity<DocumentEntity<T>> executeSimpleByExampleWithDocument(String collectionName, Map<String, Object> example, int skip, int limit, Class<T> clazz) throws ArangoException {
		return simpleDriver.executeSimpleByExampleWithDocument(collectionName, example, skip, limit, clazz);
	}

	public <T> CursorResultSet<DocumentEntity<T>> executeSimpleByExampleWithDocumentResusltSet(String collectionName, Map<String, Object> example, int skip, int limit, Class<T> clazz) throws ArangoException {
		return simpleDriver.executeSimpleByExampleWithDocumentResultSet(collectionName, example, skip, limit, clazz);
	}
	
	public <T> CursorEntity<T> executeSimpleAll(String collectionName, int skip, int limit, Class<T> clazz) throws ArangoException {
		return simpleDriver.executeSimpleAll(collectionName, skip, limit, clazz);
	}

	public <T> CursorResultSet<T> executeSimpleAllWithResultSet(String collectionName, int skip, int limit, Class<T> clazz) throws ArangoException {
		return simpleDriver.executeSimpleAllWithResultSet(collectionName, skip, limit, clazz);
	}

	public <T> CursorEntity<DocumentEntity<T>> executeSimpleAllWithDocument(String collectionName, int skip, int limit, Class<T> clazz) throws ArangoException {
		return simpleDriver.executeSimpleAllWithDocument(collectionName, skip, limit, clazz);
	}

	public <T> CursorResultSet<DocumentEntity<T>> executeSimpleAllWithDocumentResultSet(String collectionName, int skip, int limit, Class<T> clazz) throws ArangoException {
		return simpleDriver.executeSimpleAllWithDocumentResultSet(collectionName, skip, limit, clazz);
	}

	public <T> ScalarExampleEntity<T> executeSimpleFirstExample(String collectionName, Map<String, Object> example, Class<T> clazz) throws ArangoException {
		return simpleDriver.executeSimpleFirstExample(collectionName, example, clazz);
	}
	
	public <T> ScalarExampleEntity<T> executeSimpleAny(String collectionName, Class<T> clazz) throws ArangoException {
		return simpleDriver.executeSimpleAny(collectionName, clazz);
	}
	
	public <T> CursorEntity<T> executeSimpleRange(
			String collectionName,
			String attribute,
			Object left, Object right, Boolean closed,
			int skip, int limit,
			Class<T> clazz
			) throws ArangoException {
		return simpleDriver.executeSimpleRange(collectionName, attribute, left, right, closed, skip, limit, clazz);
	}
	
	public <T> CursorResultSet<T> executeSimpleRangeWithResultSet(
			String collectionName,
			String attribute,
			Object left, Object right, Boolean closed,
			int skip, int limit,
			Class<T> clazz
			) throws ArangoException {
		return simpleDriver.executeSimpleRangeWithResultSet(collectionName, attribute, left, right, closed, skip, limit, clazz);
	}

	public <T> CursorEntity<DocumentEntity<T>> executeSimpleRangeWithDocument(
			String collectionName,
			String attribute,
			Object left, Object right, Boolean closed,
			int skip, int limit,
			Class<T> clazz
			) throws ArangoException {
		return simpleDriver.executeSimpleRangeWithDocument(collectionName, attribute, left, right, closed, skip, limit, clazz);
	}
	
	public <T> CursorResultSet<DocumentEntity<T>> executeSimpleRangeWithDocumentResultSet(
			String collectionName,
			String attribute,
			Object left, Object right, Boolean closed,
			int skip, int limit,
			Class<T> clazz
			) throws ArangoException {
		return simpleDriver.executeSimpleRangeWithDocumentResultSet(collectionName, attribute, left, right, closed, skip, limit, clazz);
	}
	
	public <T> CursorEntity<T> executeSimpleFulltext(
			String collectionName,
			String attribute, String query, 
			int skip, int limit,
			String index,
			Class<T> clazz
			) throws ArangoException {
		return simpleDriver.executeSimpleFulltext(collectionName, attribute, query, skip, limit, index, clazz);
	}
	
	public <T> CursorResultSet<T> executeSimpleFulltextWithResultSet(
			String collectionName,
			String attribute, String query, 
			int skip, int limit,
			String index,
			Class<T> clazz
			) throws ArangoException {
		return simpleDriver.executeSimpleFulltextWithResultSet(collectionName, attribute, query, skip, limit, index, clazz);
	}

	public <T> CursorEntity<DocumentEntity<T>> executeSimpleFulltextWithDocument(
			String collectionName,
			String attribute, String query, 
			int skip, int limit,
			String index,
			Class<T> clazz
			) throws ArangoException {
		return simpleDriver.executeSimpleFulltextWithDocument(collectionName, attribute, query, skip, limit, index, clazz);
	}
	
	public <T> CursorResultSet<DocumentEntity<T>> executeSimpleFulltextWithDocumentResultSet(
			String collectionName,
			String attribute, String query, 
			int skip, int limit,
			String index,
			Class<T> clazz
			) throws ArangoException {
		return simpleDriver.executeSimpleFulltextWithDocumentResultSet(collectionName, attribute, query, skip, limit, index, clazz);
	}
	
	public SimpleByResultEntity executeSimpleRemoveByExample(
			String collectionName,
			Map<String, Object> example,
			Boolean waitForSync,
			Integer limit) throws ArangoException {
		return simpleDriver.executeSimpleRemoveByExample(collectionName, example, waitForSync, limit);
	}
	
	public SimpleByResultEntity executeSimpleReplaceByExample(
			String collectionName,
			Map<String, Object> example,
			Map<String, Object> newValue,
			Boolean waitForSync,
			Integer limit) throws ArangoException {
		return simpleDriver.executeSimpleReplaceByExample(collectionName, example, newValue, waitForSync, limit);
	}

	public SimpleByResultEntity executeSimpleUpdateByExample(
			String collectionName,
			Map<String, Object> example,
			Map<String, Object> newValue,
			Boolean keepNull,
			Boolean waitForSync,
			Integer limit) throws ArangoException {
		return simpleDriver.executeSimpleUpdateByExample(collectionName, example, newValue, keepNull, waitForSync, limit);
	}
	
	// ---------------------------------------- end of simple ----------------------------------------

	// ---------------------------------------- start of users ----------------------------------------
	
	public DefaultEntity createUser(String username, String passwd, Boolean active, Map<String, Object> extra) throws ArangoException {
		return usersDriver.createUser(username, passwd, active, extra);
	}
	
	public DefaultEntity replaceUser(String username, String passwd, Boolean active, Map<String, Object> extra) throws ArangoException {
		return usersDriver.replaceUser(username, passwd, active, extra);
	}
	
	public DefaultEntity updateUser(String username, String passwd, Boolean active, Map<String, Object> extra) throws ArangoException {
		return usersDriver.updateUser(username, passwd, active, extra);
	}
	
	public DefaultEntity deleteUser(String username) throws ArangoException {
		return usersDriver.deleteUser(username);
	}
	
	public UserEntity getUser(String username) throws ArangoException {
		return usersDriver.getUser(username);
	}
	
	// Original (ArangoDB does not implements this API)
	public List<DocumentEntity<UserEntity>> getUsersDocument() throws ArangoException {
		
		CursorResultSet<DocumentEntity<UserEntity>> rs = executeSimpleAllWithDocumentResultSet("_users", 0, 0, UserEntity.class);
		return ResultSetUtils.toList(rs);
		
	}
	
	// Original (ArangoDB does not implements this API)
	public List<UserEntity> getUsers() throws ArangoException {
		
		CursorResultSet<UserEntity> rs = executeSimpleAllWithResultSet("_users", 0, 0, UserEntity.class);
		return ResultSetUtils.toList(rs);
		
	}

	// ---------------------------------------- end of users ----------------------------------------

	// ---------------------------------------- start of import ----------------------------------------
	
	public ImportResultEntity importDocuments(String collection, Boolean createCollection, Collection<?> values) throws ArangoException {
		return importDriver.importDocuments(collection, createCollection, values);
	}
	
//	public void importDocuments(String collection, Boolean createCollection, Iterator<?> itr) throws ArangoException {
//		importDriver.importDocuments(collection, createCollection, itr);
//	}

	public ImportResultEntity importDocumentsByHeaderValues(String collection, Boolean createCollection, Collection<? extends Collection<?>> headerValues) throws ArangoException {
		return importDriver.importDocumentsByHeaderValues(collection, createCollection, headerValues);
	}

	// ---------------------------------------- end of import ----------------------------------------

	
	// ---------------------------------------- start of xxx ----------------------------------------

	// ---------------------------------------- end of xxx ----------------------------------------

}
