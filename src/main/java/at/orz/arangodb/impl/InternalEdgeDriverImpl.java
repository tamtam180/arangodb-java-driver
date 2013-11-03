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
			String database,
			String collectionName, 
			String fromHandle, String toHandle, 
			T attribute) throws ArangoException {
		
		validateCollectionName(collectionName);
		validateDocumentHandle(fromHandle);
		validateDocumentHandle(toHandle);
		HttpResponseEntity res = httpManager.doPost(
				createEndpointUrl(baseUrl, database, "/_api/edge"), 
				new MapBuilder()
					.put("collection", collectionName)
					.put("from", fromHandle)
					.put("to", toHandle)
					.get(), 
				EntityFactory.toJsonString(attribute)
				);
		
		EdgeEntity<T> entity = createEntity(res, EdgeEntity.class);
		return entity;
		
	}

	// TODO UpdateEdge
	public <T> EdgeEntity<T> updateEdge(
			String database,
			String collectionName, 
			String fromHandle, String toHandle, 
			T attribute) throws ArangoException {
		
		validateCollectionName(collectionName);
		validateDocumentHandle(fromHandle);
		validateDocumentHandle(toHandle);
		HttpResponseEntity res = httpManager.doPut(
				createEndpointUrl(baseUrl, database, "/_api/edge"), 
				new MapBuilder()
					.put("collection", collectionName)
					.put("from", fromHandle)
					.put("to", toHandle)
					.get(), 
				EntityFactory.toJsonString(attribute)
				);
		
		EdgeEntity<T> entity = createEntity(res, EdgeEntity.class);
		return entity;
		
	}
	
	public long checkEdge(String database, String edgeHandle) throws ArangoException {
		
		validateDocumentHandle(edgeHandle);
		HttpResponseEntity res = httpManager.doHead(
				createEndpointUrl(baseUrl, database, "/_api/edge", edgeHandle),
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
	public <T> EdgeEntity<T> getEdge(String database, String edgeHandle, Class<T> attributeClass) throws ArangoException {
		
		validateDocumentHandle(edgeHandle);
		HttpResponseEntity res = httpManager.doGet(
				createEndpointUrl(baseUrl, database, "/_api/edge", edgeHandle)
				);
		
		return createEntity(res, EdgeEntity.class, attributeClass);
		
	}

	public EdgeEntity<?> deleteEdge(String database, String collectionName, String edgeHandle) throws ArangoException {
		
		validateDocumentHandle(edgeHandle);
		HttpResponseEntity res = httpManager.doDelete(
				createEndpointUrl(baseUrl, database, "/_api/edge", edgeHandle),
				null);
		
		EdgeEntity<?> entity = createEntity(res, EdgeEntity.class);
		return entity;
		
	}
	
	public <T> EdgesEntity<T> getEdges(String database, String collectionName, String vertexHandle, Direction direction, Class<T> edgeAttributeClass) throws ArangoException {
		
		validateCollectionName(collectionName);
		validateDocumentHandle(vertexHandle);
		HttpResponseEntity res = httpManager.doGet(
				createEndpointUrl(baseUrl, database, "/_api/edges", collectionName), 
				new MapBuilder()
					.put("vertex", vertexHandle)
					.put("direction", direction.name().toLowerCase(Locale.US))
					.get()
				);
		
		return createEntity(res, EdgesEntity.class, edgeAttributeClass);
		
	}

	
}
