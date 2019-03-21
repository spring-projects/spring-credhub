/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.configuration;

import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.ReactiveCredHubOperations;
import org.springframework.credhub.core.ReactiveCredHubTemplate;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;

/**
 * Factory for {@link CredHubTemplate} used to communicate with CredHub.
 *
 * @author Scott Frederick
 * @author Daniel Lavoie
 */
public class CredHubTemplateFactory {

	/**
	 * Create a {@link CredHubTemplate} for interaction with a CredHub server.
	 *
	 * @param credHubProperties connection properties
	 * @param clientOptions connection options
	 * @return a {@code CredHubTemplate}
	 */
	public CredHubTemplate credHubTemplate(CredHubProperties credHubProperties,
										   ClientOptions clientOptions) {
		return new CredHubTemplate(credHubProperties, clientHttpRequestFactory(clientOptions));
	}

	/**
	 * Create a {@link CredHubTemplate} for interaction with a CredHub server
	 * using OAuth2 for authentication.
	 *
	 * @param credHubProperties connection properties
	 * @param clientOptions connection options
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientService  a repository of authorized OAuth2 clients
	 * @return a {@code CredHubTemplate}
	 */
	public CredHubTemplate credHubTemplate(CredHubProperties credHubProperties,
										   ClientOptions clientOptions,
										   ClientRegistrationRepository clientRegistrationRepository,
										   OAuth2AuthorizedClientService authorizedClientService) {
		return new CredHubTemplate(credHubProperties, clientHttpRequestFactory(clientOptions),
				clientRegistrationRepository, authorizedClientService);
	}

	/**
	 * Create a {@link ReactiveCredHubTemplate} for interaction with a CredHub server.
	 *
	 * @param credHubProperties connection properties
	 * @param clientOptions connection options
	 * @return a {@code ReactiveCredHubTemplate}
	 */
	public ReactiveCredHubTemplate reactiveCredHubTemplate(CredHubProperties credHubProperties,
														   ClientOptions clientOptions) {
		return new ReactiveCredHubTemplate(credHubProperties, clientHttpConnector(clientOptions));
	}

	/**
	 * Create a {@link ReactiveCredHubTemplate} for interaction with a CredHub server
	 * using OAuth2 for authentication.
	 *
	 * @param credHubProperties connection properties
	 * @param clientOptions connection options
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientRepository a repository of OAuth2 client authorizations
	 * @return a {@code ReactiveCredHubTemplate}
	 */
	public ReactiveCredHubOperations reactiveCredHubTemplate(CredHubProperties credHubProperties,
															 ClientOptions clientOptions,
															 ReactiveClientRegistrationRepository clientRegistrationRepository,
															 ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
		return new ReactiveCredHubTemplate(credHubProperties, clientHttpConnector(clientOptions),
				clientRegistrationRepository, authorizedClientRepository);
	}

	/**
	 * Create a {@link ClientHttpRequestFactory}.
	 *
	 * @param clientOptions options for creating the client connection
	 * @return the {@link ClientHttpRequestFactory} instance.
	 */
	private ClientHttpRequestFactory clientHttpRequestFactory(ClientOptions clientOptions) {
		return ClientHttpRequestFactoryFactory.create(clientOptions);
	}

	/**
	 * Create a {@link ClientHttpRequestFactory}.
	 *
	 * @param clientOptions options for creating the client connection
	 * @return the {@link ClientHttpRequestFactory} instance.
	 */
	private ClientHttpConnector clientHttpConnector(ClientOptions clientOptions) {
		return ClientHttpConnectorFactory.create(clientOptions);
	}
}
