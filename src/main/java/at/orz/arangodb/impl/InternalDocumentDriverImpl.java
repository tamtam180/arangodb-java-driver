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

package at.orz.arangodb.impl;

import java.util.List;
import java.util.Locale;

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.DefaultEntity;
import at.orz.arangodb.entity.DocumentEntity;
import at.orz.arangodb.entity.DocumentsEntity;
import at.orz.arangodb.entity.EntityFactory;
import at.orz.arangodb.entity.Policy;
import at.orz.arangodb.http.HttpManager;
import at.orz.arangodb.http.HttpResponseEntity;
import at.orz.arangodb.util.CollectionUtils;
import at.orz.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class InternalDocumentDriverImpl extends BaseArangoDriverImpl {

	InternalDocumentDriverImpl(ArangoConfigure configure) {
		super(configure);
	}

//	public DocumentEntity<?> createDocument(long collectionId, Object value, Boolean createCollection, Boolean waitForSync) throws ArangoException {
//		return createDocument(String.valueOf(collectionId), value, createCollection, waitForSync);
//	}
	public <T> DocumentEntity<T> createDocument(String collectionName, Object value, Boolean createCollection, Boolean waitForSync) throws ArangoException {
		
		validateCollectionName(collectionName);
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/document", 
				new MapBuilder()
					.put("collection", collectionName)
					.put("createCollection", createCollection)
					.put("waitForSync", waitForSync)
					.get(),
				EntityFactory.toJsonString(value));
		
		try {
			DocumentEntity<T> entity = createEntity(res, DocumentEntity.class);
			return entity;
		} catch (ArangoException e) {
//			if (HttpManager.is404Error(e)) {
//				if (mode == null || mode == Mode.RETURN_NULL) {
//					return null;
//				}
//			}
			throw e;
		}
		
	}
	
	public <T> DocumentEntity<T> updateDocument(String documentHandle, Object value, long rev, Policy policy, Boolean waitForSync) throws ArangoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/document/" + documentHandle, 
				new MapBuilder()
					.put("rev", rev == -1 ? null : rev)
					.put("policy", policy == null ? null : policy.name())
					.put("waitForSync", waitForSync)
					.get(),
				EntityFactory.toJsonString(value));
		
		DocumentEntity<T> entity = createEntity(res, DocumentEntity.class);
		return entity;
		
	}

	public <T> DocumentEntity<T> partialUpdateDocument(String documentHandle, Object value, long rev, Policy policy, Boolean waitForSync, Boolean keepNull) throws ArangoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doPatch(
				baseUrl + "/_api/document/" + documentHandle, 
				new MapBuilder()
					.put("rev", rev == -1 ? null : rev)
					.put("policy", policy == null ? null : policy.name())
					.put("waitForSync", waitForSync)
					.put("keepNull", keepNull)
					.get(),
				EntityFactory.toJsonString(value));
		
		DocumentEntity<T> entity = createEntity(res, DocumentEntity.class);
		return entity;
		
	}

	
//	public List<String> getDocuments(long collectionId) throws ArangoException {
//		return getDocuments(String.valueOf(collectionId));
//	}
	public List<String> getDocuments(String collectionName) throws ArangoException {
		
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/document", 
				new MapBuilder("collection", collectionName).get()
				);
		
		DocumentsEntity entity = createEntity(res, DocumentsEntity.class);
		return CollectionUtils.safety(entity.getDocuments());
		
	}
	
	
//	public long checkDocument(long collectionId, long documentId) throws ArangoException {
//		return checkDocument(createDocumentHandle(collectionId, documentId));
//	}
//	public long checkDocument(String collectionName, long documentId) throws ArangoException {
//		return checkDocument(createDocumentHandle(collectionName, documentId));
//	}
	public long checkDocument(String documentHandle) throws ArangoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doHead(
				baseUrl + "/_api/document/" + documentHandle,
				null
				);
		
		DefaultEntity entity = createEntity(res, DefaultEntity.class);
		return entity.getEtag();
		
	}

//	public <T> DocumentEntity<T> getDocument(long collectionId, long documentId, Class<T> clazz) throws ArangoException {
//		return getDocument(createDocumentHandle(collectionId, documentId), clazz);
//	}
//	public <T> DocumentEntity<T> getDocument(String collectionName, long documentId, Class<T> clazz) throws ArangoException {
//		return getDocument(createDocumentHandle(collectionName, documentId), clazz);
//	}
	public <T> DocumentEntity<T> getDocument(String documentHandle, Class<T> clazz) throws ArangoException {
		
		// TODO If-None-Match http-header
		// TODO CAS
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/document/" + documentHandle,
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
		} catch (ArangoException e) {
			// TODO 404
//			if (HttpManager.is404Error(e)) {
//				if (mode == null || mode == Mode.RETURN_NULL) {
//					return null;
//				}
//			}
			throw e;
		}
	}

//	public DocumentEntity<?> deleteDocument(long collectionId, long documentId, long rev, Policy policy) throws ArangoException {
//		return deleteDocument(createDocumentHandle(collectionId, documentId), rev, policy);
//	}
//	public DocumentEntity<?> deleteDocument(String collectionName, long documentId, long rev, Policy policy) throws ArangoException {
//		return deleteDocument(createDocumentHandle(collectionName, documentId), rev, policy);
//	}
	public DocumentEntity<?> deleteDocument(String documentHandle, long rev, Policy policy) throws ArangoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doDelete(
				baseUrl + "/_api/document/" + documentHandle, 
				new MapBuilder()
				.put("rev", rev == -1 ? null : rev)
				.put("policy", policy == null ? null : policy.name().toLowerCase(Locale.US))
				.get());
		
		try {
			DocumentEntity<?> entity = createEntity(res, DocumentEntity.class);
			return entity;
		} catch (ArangoException e) {
//			if (HttpManager.is404Error(e)) {
//				if (mode == null || mode == Mode.RETURN_NULL) {
//					return null;
//				}
//			} else if (HttpManager.is412Error(e)) {
//				// TODO mode
//				return (DocumentEntity<?>) e.getEntity();
//			}
			throw e;
		}
		
	}

}
