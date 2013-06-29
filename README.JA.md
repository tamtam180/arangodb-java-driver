# at first

ひとまず私がArangoDBをテストするために書いているものです。

一見作業中に見えますが、ほとんどのAPIはコールできます。
クライアント側で複数のサーバを選択する機能やArangoDBサーバがまだ正式リリースしていないAPI等がWORKINGになっています。

!! This is a prototype version. production not ready. !!

# Required

* Java 5 later

# Maven

```
```

# Arango-Java-Driver

Dukeがアボカド食べてる画像が欲しいです。

このプログラムはArangoDBをJavaから操作するためのライブラリです。
主に4つのインターフェースがあります。

* 低レイヤー
    * ArangoDriver
    * Rest-APIとほぼ1:1に対応するレイヤーです。
    * 正常処理以外は全て例外が発生します。
    * **Multithread-safety**です。
* 中レイヤー
    * ArangoClient
    * ArangoDriverを使いやすくしたラッパークラスです。
    * 例えば削除処理で存在しないものを削除してもエラーにならなかったり、
    Collectionを重複生成してもエラーにならなかったりと、一般的な用途に使いやすいインターフェースを提供します。
* 高レイヤー
    * オブジェクト指向のレイヤーです。
    * 各クラスがCRUDになっています。
* JDBC Driver
    * JDBCドライバとして動作します。

ArangoDriverはAPIと1:1に対応する機能を提供し、正常処理以外は全て例外が発生します。
サーバからのレスポンスは戻り値か例外クラスに全て格納されます。
そのため、ArangoDriverは使いやすいとは言えません。

一般的な用途で使いやすいようにしたラッパークラスとしてArangoClientを提供します。
通常はこちらを使ってアプリケーションを書きます。

また、AQLを発行するインターフェースを提供しておりJDBCドライバのような振る舞いを提供します。
ただし、こちらはJDBCDriverとしては **まだ** 動きません。
そのうち対応します。


## 簡単な使い方

クエリを発行して複数のDocumentをとるサンプルは
Example1.javaを参照してください。

## ArangoDriver
``` Java
  ArangoConfigure configure = new ArangoConfigure();
  configure.init();
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

  configure.shutdown();
```

グラフデータを作る。
**ArangoDB-1.1からCollectonに格納する種類がDocumentかGraphかを厳密に判断するようになりました。**


```Java

```

クエリを発行し、JDBCのカーソル操作のようなことをする。

```Java
```


## ArangoClient
## ArangoJDBCDriver

# TODO
* etagのサポート
* パーシャルアップデート
* バッチ処理
* バルクインポート
* 認証処理
* 特定のHTTPメソッドの再実行処理
* Mavenレポジトリの用意
* ダウンロード用パッケージの用意
* 複数サーバの対応(例: ConsistentHashとか)
* **/_admin/echo は実装する予定はいまのところありません。**
* KVS/Blueprintsはドキュメントが整ったら対応します。

* POST /_api/explain
* PUT /_api/simple/near
* PUT /_api/simple/within
* PUT /_api/simple/fulltext

# ライセンス

Apache License 2.0

# 作者

Twitter: @tamtam180



