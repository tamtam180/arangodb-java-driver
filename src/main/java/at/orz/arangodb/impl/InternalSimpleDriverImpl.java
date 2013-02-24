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

import java.util.Map;

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.CursorResultSet;
import at.orz.arangodb.entity.CursorEntity;
import at.orz.arangodb.entity.DocumentEntity;
import at.orz.arangodb.entity.EntityFactory;
import at.orz.arangodb.entity.ScalarExampleEntity;
import at.orz.arangodb.http.HttpResponseEntity;
import at.orz.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class InternalSimpleDriverImpl extends BaseArangoDriverWithCursorImpl {

	InternalSimpleDriverImpl(ArangoConfigure configure,
			InternalCursorDriverImpl cursorDriver) {
		super(configure, cursorDriver);
	}

	public <T> CursorResultSet<T> executeSimpleAllWithResultSet(
			String collectionName, int skip, int limit,
			Class<T> clazz) throws ArangoException {
		
		CursorEntity<T> entity = executeSimpleAll(collectionName, skip, limit, clazz);
		CursorResultSet<T> rs = new CursorResultSet<T>(cursorDriver, clazz, entity);
		return rs;
		
	}

	public <T> CursorEntity<T> executeSimpleAll(
			String collectionName, int skip, int limit,
			Class<T> clazz) throws ArangoException {
		
		validateCollectionName(collectionName);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/simple/all", 
				null,
				EntityFactory.toJsonString(
						new MapBuilder()
						.put("collection", collectionName)
						.put("skip", skip > 0 ? skip : null)
						.put("limit", limit > 0 ? limit : null)
						.get())
				);
		
		try {
			CursorEntity<T> entity = createEntity(res, CursorEntity.class);
			// resultを処理する
			EntityFactory.createResult(entity, clazz);
			return entity;
		} catch (ArangoException e) {
			// TODO
			throw e;
		}		
	}

	public <T> CursorEntity<T> executeSimpleByExample(
			String collectionName,
			Map<String, Object> example,
			int skip, int limit,
			Class<T> clazz
			) throws ArangoException {
		
		validateCollectionName(collectionName);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/simple/by-example", 
				null,
				EntityFactory.toJsonString(
						new MapBuilder()
						.put("collection", collectionName)
						.put("example", example)
						.put("skip", skip > 0 ? skip : null)
						.put("limit", limit > 0 ? limit : null)
						.get())
				);
		
		try {
			CursorEntity<T> entity = createEntity(res, CursorEntity.class);
			// resultを処理する
			EntityFactory.createResult(entity, clazz);
			return entity;
		} catch (ArangoException e) {
			// TODO
			throw e;
		}		
		
	}

	public <T> CursorResultSet<T> executeSimpleByExampleWithResultSet(
			String collectionName, Map<String, Object> example,
			int skip, int limit,
			Class<T> clazz
			) throws ArangoException {
		
		CursorEntity<T> entity = executeSimpleByExample(collectionName, example, skip, limit, clazz);
		CursorResultSet<T> rs = new CursorResultSet<T>(cursorDriver, clazz, entity);
		return rs;
		
	}

	public <T> ScalarExampleEntity<T> executeSimpleFirstExample(
			String collectionName,
			Map<String, Object> example,
			Class<T> clazz
			) throws ArangoException {
		
		validateCollectionName(collectionName);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/simple/first-example", 
				null,
				EntityFactory.toJsonString(
						new MapBuilder()
						.put("collection", collectionName)
						.put("example", example)
						.get())
				);
		
		ScalarExampleEntity<T> entity = createEntity(res, ScalarExampleEntity.class);
		return EntityFactory.createScalarExampleEntity(entity, clazz);
		
	}
	
}
