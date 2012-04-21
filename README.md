# at first

ひとまず私がAvocadoDBをテストするために書いているものです。

!! This is a prototype version !!
※このプログラムはプロトタイプです。
エラー処理とかはまだまだ全然書いていません。

# Avocado-Java-Driver

 DukeがAvocado食べてる画像が欲しいです。

このプログラムはAvocadoDBをJavaから操作するためのライブラリです。
主に3つのインターフェースがあります。

低レイヤー
  AvocadoDriver
    Rest-APIとほぼ1:1に対応するレイヤーです。

高レイヤー
  AvocadoClient
    オブジェクト指向のレイヤーです。
    各クラスがCRUDになっています。
    
JDBC Driver
  JDBCドライバとして動作します。

## 簡単な使い方

クエリを発行して複数のDocumentをとるサンプルは
Example1.javaを参照してください。

``` Java
  AvocadoConfigure configure = new AvocadoConfigure();
  AvocadoDriver client = new AvocadoDriver(configure);
  
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

  client.shutdown();
```

## AvocadoDriver
## AvocadoClient
## AvocadoJDBCDriver

## ライブラリの実装状況
* Base/Configure (WORKING)
* Base/HTTP (DONE)
* Base/ErrorCode (未着手)
* Base/ServerLocator:複数ホストの対応(未着手)
* AvocadoDriver (WORKING)
    * REST/Collection (DONE)
    * REST/Document (DONE)
    * REST/Index(DONE)
    * REST/Key
    * REST/Cursor
    * REST/Edge
    * UnitTest (WORKING)
    * JavaDoc (WORKING)
* AvocadoClient (未着手)
* AvocadoJDBCDriver (未着手)
* Document (未着手)

