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
import java.util.ListIterator;
import java.util.Locale;

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.DefaultEntity;
import at.orz.arangodb.entity.DocumentEntity;
import at.orz.arangodb.entity.DocumentsEntity;
import at.orz.arangodb.entity.EntityFactory;
import at.orz.arangodb.entity.Policy;
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

	private <T> DocumentEntity<T> _createDocument(String database, String collectionName, Object value, Boolean createCollection, Boolean waitForSync, boolean raw) throws ArangoException {
		
		validateCollectionName(collectionName);
		HttpResponseEntity res = httpManager.doPost(
				createEndpointUrl(baseUrl, database, "/_api/document"), 
				new MapBuilder()
					.put("collection", collectionName)
					.put("createCollection", createCollection)
					.put("waitForSync", waitForSync)
					.get(),
					raw ? value.toString() : EntityFactory.toJsonString(value));
		
		return createEntity(res, DocumentEntity.class);
		
	}

	public <T> DocumentEntity<T> createDocument(String database, String collectionName, Object value, Boolean createCollection, Boolean waitForSync) throws ArangoException {
		return _createDocument(database, collectionName, value, createCollection, waitForSync, false);
	}

	public <T> DocumentEntity<T> createDocumentRaw(String database, String collectionName, String rawJsonString, Boolean createCollection, Boolean waitForSync) throws ArangoException {
		return _createDocument(database, collectionName, rawJsonString, createCollection, waitForSync, true);
	}

	public <T> DocumentEntity<T> replaceDocument(String database, String documentHandle, Object value, long rev, Policy policy, Boolean waitForSync) throws ArangoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doPut(
				createEndpointUrl(baseUrl, database, "/_api/document", documentHandle), 
				new MapBuilder()
					.put("rev", rev == -1 ? null : rev)
					.put("policy", policy == null ? null : policy.name())
					.put("waitForSync", waitForSync)
					.get(),
				EntityFactory.toJsonString(value));
		
		return createEntity(res, DocumentEntity.class);
		
	}

	public <T> DocumentEntity<T> updateDocument(String database, String documentHandle, Object value, long rev, Policy policy, Boolean waitForSync, Boolean keepNull) throws ArangoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doPatch(
				createEndpointUrl(baseUrl, database, "/_api/document", documentHandle), 
				new MapBuilder()
					.put("rev", rev == -1 ? null : rev)
					.put("policy", policy == null ? null : policy.name())
					.put("waitForSync", waitForSync)
					.put("keepNull", keepNull)
					.get(),
				EntityFactory.toJsonString(value, keepNull != null && !keepNull));
		
		DocumentEntity<T> entity = createEntity(res, DocumentEntity.class);
		return entity;
		
	}

	private static final String API_DOCUMENT_PREFIX = "/_api/document/";
	public List<String> getDocuments(String database, String collectionName, boolean handleConvert) throws ArangoException {
		
		HttpResponseEntity res = httpManager.doGet(
				createEndpointUrl(baseUrl, database, "/_api/document"), 
				new MapBuilder("collection", collectionName).get()
				);
		
		DocumentsEntity entity = createEntity(res, DocumentsEntity.class);
		List<String> documents = CollectionUtils.safety(entity.getDocuments());
		
		if (handleConvert && !documents.isEmpty()) {
			ListIterator<String> lit = documents.listIterator();
			while (lit.hasNext()) {
				String d = lit.next();
				if (d.startsWith(API_DOCUMENT_PREFIX)) {
					lit.set(d.substring(API_DOCUMENT_PREFIX.length()));
				}
			}
		}
		return documents;
	}
	
	public long checkDocument(String database, String documentHandle) throws ArangoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doHead(
				createEndpointUrl(baseUrl, database, "/_api/document", documentHandle),
				null
				);
		
		DefaultEntity entity = createEntity(res, DefaultEntity.class);
		return entity.getEtag();
		
	}

	public <T> DocumentEntity<T> getDocument(String database, String documentHandle, Class<T> clazz) throws ArangoException {
		
		// TODO If-None-Match http-header
		// TODO CAS
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doGet(
				createEndpointUrl(baseUrl, database, "/_api/document", documentHandle),
				null);
		
		// TODO Case of StatusCode=304
		
		//T obj = createEntityImpl(res, clazz);
		DocumentEntity<T> entity = createEntity(res, DocumentEntity.class, clazz);
		if (entity == null) {
			entity = new DocumentEntity<T>();
		}
		//entity.setEntity(obj);
		return entity;

	}

	public DocumentEntity<?> deleteDocument(String database, String documentHandle, long rev, Policy policy) throws ArangoException {
		
		validateDocumentHandle(documentHandle);
		HttpResponseEntity res = httpManager.doDelete(
				createEndpointUrl(baseUrl, database, "/_api/document", documentHandle), 
				new MapBuilder()
				.put("rev", rev == -1 ? null : rev)
				.put("policy", policy == null ? null : policy.name().toLowerCase(Locale.US))
				.get());
		
		try {
			DocumentEntity<?> entity = createEntity(res, DocumentEntity.class);
			return entity;
		} catch (ArangoException e) {
			throw e;
		}
		
	}

}
