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

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.CursorResultSet;
import at.orz.arangodb.entity.CursorEntity;
import at.orz.arangodb.entity.EntityFactory;
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

	public <T> CursorResultSet<T> eexecuteSimpleAllWithResultSet(
			String collectionName, int skip, int limit,
			Class<T> clazz) throws ArangoException {
		
		CursorEntity<T> entity = executeSimpleAll(collectionName, skip, limit, clazz);
		CursorResultSet<T> rs = new CursorResultSet<T>(cursorDriver, clazz, entity);
		return rs;
		
	}

	public <T> CursorEntity<T> executeSimpleAll(
			String collectionName, int skip, int limit,
			Class<T> clazz) throws ArangoException {
		
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/cursor", 
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

}
