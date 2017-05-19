/*
 *
 *  * Copyright 2013-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.credhub.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.springframework.web.util.UriTemplateHandler;

/**
 * Factory for creating a {@link RestTemplate} configured for communication with
 * a CredHub server.
 *
 * @author Scott Frederick
 */
public class CredHubClient {
	/**
	 * Create a {@link RestTemplate} configured for communication with a CredHub server.
	 *
	 * @param baseUri the base URI for the CredHub server
	 * @param clientHttpRequestFactory the {@link ClientHttpRequestFactory} to use when
	 * creating new connections
	 * @return a configured {@link RestTemplate}
	 */
	public static RestTemplate createRestTemplate(String baseUri,
			ClientHttpRequestFactory clientHttpRequestFactory) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(clientHttpRequestFactory);
		restTemplate.setUriTemplateHandler(createUriTemplateHandler(baseUri));
		restTemplate.setMessageConverters(createMessageConverters());
		return restTemplate;
	}

	/**
	 * Create a {@link UriTemplateHandler} that prefixes all {@link RestTemplate} calls
	 * with the configured {@literal baseUri}.
	 *
	 * @param baseUri the base URI for the CredHub server
	 * @return a configured {@link UriTemplateHandler}
	 */
	private static DefaultUriTemplateHandler createUriTemplateHandler(String baseUri) {
		DefaultUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
		uriTemplateHandler.setBaseUrl(baseUri);
		return uriTemplateHandler;
	}

	/**
	 * Create the {@link HttpMessageConverter}s necessary to build request to
	 * and parse responses from CredHub.
	 *
	 * @return the list of {@link HttpMessageConverter}s
	 */
	private static List<HttpMessageConverter<?>> createMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>(3);
		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(new MappingJackson2HttpMessageConverter(JsonUtils.buildObjectMapper()));

		return messageConverters;
	}
}
