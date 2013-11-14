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

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.EntityFactory;
import at.orz.arangodb.entity.GraphEntity;
import at.orz.arangodb.http.HttpResponseEntity;
import at.orz.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * @since 1.4.0
 */
public class InternalGraphDriverImpl extends BaseArangoDriverImpl {

	InternalGraphDriverImpl(ArangoConfigure configure) {
		super(configure);
	}
	
	public GraphEntity createGraph(
			String database, 
			String documentKey, String vertices, String edges,
			Boolean waitForSync) throws ArangoException {
		
		HttpResponseEntity res = httpManager.doPost(
				createEndpointUrl(baseUrl, database, "/_api/graph"), 
				new MapBuilder().put("waitForSync", waitForSync).get(), 
				EntityFactory.toJsonString(
						new MapBuilder()
						.put("_key", documentKey)
						.put("vertices", vertices)
						.put("edges", edges).get()));
		
		return createEntity(res, GraphEntity.class);
		
	}
	
}
