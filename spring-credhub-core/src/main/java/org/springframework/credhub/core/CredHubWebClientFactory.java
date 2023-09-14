/*
 * Copyright 2016-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.credhub.support.utils.JsonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Factory for creating a {@link WebClient} configured for communication with a CredHub
 * server.
 *
 * @author Mark Paluch
 * @author Scott Frederick
 */
final class CredHubWebClientFactory {

	private CredHubWebClientFactory() {
	}

	/**
	 * Create a {@link WebClient} configured for communication with a CredHub server.
	 * @param properties the CredHub connection properties
	 * @param clientHttpConnector the {@link ClientHttpConnector} to use when creating new
	 * connections
	 * @return a configured {@link WebClient}
	 */
	static WebClient createWebClient(CredHubProperties properties, ClientHttpConnector clientHttpConnector) {
		return buildWebClient(properties.getUrl(), clientHttpConnector).build();
	}

	/**
	 * Create a {@link WebClient} configured for communication with a CredHub server.
	 * @param properties the CredHub connection properties
	 * @param clientHttpConnector the {@link ClientHttpConnector} to use when creating new
	 * connections
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientRepository a repository of OAuth2 authorized clients
	 * @return a configured {@link WebClient}
	 */
	static WebClient createWebClient(CredHubProperties properties, ClientHttpConnector clientHttpConnector,
			ReactiveClientRegistrationRepository clientRegistrationRepository,
			ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
		ReactiveOAuth2AuthorizedClientProvider clientProvider = buildClientProvider(clientHttpConnector);

		DefaultReactiveOAuth2AuthorizedClientManager defaultClientManager = buildClientManager(
				clientRegistrationRepository, authorizedClientRepository, clientProvider);

		return createWebClient(properties, clientHttpConnector, defaultClientManager);
	}

	/**
	 * Create a {@link WebClient} configured for communication with a CredHub server.
	 * @param properties the CredHub connection properties
	 * @param clientHttpConnector the {@link ClientHttpConnector} to use when creating new
	 * connections
	 * @param clientManager an OAuth2 client manager to use to authenticate a client
	 * @return a configured {@link WebClient}
	 */
	static WebClient createWebClient(CredHubProperties properties, ClientHttpConnector clientHttpConnector,
			ReactiveOAuth2AuthorizedClientManager clientManager) {

		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientManager);

		return buildWebClient(properties.getUrl(), clientHttpConnector).filter(oauth)
			.defaultRequest((requestHeadersSpec) -> requestHeadersSpec
				.attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction
					.clientRegistrationId(properties.getOauth2().getRegistrationId())))
			.build();
	}

	private static ReactiveOAuth2AuthorizedClientProvider buildClientProvider(ClientHttpConnector clientHttpConnector) {
		return ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
			.authorizationCode()
			.clientCredentials((b) -> b.accessTokenResponseClient(buildTokenResponseClient(clientHttpConnector)))
			.build();
	}

	private static WebClientReactiveClientCredentialsTokenResponseClient buildTokenResponseClient(
			ClientHttpConnector clientHttpConnector) {
		WebClientReactiveClientCredentialsTokenResponseClient tokenResponseClient = new WebClientReactiveClientCredentialsTokenResponseClient();
		tokenResponseClient.setWebClient(WebClient.builder().clientConnector(clientHttpConnector).build());
		return tokenResponseClient;
	}

	private static DefaultReactiveOAuth2AuthorizedClientManager buildClientManager(
			ReactiveClientRegistrationRepository clientRegistrationRepository,
			ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
			ReactiveOAuth2AuthorizedClientProvider clientProvider) {
		DefaultReactiveOAuth2AuthorizedClientManager clientManager = new DefaultReactiveOAuth2AuthorizedClientManager(
				clientRegistrationRepository, authorizedClientRepository);
		clientManager.setAuthorizedClientProvider(clientProvider);
		return clientManager;
	}

	private static WebClient.Builder buildWebClient(String baseUri, ClientHttpConnector clientHttpConnector) {
		ExchangeStrategies strategies = ExchangeStrategies.builder().codecs((configurer) -> {
			ObjectMapper mapper = JsonUtils.buildObjectMapper();

			CodecConfigurer.DefaultCodecs dc = configurer.defaultCodecs();
			dc.jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
			dc.jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
		}).build();

		return WebClient.builder()
			.clientConnector(clientHttpConnector)
			.baseUrl(baseUri)
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.exchangeStrategies(strategies);
	}

}
