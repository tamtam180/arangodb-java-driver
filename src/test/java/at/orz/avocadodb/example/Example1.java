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

package at.orz.avocadodb.example;

import java.util.HashMap;

import at.orz.avocadodb.AvocadoConfigure;
import at.orz.avocadodb.AvocadoDriver;
import at.orz.avocadodb.AvocadoException;
import at.orz.avocadodb.CursorResultSet;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class Example1 {
	
	public static class ExampleEntity {
		public String name;
		public String gender;
		public int age;
	}
	
	public static void main(String[] args) {

		AvocadoConfigure configure = new AvocadoConfigure();
		AvocadoDriver driver = new AvocadoDriver(configure);
		
		try {
			for (int i = 0; i < 1000; i++) {
				ExampleEntity value = new ExampleEntity();
				value.name = "TestUser" + i;
				switch (i % 3) {
				case 0: value.gender = "MAN"; break;
				case 1: value.gender = "WOMAN"; break;
				case 2: value.gender = "OTHER"; break;
				}
				value.age = (int) (Math.random() * 100) + 10;
				driver.createDocument("example_collection1", value, true, null, null);
			}
			
			HashMap<String, Object> bindVars = new HashMap<String, Object>();
			bindVars.put("gender", "WOMAN");
			
			CursorResultSet<ExampleEntity> rs = driver.executeQueryWithResultSet(
					"select t from example_collection1 t where t.age >= 20 && t.age < 30 && t.gender == @gender@", 
					bindVars, ExampleEntity.class, true, 10);
			
			System.out.println(rs.getTotalCount());
			for (ExampleEntity obj: rs) {
				System.out.printf("  %15s(%5s): %d%n", obj.name, obj.gender, obj.age);
			}
			
		} catch (AvocadoException e) {
			e.printStackTrace();
		} finally {
			driver.shutdown();
		}
		
	}

}
