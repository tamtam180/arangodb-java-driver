package at.orz.arangodb.impl;

import java.util.Locale;

import at.orz.arangodb.ArangoConfigure;
import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.Direction;
import at.orz.arangodb.entity.EdgeEntity;
import at.orz.arangodb.entity.EdgesEntity;
import at.orz.arangodb.entity.EntityFactory;
import at.orz.arangodb.http.HttpResponseEntity;
import at.orz.arangodb.util.MapBuilder;

public class InternalEdgeDriverImpl extends BaseArangoDriverImpl {

	InternalEdgeDriverImpl(ArangoConfigure configure) {
		super(configure);
	}
	
	public <T> EdgeEntity<T> createEdge(
			String collectionName, 
			String fromHandle, String toHandle, 
			T attribute) throws ArangoException {
		
		validateCollectionName(collectionName);
		validateDocumentHandle(fromHandle);
		validateDocumentHandle(toHandle);
		HttpResponseEntity res = httpManager.doPost(
				baseUrl + "/_api/edge", 
				new MapBuilder()
					.put("collection", collectionName)
					.put("from", fromHandle)
					.put("to", toHandle)
					.get(), 
				EntityFactory.toJsonString(attribute)
				);
		
		try {
			EdgeEntity<T> entity = createEntity(res, EdgeEntity.class);
			return entity;
		} catch (ArangoException e) {
			throw e;
		}
		
	}

	// TODO UpdateEdge
	public <T> EdgeEntity<T> updateEdge(
			String collectionName, 
			String fromHandle, String toHandle, 
			T attribute) throws ArangoException {
		
		validateCollectionName(collectionName);
		validateDocumentHandle(fromHandle);
		validateDocumentHandle(toHandle);
		HttpResponseEntity res = httpManager.doPut(
				baseUrl + "/_api/edge", 
				new MapBuilder()
					.put("collection", collectionName)
					.put("from", fromHandle)
					.put("to", toHandle)
					.get(), 
				EntityFactory.toJsonString(attribute)
				);
		
		try {
			EdgeEntity<T> entity = createEntity(res, EdgeEntity.class);
			return entity;
		} catch (ArangoException e) {
			return null;
		}
		
	}
	
	public long checkEdge(String edgeHandle) throws ArangoException {
		
		validateDocumentHandle(edgeHandle);
		HttpResponseEntity res = httpManager.doHead(
				baseUrl + "/_api/edge/" + edgeHandle,
				null
				);
		
		EdgeEntity<?> entity = createEntity(res, EdgeEntity.class);
		return entity.getEtag();

	}
	
	/**
	 * エッジハンドルを指定して、エッジの情報を取得する。
	 * @param edgeHandle
	 * @param attributeClass
	 * @return
	 * @throws ArangoException
	 */
	public <T> EdgeEntity<T> getEdge(String edgeHandle, Class<T> attributeClass) throws ArangoException {
		
		validateDocumentHandle(edgeHandle);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/edge/" + edgeHandle
				);
		
		try {
			EdgeEntity<T> entity = createEntity(res, EdgeEntity.class);
			if (entity != null) {
				T obj = createEntityImpl(res, attributeClass);
				entity.setAttributes(obj);
			}
			return entity;
		} catch (ArangoException e) {
			return null;
		}
		
	}

	public EdgeEntity<?> deleteEdge(String collectionName, String edgeHandle) throws ArangoException {
		
		validateDocumentHandle(edgeHandle);
		HttpResponseEntity res = httpManager.doDelete(
				baseUrl + "/_api/edge/" + edgeHandle,
				null);
		
		try {
			EdgeEntity<?> entity = createEntity(res, EdgeEntity.class);
			return entity;
		} catch (ArangoException e) {
			return null;
		}
		
	}
	
	public <T> EdgesEntity<T> getEdges(String collectionName, String vertexHandle, Direction direction, Class<T> edgeAttributeClass) throws ArangoException {
		
		validateCollectionName(collectionName);
		validateDocumentHandle(vertexHandle);
		HttpResponseEntity res = httpManager.doGet(
				baseUrl + "/_api/edges/" + collectionName, 
				new MapBuilder()
					.put("vertex", vertexHandle)
					.put("direction", direction.name().toLowerCase(Locale.US))
					.get()
				);
		
		try {
			EdgesEntity<T> entity = EntityFactory.createEdges(res.getText(), edgeAttributeClass);
			validateAndSetStatusCode(res, entity);
			return entity;
		} catch (ArangoException e) {
			return null;
		}
		
	}

	
}