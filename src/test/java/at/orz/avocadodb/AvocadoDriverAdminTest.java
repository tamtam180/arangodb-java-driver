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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import at.orz.avocadodb.entity.AdminLogEntity;
import at.orz.avocadodb.entity.AdminStatusEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AvocadoDriverAdminTest extends BaseTest {

	@Test
	public void test_log_all() throws AvocadoException {
		
		AdminLogEntity entity = client.getAdminLog(
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
	public void test_log_text() throws AvocadoException {
		
		AdminLogEntity entity = client.getAdminLog(
				null, null, null, null, null, null, "Fun");
		
		assertThat(entity, is(notNullValue()));
		// debug
		for (AdminLogEntity.LogEntry log : entity.getLogs()) {
			System.out.printf("%d\t%d\t%tF %<tT\t%s%n", log.getLid(), log.getLevel(), log.getTimestamp(), log.getText());
		}
		
	}

	// TODO テスト増やす
	
	@Test
	public void test_status() throws AvocadoException {
		
		AdminStatusEntity status = client.getStatus();
		
		// debug
		System.out.println(status.getMinorPageFaults());
		System.out.println(status.getMajorPageFaults());
		System.out.println(status.getUserTime());
		System.out.println(status.getSystemTime());
		System.out.println(status.getNumberThreads());
		System.out.println(status.getResidentSize());
		System.out.println(status.getVirtualSize());

	}
	
}
