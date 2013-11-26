/*
 * Copyright (C) 2012,2013 tamtam180
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

import java.util.TreeMap;

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.BooleanResultEntity;
import at.orz.arangodb.entity.DatabaseEntity;
import at.orz.arangodb.entity.EntityFactory;
import at.orz.arangodb.entity.StringsResultEntity;
import at.orz.arangodb.http.HttpResponseEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class InternalDatabaseDriverImpl extends BaseArangoDriverImpl {

	InternalDatabaseDriverImpl(ArangoConfigure configure) {
		super(configure);
	}

	public DatabaseEntity getCurrentDatabase() throws ArangoException {
		
		HttpResponseEntity res = httpManager.doGet(baseUrl + "/_api/database/current");
		return createEntity(res, DatabaseEntity.class);
		
	}
	
	public StringsResultEntity getDatabases() throws ArangoException {

		HttpResponseEntity res = httpManager.doGet(baseUrl + "/_api/database");
		return createEntity(res, StringsResultEntity.class);

	}
	
	public BooleanResultEntity createDatabase(String database) throws ArangoException {

		validateDatabaseName(database, false);
		
		TreeMap<String, Object> body = new TreeMap<String, Object>();
		body.put("name", database);
		
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/database", 
				null, 
				EntityFactory.toJsonString(body)
				);
		
		return createEntity(res, BooleanResultEntity.class);
		
	}

	public BooleanResultEntity deleteDatabase(String database) throws ArangoException {

		validateDatabaseName(database, false);
		
		TreeMap<String, Object> body = new TreeMap<String, Object>();
		body.put("name", database);
		
		HttpResponseEntity res = httpManager.doDelete(
				baseUrl + "/_api/database/" + database, // not need uri encode
				null
				);
		
		return createEntity(res, BooleanResultEntity.class);
		
	}

}
