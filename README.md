# at first

ひとまず私がArangoDBをテストするために書いているものです。

!! This is a prototype version !!

※このプログラムはプロトタイプです。
エラー処理とかはまだまだ全然書いていません。

# Arango-Java-Driver

 Dukeがアボカド食べてる画像が欲しいです。

このプログラムはArangoDBをJavaから操作するためのライブラリです。
主に3つのインターフェースがあります。

低レイヤー
  ArangoDriver
    Rest-APIとほぼ1:1に対応するレイヤーです。

高レイヤー
  ArangoClient
    オブジェクト指向のレイヤーです。
    各クラスがCRUDになっています。
    
JDBC Driver
  JDBCドライバとして動作します。

## 簡単な使い方

クエリを発行して複数のDocumentをとるサンプルは
Example1.javaを参照してください。

``` Java
  ArangoConfigure configure = new ArangoConfigure();
  configure.init();
  ArangoDriver client = new ArangoDriver(configure);
  
  String collectionName = "mytest";
  TestComplexEntity01 value = new TestComplexEntity01("name", "desc", 10); // any POJO class

  // Create Collection
  CollectionEntity collection = client.createCollection(collectionName, false, Mode.DUP_GET);
  // Create Document
  DocumentEntity<TestComplexEntity01> ret1 = client.createDocument(collectionName, value, null, null, null);
  String documentHandle = ret1.getDocumentHandle();
  
  // Get Document
  DocumentEntity<TestComplexEntity01> ret2 =
    client.getDocument(documentHandle, TestComplexEntity01.class, null);

  // Delete Document
  driver.deleteDocument(documentHandle, -1, DeletePolicy.LAST, Mode.RAISE_ERROR);  

  configure.shutdown();
```

## ArangoDriver
## ArangoClient
## ArangoJDBCDriver

## ライブラリの実装状況
* Base/Configure (WORKING)
* Base/HTTP (DONE)
* Base/ErrorCode (未着手)
* Base/ServerLocator:複数ホストの対応(未着手)
* Base/ErrorHandling (未着手)
* ArangoDriver (WORKING)
    * REST/Collection (DONE)
    * REST/Document (DONE)
    * REST/Index (DONE)
    * REST/Key
    * REST/Cursor (DONE)
    * REST/Edge (DONE)
    * REST/Graph
    * REST/Admin
    * UnitTest (WORKING)
    * JavaDoc (WORKING)
* ArangoClient (未着手)
* ArangoJDBCDriver (未着手)
* Document (未着手)

