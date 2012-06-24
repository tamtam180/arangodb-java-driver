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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Map.Entry;

import org.junit.Test;

import at.orz.arangodb.ArangoException;
import at.orz.arangodb.entity.AdminConfigDescriptionEntity;
import at.orz.arangodb.entity.AdminConfigurationEntity;
import at.orz.arangodb.entity.AdminLogEntity;
import at.orz.arangodb.entity.AdminStatusEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverAdminTest extends BaseTest {

	@Test
	public void test_log_all() throws ArangoException {
		
		AdminLogEntity entity = client.getServerLog(
				null, null, null, null, null, null, null);
		
		assertThat(entity, is(notNullValue()));
		assertThat(entity.getTotalAmount(), is(not(0)));
		assertThat(entity.getLogs().size(), is(entity.getTotalAmount()));
		
		// debug
		for (AdminLogEntity.LogEntry log : entity.getLogs()) {
			System.out.printf("%d\t%d\t%tF %<tT\t%s%n", log.getLid(), log.getLevel(), log.getTimestamp(), log.getText());
		}
		
	}

	@Test
	public void test_log_text() throws ArangoException {
		
		AdminLogEntity entity = client.getServerLog(
				null, null, null, null, null, null, "Fun");
		
		assertThat(entity, is(notNullValue()));
		// debug
		for (AdminLogEntity.LogEntry log : entity.getLogs()) {
			System.out.printf("%d\t%d\t%tF %<tT\t%s%n", log.getLid(), log.getLevel(), log.getTimestamp(), log.getText());
		}
		
	}

	// TODO テスト増やす
	
	@Test
	public void test_status() throws ArangoException {
		
		AdminStatusEntity status = client.getServerStatus();
		
		// debug
		System.out.println(status.getMinorPageFaults());
		System.out.println(status.getMajorPageFaults());
		System.out.println(status.getUserTime());
		System.out.println(status.getSystemTime());
		System.out.println(status.getNumberThreads());
		System.out.println(status.getResidentSize());
		System.out.println(status.getVirtualSize());

	}

	@Test
	public void test_configure() throws ArangoException {
		
		AdminConfigurationEntity conf = client.getServerConfiguration();
		
		// debug
		for (Entry<String, Object> ent: conf.entrySet()) {
			System.out.println(ent);
		}
		
	}
	
	@Test
	public void test_config_description() throws ArangoException {
		
		AdminConfigDescriptionEntity desc = client.getServerConfigurationDescription();
		
		// debug
		for (Entry<String, AdminConfigDescriptionEntity.DescriptionEntry> ent : desc.entrySet()) {
			System.out.printf("%s\t%b\t%s\t%s\t%n", 
					ent.getKey(),
					ent.getValue().isReadonly(),
					ent.getValue().getName(),
					ent.getValue().getType()
					);
			if (ent.getValue().getValues() != null) {
				for (Object v : ent.getValue().getValues()) {
					System.out.println("\t" + v);
				}
			}
		}
		
	}
	
}
