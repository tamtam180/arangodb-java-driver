
This library ia a Java driver for ArangoDB.

Support version: ArangoDB-1.2.x

# Required

* Java 5 later

# Maven

```
```

# JavaDoc

# Library Structure

This library has 4 layers.

* Low layer
    * ArangoDriver
    * Corresponding to 1:1 and Rest-API.
    * All exception is raised other than normal processing.
    * **Multithread-safety**
* Middle layer **(Not yet implemented)**
    * ArangoClient
    * It is a wrapper class that easy to use ArangoDriver.
    * For example, you can not be an error to delete the ones that do not exist in the delete command,
    That it may not be an error to generate a duplicate Collection,
    it is provide you with an easy to use interface for general use.
* High layer **(Not yet implemented)**
    * object-oriented programming layer.
    * Each class is CRUD.
* JDBC layer **(Not yet implemented)**
    * AQL for JDBC driver

# How to use.

## Basic usage ArangoDriver

``` Java
  // Initialize configure
  ArangoConfigure configure = new ArangoConfigure();
  configure.init();

  // Create Driver (this instance is thread-safe)
  ArangoDriver client = new ArangoDriver(configure);
  
  String collectionName = "mytest";
  TestComplexEntity01 value = new TestComplexEntity01("name", "desc", 10); // any POJO class

  // Create Collection
  CollectionEntity collection = client.createCollection(collectionName);

  // Create Document
  DocumentEntity<TestComplexEntity01> ret1 = client.createDocument(collectionName, value, null, null);
  String documentHandle = ret1.getDocumentHandle();
  
  // Get Document
  DocumentEntity<TestComplexEntity01> ret2 =
    client.getDocument(documentHandle, TestComplexEntity01.class);

  // Delete Document
  driver.deleteDocument(documentHandle, -1, DeletePolicy.LAST);  

  // finalize library
  configure.shutdown();
```

## Create Graph data.

Since ArangoDB-1.1, If you put a graph document to collection, you need create a collection of graph type.


```Java

public class Example2 {
	
	public static class TestEdgeAttribute {
		public String a;
		public int b;
		public TestEdgeAttribute(){}
		public TestEdgeAttribute(String a, int b) {
			this.a = a;
			this.b = b;
		}
	}
	public static class TestVertex {
		public String name;
	}
	
	public static void main(String[] args) {

		// Initialize configure
		ArangoConfigure configure = new ArangoConfigure();
		configure.init();
		
		// Create Driver
		ArangoDriver driver = new ArangoDriver(configure);
		
		final String collectionName = "example";
		try {
			
			// Create Collection for *Graph*
			driver.createCollection(collectionName, false, null, null, null, CollectionType.EDGE);
			
			// Create 10 Vertex
			ArrayList<DocumentEntity<TestVertex>> docs = new ArrayList<DocumentEntity<TestVertex>>();
			for (int i = 0; i < 10; i++) {
				TestVertex value = new TestVertex();
				value.name = "vvv" + i;
				DocumentEntity<TestVertex> doc = driver.createDocument(collectionName, value, true, false);
				docs.add(doc);
			}
			
			// Create Edge
			// 0 -> 1
			// 0 -> 2
			// 2 -> 3
			
			EdgeEntity<TestEdgeAttribute> edge1 = driver.createEdge(
					collectionName, docs.get(0).getDocumentHandle(), docs.get(1).getDocumentHandle(), 
					new TestEdgeAttribute("edge1", 100));

			EdgeEntity<TestEdgeAttribute> edge2 = driver.createEdge(
					collectionName, docs.get(0).getDocumentHandle(), docs.get(2).getDocumentHandle(), 
					new TestEdgeAttribute("edge2", 200));

			EdgeEntity<TestEdgeAttribute> edge3 = driver.createEdge(
					collectionName, docs.get(2).getDocumentHandle(), docs.get(3).getDocumentHandle(), 
					new TestEdgeAttribute("edge3", 300));
			
			EdgesEntity<TestEdgeAttribute> edges = driver.getEdges(collectionName, docs.get(0).getDocumentHandle(), Direction.ANY, TestEdgeAttribute.class);
			System.out.println(edges.size());
			System.out.println(edges.get(0).getAttributes().a);
			System.out.println(edges.get(1).getAttributes().a);
			
		} catch (ArangoException e) {
			e.printStackTrace();
		} finally {
			// Finalize library
			configure.shutdown();
		}
		
	}

}

```

## Use AQL

Use ForEach

```Java
// Query
String query = "FOR t IN unit_test_query_test FILTER t.age >= @age SORT t.age RETURN t";
// Bind Variables
Map<String, Object> bindVars = new MapBuilder().put("age", 90).get();

// Execute Query
CursorResultSet<TestComplexEntity01> rs = driver.executeQueryWithResultSet(
		query, bindVars, TestComplexEntity01.class, true, 20);

for (TestComplexEntity01 obj: rs) {
	System.out.println(obj);
}

```

Not use ForEach

```Java
String query = "FOR t IN unit_test_query_test FILTER t.age >= @age SORT t.age RETURN t";
Map<String, Object> bindVars = new MapBuilder().put("age", 90).get();

CursorResultSet<TestComplexEntity01> rs = driver.executeQueryWithResultSet(
		query, bindVars, TestComplexEntity01.class, true, 20);

while (rs.hasNext()) {
	TestComplexEntity01 obj = rs.next();
	System.out.println(obj);
}
rs.close();
```

## More example



# TODO

* Exact ETAG support 
* Batch process
* Bulk import.
* Support authorize.
* Retry http method.
* Maven Repo and download packages.
* Online JavaDoc.
* Multi Server connection (ex. Consistent Hash)
* exclude logback dependency

* POST /_api/explain
* PUT /_api/simple/near
* PUT /_api/simple/within
* PUT /_api/simple/fulltext
* KVS
* Blueprints

This library does not support admin/_echo

# License

Apache License 2.0

# Author

Twitter: @tamtam180


