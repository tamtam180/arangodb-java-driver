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

import java.util.Collections;
import java.util.Map;

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.CursorResultSet;
import at.orz.arangodb.entity.CursorEntity;
import at.orz.arangodb.entity.DefaultEntity;
import at.orz.arangodb.entity.EntityFactory;
import at.orz.arangodb.http.HttpManager;
import at.orz.arangodb.http.HttpResponseEntity;
import at.orz.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class InternalCursorDriverImpl extends BaseArangoDriverImpl {

	InternalCursorDriverImpl(ArangoConfigure configure) {
		super(configure);
	}
	
	public CursorEntity<?> validateQuery(String query) throws ArangoException {
		
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/query", 
				null,
				EntityFactory.toJsonString(new MapBuilder("query", query).get())
				);
		try {
			CursorEntity<?> entity = createEntity(res, CursorEntity.class);
			return entity;
		} catch (ArangoException e) {
			return (CursorEntity<?>) e.getEntity();
		}
		
	}
	
	// ※Iteratorで綺麗に何回もRoundtripもしてくれる処理はClientのレイヤーで行う。
	// ※ここでは単純にコールするだけ
	
	// TODO Mode
	public <T> CursorEntity<T> executeQuery(
			String query, Map<String, Object> bindVars,
			Class<T> clazz,
			Boolean calcCount, Integer batchSize) throws ArangoException {
		
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
		} catch (ArangoException e) {
			// TODO
			throw e;
		}
		
	}
	
	// TODO Mode
	public <T> CursorEntity<T> continueQuery(long cursorId, Class<T> clazz) throws ArangoException {
		
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
		} catch (ArangoException e) {
			// TODO
			throw e;
		}
		
	}
	
	// TODO Mode
	public DefaultEntity finishQuery(long cursorId) throws ArangoException {
		HttpResponseEntity res = httpManager.doDelete(
				baseUrl + "/_api/cursor/" + cursorId, 
				null
				);
		
		try {
			DefaultEntity entity = createEntity(res, DefaultEntity.class);
			return entity;
		} catch (ArangoException e) {
			// TODO Mode
			if (e.getErrorNumber() == 1600) {
				// 既に削除されている
				return (DefaultEntity) e.getEntity();
			}
			throw e;
		}
	}
	
	public <T> CursorResultSet<T> executeQueryWithResultSet(
			String query, Map<String, Object> bindVars,
			Class<T> clazz,
			Boolean calcCount, Integer batchSize) throws ArangoException {
		
		CursorEntity<T> entity = executeQuery(query, bindVars, clazz, calcCount, batchSize);
		CursorResultSet<T> rs = new CursorResultSet<T>(this, clazz, entity);
		return rs;
		
	}
	
}
