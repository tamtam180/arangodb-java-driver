/*
 * Copyright (C) 2012,2013 tamtam180
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

package at.orz.arangodb.entity;

import java.util.Map;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class UserEntity extends BaseEntity {

	String user;
	String password;
	boolean active;
	Map<String, Object> extra;
	
	public String getUser() {
		return user;
	}
	public String getPassword() {
		return password;
	}
	public boolean isActive() {
		return active;
	}
	public Map<String, Object> getExtra() {
		return extra;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public void setExtra(Map<String, Object> extra) {
		this.extra = extra;
	}
	
}
