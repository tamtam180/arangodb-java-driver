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

/**
 * Configure of AvocadoDB
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AvocadoConfigure {

	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final int DEFAULT_CLIENT_PORT = 8529;
	private static final int DEFAULT_ADMIN_PORT = 8529;
	
	private static final int DEFAULT_MAX_PER_CONNECTION = 20; // 2;
	private static final int DEFAULT_MAX_CONNECTION = 20;
	
	int clinetPort;
	int adminPort;
	String host;
	int connectionTimeout;
	int timeout;
	boolean autoUnknownCollections = false;
	
	int maxTotalConnection;
	int maxPerConnection;
	
	String proxyHost;
	int proxyPort;
	
	public AvocadoConfigure() {
		this.clinetPort = DEFAULT_CLIENT_PORT;
		this.adminPort = DEFAULT_ADMIN_PORT;
		this.host = DEFAULT_HOST;
	}
	
}
