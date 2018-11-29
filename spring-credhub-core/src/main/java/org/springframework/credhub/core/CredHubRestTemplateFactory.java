/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.core;

import static java.util.Collections.singletonList;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.credhub.support.utils.JsonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Factory for creating a {@link RestTemplate} configured for communication with
 * a CredHub server.
 *
 * @author Scott Frederick
 * @author Daniel Lavoie
 */
class CredHubRestTemplateFactory {

	/**
	 * Create a {@link RestTemplate} configured for communication with a CredHub server.
	 *
	 * @param properties               CredHub connection properties
	 * @param clientHttpRequestFactory the {@link ClientHttpRequestFactory} to use when
	 *                                 creating new connections
	 * @return a configured {@link RestTemplate}
	 */
	static RestTemplate createRestTemplate(CredHubProperties properties,
										   ClientHttpRequestFactory clientHttpRequestFactory) {
		RestTemplate restTemplate = new RestTemplate();

		configureRestTemplate(restTemplate, properties.getUrl(), clientHttpRequestFactory);

		return restTemplate;
	}

	/**
	 * Create a {@link RestTemplate} configured for communication with a CredHub server.
	 *
	 * @param properties                   CredHub connection properties
	 * @param clientHttpRequestFactory     the {@link ClientHttpRequestFactory} to use when
	 *                                     creating new connections
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientService      a repository of authorized OAuth2 clients
	 * @return a configured {@link RestTemplate}
	 */
	static RestTemplate createRestTemplate(CredHubProperties properties,
										   ClientHttpRequestFactory clientHttpRequestFactory,
										   ClientRegistrationRepository clientRegistrationRepository,
										   OAuth2AuthorizedClientService authorizedClientService) {
		RestTemplate restTemplate = new RestTemplate();

		configureRestTemplate(restTemplate, properties.getUrl(), clientHttpRequestFactory);
		configureOAuth2(restTemplate, clientHttpRequestFactory,
				properties.getOauth2().getRegistrationId(),
				clientRegistrationRepository,
				authorizedClientService);

		return restTemplate;
	}

	/**
	 * Configure a {@link RestTemplate} for communication with a CredHub server.
	 *
	 * @param restTemplate             an existing {@link RestTemplate} to configure
	 * @param baseUri                  the base URI for the CredHub server
	 * @param clientHttpRequestFactory the {@link ClientHttpRequestFactory} to use when
	 *                                 creating new connections
	 */
	private static void configureRestTemplate(RestTemplate restTemplate, String baseUri,
											  ClientHttpRequestFactory clientHttpRequestFactory) {
		restTemplate.setRequestFactory(clientHttpRequestFactory);
		restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUri));
		restTemplate.getInterceptors().add(new CredHubRequestInterceptor());
		restTemplate.setMessageConverters(Arrays.asList(
				new ByteArrayHttpMessageConverter(),
				new StringHttpMessageConverter(),
				new MappingJackson2HttpMessageConverter(JsonUtils.buildObjectMapper())));
	}

	/**
	 * Configure OAuth2 features of a {@link RestTemplate}.
	 *
	 * @param restTemplate                 an existing {@link RestTemplate} to configure
	 * @param clientHttpRequestFactory     the {@link ClientHttpRequestFactory} to use when
	 *                                     creating new connections
	 * @param clientId                     the OAuth2 client ID for authentication
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientService      a repository of authorized OAuth2 clients
	 */
	private static void configureOAuth2(RestTemplate restTemplate,
										ClientHttpRequestFactory clientHttpRequestFactory,
										String clientId,
										ClientRegistrationRepository clientRegistrationRepository,
										OAuth2AuthorizedClientService authorizedClientService) {
		ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientId);

		RestOperations tokenServerRestTemplate = createTokenServerRestTemplate(clientHttpRequestFactory);

		restTemplate.getInterceptors()
				.add(new CredHubOAuth2RequestInterceptor(tokenServerRestTemplate,
						clientRegistration, authorizedClientService));
	}

	private static RestTemplate createTokenServerRestTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
		RestTemplate restOperations = new RestTemplate(Arrays.asList(
				new FormHttpMessageConverter(),
				new OAuth2AccessTokenResponseHttpMessageConverter()));
		restOperations.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		restOperations.setRequestFactory(clientHttpRequestFactory);
		return restOperations;
	}

	/**
	 * A request interceptor that sets headers common to all CredHub requests.
	 */
	private static class CredHubRequestInterceptor implements ClientHttpRequestInterceptor {
		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body,
											ClientHttpRequestExecution execution) throws IOException {
			HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);

			HttpHeaders headers = requestWrapper.getHeaders();
			headers.setAccept(singletonList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);

			return execution.execute(requestWrapper, body);
		}
	}
}
