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
import at.orz.arangodb.entity.AdminConfigDescriptionEntity;
import at.orz.arangodb.entity.AdminConfigurationEntity;
import at.orz.arangodb.entity.AdminLogEntity;
import at.orz.arangodb.entity.AdminStatusEntity;
import at.orz.arangodb.http.HttpResponseEntity;
import at.orz.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class InternalAdminDriverImpl extends BaseArangoDriverImpl {

	InternalAdminDriverImpl(ArangoConfigure configure) {
		super(configure);
	}

	public AdminLogEntity getServerLog(
			Integer logLevel, Boolean logLevelUpTo,
			Integer start,
			Integer size, Integer offset,
			Boolean sortAsc,
			String text
			) throws ArangoException {
		
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
		} catch (ArangoException e) {
			throw e;
			//return null;
		}
		
	}
	
	public AdminStatusEntity getServerStatus() throws ArangoException {
		
		HttpResponseEntity res = httpManager.doGet(baseUrl + "/_admin/status");
		
		try {
			return createEntity(res, AdminStatusEntity.class);
		} catch (ArangoException e) {
			throw e;
		}
		
	}

	public AdminConfigurationEntity getServerConfiguration() throws ArangoException {
		
		HttpResponseEntity res = httpManager.doGet(baseUrl + "/_admin/config/configuration");
		
		try {
			return createEntity(res, AdminConfigurationEntity.class);
		} catch (ArangoException e) {
			throw e;
		}
		
	}
	
	public AdminConfigDescriptionEntity getServerConfigurationDescription() throws ArangoException {
		
		HttpResponseEntity res = httpManager.doGet(baseUrl + "/_admin/config/description");
		return createEntity(res, AdminConfigDescriptionEntity.class);
		
	}

	
}
