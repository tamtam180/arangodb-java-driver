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

/**
 * Configure of ArangoDB
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoConfigure {

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
	
	public ArangoConfigure() {
		this.clinetPort = DEFAULT_CLIENT_PORT;
		this.adminPort = DEFAULT_ADMIN_PORT;
		this.maxPerConnection = DEFAULT_MAX_PER_CONNECTION;
		this.maxTotalConnection = DEFAULT_MAX_CONNECTION;
		this.host = DEFAULT_HOST;
	}

	public static String getDefaultHost() {
		return DEFAULT_HOST;
	}

	public static int getDefaultClientPort() {
		return DEFAULT_CLIENT_PORT;
	}

	public static int getDefaultAdminPort() {
		return DEFAULT_ADMIN_PORT;
	}

	public static int getDefaultMaxPerConnection() {
		return DEFAULT_MAX_PER_CONNECTION;
	}

	public static int getDefaultMaxConnection() {
		return DEFAULT_MAX_CONNECTION;
	}

	public int getClinetPort() {
		return clinetPort;
	}

	public int getAdminPort() {
		return adminPort;
	}

	public String getHost() {
		return host;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public int getTimeout() {
		return timeout;
	}

	public boolean isAutoUnknownCollections() {
		return autoUnknownCollections;
	}

	public int getMaxTotalConnection() {
		return maxTotalConnection;
	}

	public int getMaxPerConnection() {
		return maxPerConnection;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setClinetPort(int clinetPort) {
		this.clinetPort = clinetPort;
	}

	public void setAdminPort(int adminPort) {
		this.adminPort = adminPort;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setAutoUnknownCollections(boolean autoUnknownCollections) {
		this.autoUnknownCollections = autoUnknownCollections;
	}

	public void setMaxTotalConnection(int maxTotalConnection) {
		this.maxTotalConnection = maxTotalConnection;
	}

	public void setMaxPerConnection(int maxPerConnection) {
		this.maxPerConnection = maxPerConnection;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
	
	
	
}
