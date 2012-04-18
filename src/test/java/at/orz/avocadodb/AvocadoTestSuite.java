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

package at.orz.avocadodb;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 全ての単体テストを実行する。
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
	NegativeTest.class,
	AvocadoDriverVersionTest.class,
	PrimitiveDocumentTest.class,
	AvocadoDriverDocumentTest.class,
	AvocadoDriverCollectionTest.class,
	MyTDD.class,
	AvocadoDriverCursorTest.class,
	AvocadoDriverCursorResultSetTest.class,
	AvocadoDriverKeyValueTest.class,
	AvocadoDriverStoryTest.class
})
public class AvocadoTestSuite {

}
