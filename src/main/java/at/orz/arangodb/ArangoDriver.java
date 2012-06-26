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

import java.util.Date;
import java.util.List;
import java.util.Map;

import at.orz.arangodb.entity.AdminConfigDescriptionEntity;
import at.orz.arangodb.entity.AdminConfigurationEntity;
import at.orz.arangodb.entity.AdminLogEntity;
import at.orz.arangodb.entity.AdminStatusEntity;
import at.orz.arangodb.entity.CollectionEntity;
import at.orz.arangodb.entity.CollectionsEntity;
import at.orz.arangodb.entity.CursorEntity;
import at.orz.arangodb.entity.DefaultEntity;
import at.orz.arangodb.entity.Direction;
import at.orz.arangodb.entity.DocumentEntity;
import at.orz.arangodb.entity.EdgeEntity;
import at.orz.arangodb.entity.EdgesEntity;
import at.orz.arangodb.entity.IndexEntity;
import at.orz.arangodb.entity.IndexType;
import at.orz.arangodb.entity.IndexesEntity;
import at.orz.arangodb.entity.KeyValueEntity;
import at.orz.arangodb.entity.Policy;
import at.orz.arangodb.entity.V8Version;
import at.orz.arangodb.http.HttpManager;
import at.orz.arangodb.http.HttpResponseEntity;
import at.orz.arangodb.impl.ImplFactory;
import at.orz.arangodb.impl.InternalAdminDriverImpl;
import at.orz.arangodb.impl.InternalCollectionDriverImpl;
import at.orz.arangodb.impl.InternalCursorDriverImpl;
import at.orz.arangodb.impl.InternalDocumentDriverImpl;
import at.orz.arangodb.impl.InternalEdgeDriverImpl;
import at.orz.arangodb.impl.InternalIndexDriverImpl;
import at.orz.arangodb.impl.InternalKVSDriverImpl;

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
	}
	
	public V8Version getVersion() throws ArangoException {
		return adminDriver.getVersion();
	}

	// ---------------------------------------- start of collection ----------------------------------------
	
	public CollectionEntity createCollection(String name) throws ArangoException {
		return collectionDriver.createCollection(name);
	}
	
	public CollectionEntity createCollection(String name, Boolean waitForSync, Mode mode) throws ArangoException {
		return collectionDriver.createCollection(name, waitForSync, mode);
	}
	
	public CollectionEntity getCollection(long id, Mode mode) throws ArangoException {
		return collectionDriver.getCollection(id, mode);
	}
	public CollectionEntity getCollection(String name, Mode mode) throws ArangoException {
		return collectionDriver.getCollection(name, mode);
	}
	
	public CollectionEntity getCollectionProperties(long id, Mode mode) throws ArangoException {
		return collectionDriver.getCollectionProperties(id, mode);
	}
	public CollectionEntity getCollectionProperties(String name, Mode mode) throws ArangoException {
		return collectionDriver.getCollectionProperties(name, mode);
	}
	
	public CollectionEntity getCollectionCount(long id, Mode mode) throws ArangoException {
		return collectionDriver.getCollectionCount(id, mode);
	}
	public CollectionEntity getCollectionCount(String name, Mode mode) throws ArangoException {
		return collectionDriver.getCollectionCount(name, mode);
	}
	
	public CollectionEntity getCollectionFigures(long id, Mode mode) throws ArangoException {
		return collectionDriver.getCollectionFigures(id, mode);
	}
	public CollectionEntity getCollectionFigures(String name, Mode mode) throws ArangoException {
		return collectionDriver.getCollectionFigures(name, mode);
	}
	
	public CollectionsEntity getCollections() throws ArangoException {
		return collectionDriver.getCollections();
	}
	
	public CollectionEntity loadCollection(long id, Mode mode) throws ArangoException {
		return collectionDriver.loadCollection(id, mode);
	}
	public CollectionEntity loadCollection(String name, Mode mode) throws ArangoException {
		return collectionDriver.loadCollection(name, mode);
	}

	public CollectionEntity unloadCollection(long id, Mode mode) throws ArangoException {
		return collectionDriver.unloadCollection(id, mode);
	}
	public CollectionEntity unloadCollection(String name, Mode mode) throws ArangoException {
		return collectionDriver.unloadCollection(name, mode);
	}
	
	public CollectionEntity truncateCollection(long id, Mode mode) throws ArangoException {
		return collectionDriver.truncateCollection(id, mode);
	}
	public CollectionEntity truncateCollection(String name, Mode mode) throws ArangoException {
		return collectionDriver.truncateCollection(name, mode);
	}
	
	public CollectionEntity setCollectionProperties(long id, boolean newWaitForSync, Mode mode) throws ArangoException {
		return collectionDriver.setCollectionProperties(id, newWaitForSync, mode);
	}
	public CollectionEntity setCollectionProperties(String name, boolean newWaitForSync, Mode mode) throws ArangoException {
		return collectionDriver.setCollectionProperties(name, newWaitForSync, mode);
	}
	
	public CollectionEntity renameCollection(long id, String newName, Mode mode) throws ArangoException {
		return collectionDriver.renameCollection(id, newName, mode);
	}
	public CollectionEntity renameCollection(String name, String newName, Mode mode) throws ArangoException {
		return collectionDriver.renameCollection(name, newName, mode);
	}
	
	public CollectionEntity deleteCollection(long id, Mode mode) throws ArangoException {
		return collectionDriver.deleteCollection(id, mode);
	}
	public CollectionEntity deleteCollection(String name, Mode mode) throws ArangoException {
		return collectionDriver.deleteCollection(name, mode);
	}
	// ---------------------------------------- end of collection ----------------------------------------

	
	// ---------------------------------------- start of document ----------------------------------------
	
	public DocumentEntity<?> createDocument(long collectionId, Object value, Boolean createCollection, Boolean waitForSync, Mode mode) throws ArangoException {
		return documentDriver.createDocument(collectionId, value, createCollection, waitForSync, mode);
	}
	public <T> DocumentEntity<T> createDocument(String collectionName, Object value, Boolean createCollection, Boolean waitForSync, Mode mode) throws ArangoException {
		return documentDriver.createDocument(collectionName, value, createCollection, waitForSync, mode);
	}
	
	public DocumentEntity<?> updateDocument(long collectionId, long documentId, Object value, long rev, Policy policy, Boolean waitForSync, Mode mode) throws ArangoException {
		return documentDriver.updateDocument(collectionId, documentId, value, rev, policy, waitForSync, mode);
	}
	public DocumentEntity<?> updateDocument(String collectionName, long documentId, Object value, long rev, Policy policy, Boolean waitForSync, Mode mode) throws ArangoException {
		return documentDriver.updateDocument(collectionName, documentId, value, rev, policy, waitForSync, mode);
	}
	public <T> DocumentEntity<T> updateDocument(String documentHandle, Object value, long rev, Policy policy, Boolean waitForSync, Mode mode) throws ArangoException {
		return documentDriver.updateDocument(documentHandle, value, rev, policy, waitForSync, mode);
	}
	
	public List<String> getDocuments(long collectionId) throws ArangoException {
		return documentDriver.getDocuments(collectionId);
	}
	public List<String> getDocuments(String collectionName) throws ArangoException {
		return documentDriver.getDocuments(collectionName);
	}
	
	public long checkDocument(long collectionId, long documentId) throws ArangoException {
		return documentDriver.checkDocument(collectionId, documentId);
	}
	public long checkDocument(String collectionName, long documentId) throws ArangoException {
		return documentDriver.checkDocument(collectionName, documentId);
	}
	public long checkDocument(String documentHandle) throws ArangoException {
		return documentDriver.checkDocument(documentHandle);
	}

	public <T> DocumentEntity<T> getDocument(long collectionId, long documentId, Class<T> clazz, Mode mode) throws ArangoException {
		return documentDriver.getDocument(collectionId, documentId, clazz, mode);
	}
	public <T> DocumentEntity<T> getDocument(String collectionName, long documentId, Class<T> clazz, Mode mode) throws ArangoException {
		return documentDriver.getDocument(collectionName, documentId, clazz, mode);
	}
	public <T> DocumentEntity<T> getDocument(String documentHandle, Class<T> clazz, Mode mode) throws ArangoException {
		return documentDriver.getDocument(documentHandle, clazz, mode);
	}

	public DocumentEntity<?> deleteDocument(long collectionId, long documentId, long rev, Policy policy, Mode mode) throws ArangoException {
		return documentDriver.deleteDocument(collectionId, documentId, rev, policy, mode);
	}
	public DocumentEntity<?> deleteDocument(String collectionName, long documentId, long rev, Policy policy, Mode mode) throws ArangoException {
		return documentDriver.deleteDocument(collectionName, documentId, rev, policy, mode);
	}
	public DocumentEntity<?> deleteDocument(String documentHandle, long rev, Policy policy, Mode mode) throws ArangoException {
		return documentDriver.deleteDocument(documentHandle, rev, policy, mode);
	}
	
	// ---------------------------------------- end of document ----------------------------------------
	

	// ---------------------------------------- start of cursor ----------------------------------------

	public CursorEntity<?> validateQuery(String query) throws ArangoException {
		return cursorDriver.validateQuery(query);
	}
	
	// TODO Mode
	public <T> CursorEntity<T> executeQuery(
			String query, Map<String, Object> bindVars,
			Class<T> clazz,
			Boolean calcCount, Integer batchSize) throws ArangoException {
		
		return cursorDriver.executeQuery(query, bindVars, clazz, calcCount, batchSize);
		
	}
	
	// TODO Mode
	public <T> CursorEntity<T> continueQuery(long cursorId, Class<T> clazz) throws ArangoException {
		return cursorDriver.continueQuery(cursorId, clazz);
	}
	
	// TODO Mode
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
			Map<String, Object> attributes, Date expiredDate,
			Mode mode) throws ArangoException {
		return kvsDriver.createKeyValue(collectionName, key, value, attributes, expiredDate, mode);
	}
	
	public KeyValueEntity updateKeyValue(
			String collectionName, String key, Object value, 
			Map<String, Object> attributes, Date expiredDate,
			boolean create,
			Mode mode
			) throws ArangoException {
		return kvsDriver.updateKeyValue(collectionName, key, value, attributes, expiredDate, create, mode);
	}
	
	// ---------------------------------------- end of kvs ----------------------------------------

	
	// ---------------------------------------- start of index ----------------------------------------
	// IndexはModeなしにする。

	public IndexEntity createIndex(long collectionId, IndexType type, boolean unique, String... fields) throws ArangoException {
		return indexDriver.createIndex(collectionId, type, unique, fields);
	}
	public IndexEntity createIndex(String collectionName, IndexType type, boolean unique, String... fields) throws ArangoException {
		return indexDriver.createIndex(collectionName, type, unique, fields);
	}

	public IndexEntity createCappedIndex(long collectionId, int size) throws ArangoException {
		return indexDriver.createCappedIndex(collectionId, size);
	}
	public IndexEntity createCappedIndex(String collectionName, int size) throws ArangoException {
		return indexDriver.createCappedIndex(collectionName, size);
	}
	
	public IndexEntity deleteIndex(String indexHandle) throws ArangoException {
		return indexDriver.deleteIndex(indexHandle);
	}

	public IndexEntity getIndex(String indexHandle) throws ArangoException {
		return indexDriver.getIndex(indexHandle);
	}

	public IndexesEntity getIndexes(long collectionId) throws ArangoException {
		return indexDriver.getIndexes(collectionId);
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
		return edgeDriver.createEdge(collectionId, fromHandle, toHandle, attribute);
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
		return edgeDriver.deleteEdge(collectionId, edgeHandle);
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

	public AdminConfigurationEntity getServerConfiguration() throws ArangoException {
		return adminDriver.getServerConfiguration();
	}
	
	public AdminConfigDescriptionEntity getServerConfigurationDescription() throws ArangoException {
		return adminDriver.getServerConfigurationDescription();
	}
	

	// ---------------------------------------- end of admin ----------------------------------------


	// ---------------------------------------- start of xxx ----------------------------------------

	// ---------------------------------------- end of xxx ----------------------------------------

	

	public static enum Mode {
		RETURN_NULL,
		RETURN_ERROR_ENTITY,
		RAISE_ERROR,
		DUP_GET,
		CREATE
	}
	
}
