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

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import at.orz.arangodb.entity.BaseEntity;
import at.orz.arangodb.entity.EntityDeserializers;
import at.orz.arangodb.entity.EntityFactory;
import at.orz.arangodb.entity.KeyValueEntity;
import at.orz.arangodb.http.HttpResponseEntity;
import at.orz.arangodb.util.DateUtils;
import at.orz.arangodb.util.ReflectionUtils;
import at.orz.arangodb.util.StringUtils;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public abstract class BaseArangoDriver {

	private static final Pattern databaseNamePattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9\\-_]{0,63}$");

	protected String createDocumentHandle(long collectionId, long documentId) {
		// validateCollectionNameは不要
		return collectionId + "/" + documentId;
	}

	protected String createDocumentHandle(String collectionName, long documentId) throws ArangoException {
		validateCollectionName(collectionName);
		return collectionName + "/" + documentId;
	}
	
	protected void validateCollectionName(String name) throws ArangoException {
		if (name.indexOf('/') != -1) {
			throw new ArangoException("does not allow '/' in name.");
		}
	}
	
	protected void validateDocumentHandle(String documentHandle) throws ArangoException {
		int pos = documentHandle.indexOf('/');
		if (pos > 0) {
			try {
				String collectionName = documentHandle.substring(0, pos);
				validateCollectionName(collectionName);
				long collectionId = Long.parseLong(documentHandle.substring(pos + 1));
				return;
			} catch (Exception e) {
			}
		}
		throw new ArangoException("invalid format documentHandle:" + documentHandle);
	}
	
	/**
	 * @param database
	 * @param allowNull
	 * @throws ArangoException
	 * @see http://www.arangodb.org/manuals/current/NamingConventions.html#DatabaseNames
	 */
	protected void validateDatabaseName(String database, boolean allowNull) throws ArangoException {
		boolean valid = false;
		if (database == null) {
			if (allowNull) {
				valid = true;
			}
		} else {
			valid = databaseNamePattern.matcher(database).matches();
		}
		if (!valid) {
			throw new ArangoException("invalid format database:" + database);
		}
	}
	
	protected void setKeyValueHeader(HttpResponseEntity res, KeyValueEntity entity) throws ArangoException {
		
		Map<String, String> headers = res.getHeaders();
		
		try {
			String strCreated = headers.get("x-voc-created");
			if (strCreated != null) {
				entity.setCreated(DateUtils.parse(strCreated, "yyyy-MM-dd'T'HH:mm:ss'Z'"));
			}
			
			String strExpires = headers.get("x-voc-expires");
			if (strExpires != null) {
				entity.setExpires(DateUtils.parse(strExpires, "yyyy-MM-dd'T'HH:mm:ss'Z'"));
			}
			
			String strExtened = headers.get("x-voc-extended");
			if (strExtened != null) {
				Map<String, Object> attributes = EntityFactory.createEntity(strExtened, Map.class);
				entity.setAttributes(attributes);
			}
			
		} catch (ParseException e) {
			throw new ArangoException(e);
		}
		
	}
	
	/**
	 * HTTPレスポンスから指定した型へ変換する。
	 * レスポンスがエラーであるかを確認して、エラーの場合は例外を投げる。
	 * @param res
	 * @param type
	 * @param validate
	 * @return
	 * @throws ArangoException
	 */
	protected <T extends BaseEntity> T createEntity(HttpResponseEntity res, Class<T> clazz, Class<?> pclazz, boolean validate) throws ArangoException {
		try {
			EntityDeserializers.setParameterized(pclazz);
			
			T entity = createEntityImpl(res, clazz);
			if (entity == null) {
				entity = ReflectionUtils.newInstance(clazz);
			}
			setStatusCode(res, entity);
			if (validate) {
				validate(res, entity);
				
			}
			return entity;
		} finally {
			EntityDeserializers.removeParameterized();
		}
	}

	protected <T extends BaseEntity> T createEntity(HttpResponseEntity res, Class<T> clazz) throws ArangoException {
		return createEntity(res, clazz, null, true);
	}

	protected <T extends BaseEntity> T createEntity(HttpResponseEntity res, Class<T> clazz, Class<?> pclazz) throws ArangoException {
		return createEntity(res, clazz, pclazz, true);
	}

	protected void setStatusCode(HttpResponseEntity res, BaseEntity entity) throws ArangoException {
		if (entity != null) {
			if (res.getEtag() > 0) {
				entity.setEtag(res.getEtag());
			}
			entity.setStatusCode(res.getStatusCode());
		}
	}
	
	protected void validate(HttpResponseEntity res, BaseEntity entity) throws ArangoException {
		if (entity != null) {
			if (entity.isError()) {
				throw new ArangoException(entity);
			}
		}
		
		// Custom Error
		if (res.getStatusCode() >= 400) {
			entity.setErrorNumber(res.getStatusCode());
			switch (res.getStatusCode()) {
			case 401:
				entity.setErrorMessage("Unauthorized");
				break;
			case 403:
				entity.setErrorMessage("Forbidden");
				break;
			}
			throw new ArangoException(entity);
		}
	}
	
	protected <T> T createEntityImpl(HttpResponseEntity res, Type type) throws ArangoException {
		T entity = EntityFactory.createEntity(res.getText(), type);
		return entity;
	}
	
	protected String createEndpointUrl(String baseUrl, String database, Object...paths) throws ArangoException {

		// FIXME: Very very foolish implement.
		
		ArrayList<String> list = new ArrayList<String>();
		
		if (database != null) {
			validateDatabaseName(database, false);
			list.add("_db");
			list.add(database);
		}
		for (Object path: paths) {
			list.add(path.toString());
		}
		return baseUrl + StringUtils.join(false, list);
	}
	
}
