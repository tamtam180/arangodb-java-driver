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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.ReplicationDumpRecord;
import at.orz.arangodb.entity.ReplicationInventoryEntity;
import at.orz.arangodb.entity.StreamEntity;
import at.orz.arangodb.http.HttpResponseEntity;
import at.orz.arangodb.util.DumpHandler;
import at.orz.arangodb.util.IOUtils;
import at.orz.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class InternalReplicationDriverImpl extends BaseArangoDriverImpl {

	InternalReplicationDriverImpl(ArangoConfigure configure) {
		super(configure);
	}
	
	public ReplicationInventoryEntity getReplicationInventory(String database, Boolean includeSystem) throws ArangoException {
		
		HttpResponseEntity res = httpManager.doGet(
				createEndpointUrl(baseUrl, database, "/_api/replication/inventory"), 
				new MapBuilder().put("includeSystem", includeSystem).get());
		
		return createEntity(res, ReplicationInventoryEntity.class);
		
	}
	
	public <T> void getReplicationDump(
			String database, 
			String collectionName,
			Long from, Long to, Integer chunkSize, Boolean ticks,
			Class<T> clazz, DumpHandler<T> handler) throws ArangoException {

		HttpResponseEntity res = httpManager.doGet(
				createEndpointUrl(baseUrl, database, "/_api/replication/dump"),
				new MapBuilder()
				.put("collection", collectionName)
				.put("from", from)
				.put("to", to)
				.put("chunkSize", chunkSize)
				.put("ticks", ticks)
				.get()
				);
		
		StreamEntity entity = createEntity(res, StreamEntity.class);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(entity.getStream(), "utf-8"));
			String line = null;
			boolean cont = true;
			while (cont && (line = reader.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				}
				cont = handler.handle(createEntity(line, ReplicationDumpRecord.class, clazz));
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e); // not arnago-exception: because encoding error is cause of system.
		} catch (IOException e) {
			throw new ArangoException(e);
		} finally {
			IOUtils.close(reader);
		}
		
	}

}
