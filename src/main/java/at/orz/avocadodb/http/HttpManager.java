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

package at.orz.avocadodb.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.orz.avocadodb.AvocadoException;
import at.orz.avocadodb.http.HttpRequestEntity.RequestType;
import at.orz.avocadodb.util.IOUtils;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class HttpManager {

	private Logger logger = LoggerFactory.getLogger(HttpManager.class);
	
	private ThreadSafeClientConnManager cm;
	private HttpClient client;
	
	private int defaultMaxPerRoute = 20;
	private int maxTotal = 20;
	private String proxyHost;
	private int proxyPort;

	public HttpManager() {
	}
	
	public HttpManager setDefaultMaxPerRoute(int max) {
		defaultMaxPerRoute = max;
		return this;
	}
	public HttpManager setMaxTotal(int max) {
		maxTotal = max;
		return this;
	}
	
	public HttpManager setProxyHost(String host) {
		this.proxyHost = host;
		return this;
	}
	public HttpManager setProxyPort(int port) {
		this.proxyPort = port;
		return this;
	}
	
	public void init() {
		cm = new ThreadSafeClientConnManager();
		cm.setDefaultMaxPerRoute(defaultMaxPerRoute);
		cm.setMaxTotal(maxTotal);
		client = new DefaultHttpClient(cm);
		// TODO KeepAlive Strategy
		// TODO Proxy
		if (proxyHost != null && proxyPort != 0) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
	}
	
	public void destroy() {
		if (cm != null) {
			cm.shutdown();
		}
	}

	
	public HttpResponseEntity doGet(String url) throws AvocadoException {
		return doGet(url, null);
	}
	public HttpResponseEntity doGet(String url, Map<String, Object> headers, Map<String, Object> params) throws AvocadoException {
		return doHeadGetDelete(RequestType.GET, url, headers, params);
	}
	public HttpResponseEntity doGet(String url, Map<String, Object> params) throws AvocadoException {
		return doHeadGetDelete(RequestType.GET, url, null, params);
	}
	public HttpResponseEntity doHead(String url, Map<String, Object> params) throws AvocadoException {
		return doHeadGetDelete(RequestType.HEAD, url, null, params);
	}
	public HttpResponseEntity doDelete(String url, Map<String, Object> params) throws AvocadoException {
		return doHeadGetDelete(RequestType.DELETE, url, null, params);
	}
	public HttpResponseEntity doHeadGetDelete(RequestType type, String url, Map<String, Object> headers, Map<String, Object> params) throws AvocadoException {
		HttpRequestEntity requestEntity = new HttpRequestEntity();
		requestEntity.type = type;
		requestEntity.url = url;
		requestEntity.headers = headers;
		requestEntity.parameters = params;
		return execute(requestEntity);
	}

	public HttpResponseEntity doPost(String url, Map<String, Object> headers, Map<String, Object> params, String bodyText) throws AvocadoException {
		return doPostPut(RequestType.POST, url, headers, params, bodyText);
	}

	public HttpResponseEntity doPost(String url, Map<String, Object> params, String bodyText) throws AvocadoException {
		return doPostPut(RequestType.POST, url, null, params, bodyText);
	}

	public HttpResponseEntity doPut(String url, Map<String, Object> headers, Map<String, Object> params, String bodyText) throws AvocadoException {
		return doPostPut(RequestType.PUT, url, headers, params, bodyText);
	}
	
	public HttpResponseEntity doPut(String url, Map<String, Object> params, String bodyText) throws AvocadoException {
		return doPostPut(RequestType.PUT, url, null, params, bodyText);
	}
	
	private HttpResponseEntity doPostPut(RequestType type, String url, Map<String, Object> headers, Map<String, Object> params, String bodyText) throws AvocadoException {
		HttpRequestEntity requestEntity = new HttpRequestEntity();
		requestEntity.type = type;
		requestEntity.url = url;
		requestEntity.headers = headers;
		requestEntity.parameters = params;
		requestEntity.bodyText = bodyText;
		return execute(requestEntity);
	}
	
	/**
	 * 
	 * @param requestEntity
	 * @return
	 * @throws AvocadoException
	 */
	public HttpResponseEntity execute(HttpRequestEntity requestEntity) throws AvocadoException {
		
		String url = buildUrl(requestEntity);
		logger.debug("http-{}: url={}", requestEntity.type, url);
		
		HttpRequestBase request = null;
		switch (requestEntity.type) {
		case GET:
			request = new HttpGet(url);
			break;
		case POST:
			HttpPost post = new HttpPost(url);
			configureBodyParams(requestEntity, post);
			request = post;
			break;
		case PUT:
			HttpPut put = new HttpPut(url);
			configureBodyParams(requestEntity, put);
			request = put;
			break;
		case HEAD:
			request = new HttpHead(url);
			break;
		case DELETE:
			request = new HttpDelete(url);
			break;
		}
		
		// common-header
		request.setHeader("User-Agent", "Mozilla/5.0 (compatible; AdovadoDB-JavaDriver/1.0; +http://mt.orz.at/)"); // TODO: 定数化
		
		// optinal-headers
		if (requestEntity.headers != null) {
			for (Entry<String, Object> keyValue: requestEntity.headers.entrySet()) {
				request.setHeader(keyValue.getKey(), keyValue.getValue().toString());
			}
		}

		HttpResponse response = null;
		try {
			
			response = client.execute(request);
			if (response == null) {
				return null;
			}
			
			HttpResponseEntity responseEntity = new HttpResponseEntity();
			
			// http status
			StatusLine status = response.getStatusLine();
			responseEntity.statusCode = status.getStatusCode();
			responseEntity.statusPhrase = status.getReasonPhrase();

			logger.debug("http-{}: statusCode={}", requestEntity.type, responseEntity.statusCode);
			
			// ヘッダの処理
			Header etagHeader = response.getLastHeader("etag");
			if (etagHeader != null) {
				responseEntity.etag = Long.parseLong(etagHeader.getValue().replace("\"", ""));
			}
			
			// レスポンスの取得
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// Close stream in this method.
				responseEntity.text = IOUtils.toString(entity.getContent());
				logger.debug("http-{}: text={}", requestEntity.type, responseEntity.text);
			}
			
			return responseEntity;
			
		} catch (ClientProtocolException e) {
			// TODO
			throw new AvocadoException(e);
		} catch (IOException e) {
			// TODO
			throw new AvocadoException(e);
		}
		
	}
	
	private static String buildUrl(HttpRequestEntity requestEntity) {
		if (requestEntity.parameters != null && !requestEntity.parameters.isEmpty()) {
			String paramString = URLEncodedUtils.format(toList(requestEntity.parameters), "utf-8");
			if (requestEntity.url.contains("?")) {
				return requestEntity.url + "&" + paramString;
			} else {
				return requestEntity.url + "?" + paramString;
			}
		}
		return requestEntity.url;
	}
	
	private static List<NameValuePair> toList(Map<String, Object> parameters) {
		ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>(parameters.size());
		for (Entry<String, Object> param: parameters.entrySet()) {
			if (param.getValue() != null) {
				paramList.add(new BasicNameValuePair(param.getKey(), param.getValue().toString()));
			}
		}
		return paramList;
	}
	
	private static void configureBodyParams(HttpRequestEntity requestEntity, HttpEntityEnclosingRequestBase request) {
		
		try {
			if (requestEntity.bodyText != null) {
				request.setEntity(new StringEntity(requestEntity.bodyText, "application/json", "utf-8"));
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static boolean is400Error(AvocadoException e) {
		return e.getCode() == HttpStatus.SC_BAD_REQUEST;
	}
	public static boolean is404Error(AvocadoException e) {
		return e.getCode() == HttpStatus.SC_NOT_FOUND;
	}
	public static boolean is412Error(AvocadoException e) {
		return e.getCode() == HttpStatus.SC_PRECONDITION_FAILED;
	}

	public static boolean is200(HttpResponseEntity res) {
		return res.getStatusCode() == HttpStatus.SC_OK;
	}
	public static boolean is400Error(HttpResponseEntity res) {
		return res.getStatusCode() == HttpStatus.SC_BAD_REQUEST;
	}
	public static boolean is404Error(HttpResponseEntity res) {
		return res.getStatusCode() == HttpStatus.SC_NOT_FOUND;
	}
	public static boolean is412Error(HttpResponseEntity res) {
		return res.getStatusCode() == HttpStatus.SC_PRECONDITION_FAILED;
	}
	
	
}
