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

package org.springframework.credhub.core;

import org.reactivestreams.Publisher;
import org.springframework.credhub.core.certificate.ReactiveCredHubCertificateOperations;
import org.springframework.credhub.core.certificate.ReactiveCredHubCertificateTemplate;
import org.springframework.credhub.core.credential.ReactiveCredHubCredentialOperations;
import org.springframework.credhub.core.credential.ReactiveCredHubCredentialTemplate;
import org.springframework.credhub.core.info.ReactiveCredHubInfoOperations;
import org.springframework.credhub.core.info.ReactiveCredHubInfoTemplate;
import org.springframework.credhub.core.interpolation.ReactiveCredHubInterpolationOperations;
import org.springframework.credhub.core.interpolation.ReactiveCredHubInterpolationTemplate;
import org.springframework.credhub.core.permission.ReactiveCredHubPermissionOperations;
import org.springframework.credhub.core.permission.ReactiveCredHubPermissionTemplate;
import org.springframework.credhub.core.permissionV2.ReactiveCredHubPermissionV2Operations;
import org.springframework.credhub.core.permissionV2.ReactiveCredHubPermissionV2Template;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Function;

/**
 * Implements the main interaction with CredHub.
 *
 * @author Scott Frederick
 */
public class ReactiveCredHubTemplate implements ReactiveCredHubOperations {
	private final WebClient webClient;
	private final boolean usingOAuth2;

	/**
	 * Create a new {@link ReactiveCredHubTemplate} using the provided {@link WebClient}.
	 * Intended for internal testing only.
	 *
	 * @param webClient the {@link WebClient} to use for interactions with CredHub
	 */
	public ReactiveCredHubTemplate(WebClient webClient) {
		Assert.notNull(webClient, "webClient must not be null");

		this.webClient = webClient;
		this.usingOAuth2 = false;
	}

	/**
	 * Create a new {@link ReactiveCredHubTemplate} using the provided base URI and
	 * {@link ClientHttpRequestFactory}.
	 *
	 * @param credHubProperties connection properties for the CredHub server
	 * @param clientHttpConnector the {@link ClientHttpConnector} to use when
	 * creating new connections
	 */
	public ReactiveCredHubTemplate(CredHubProperties credHubProperties, ClientHttpConnector clientHttpConnector) {
		Assert.notNull(credHubProperties, "credHubProperties must not be null");
		Assert.notNull(clientHttpConnector, "clientHttpConnector must not be null");

		this.webClient = CredHubWebClientFactory.createWebClient(credHubProperties, clientHttpConnector);
		this.usingOAuth2 = false;
	}

	/**
	 * Create a new {@link ReactiveCredHubTemplate} using the provided base URI and
	 * {@link ClientHttpRequestFactory}.
	 *
	 * @param credHubProperties connection properties for the CredHub server
	 * @param clientHttpConnector the {@link ClientHttpConnector} to use when
	 *                            creating new connections
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientRepository a repository of authorized OAuth2 clients
	 */
	public ReactiveCredHubTemplate(CredHubProperties credHubProperties, ClientHttpConnector clientHttpConnector,
								   ReactiveClientRegistrationRepository clientRegistrationRepository,
								   ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
		Assert.notNull(credHubProperties, "credHubProperties must not be null");
		Assert.notNull(clientHttpConnector, "clientHttpConnector must not be null");
		Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository must not be null");
		Assert.notNull(authorizedClientRepository, "authorizedClientRepository must not be null");

		this.webClient = CredHubWebClientFactory.createWebClient(credHubProperties, clientHttpConnector,
				clientRegistrationRepository, authorizedClientRepository);
		this.usingOAuth2 = true;
	}

	/**
	 * Get the operations for saving, retrieving, and deleting credentials.
	 *
	 * @return the credentials operations
	 */
	@Override
	public ReactiveCredHubCredentialOperations credentials() {
		return new ReactiveCredHubCredentialTemplate(this);
	}

	/**
	 * Get the operations for adding, retrieving, and deleting permissions from a credential.
	 *
	 * @return the permissions operations
	 */
	@Override
	public ReactiveCredHubPermissionOperations permissions() {
		return new ReactiveCredHubPermissionTemplate(this);
	}

	/**
	 * Get the operations for adding, retrieving, and deleting permissions from a credential.
	 *
	 * @return the permissions operations
	 */
	@Override
	public ReactiveCredHubPermissionV2Operations permissionsV2() {
		return new ReactiveCredHubPermissionV2Template(this);
	}

	/**
	 * Get the operations for retrieving, regenerating, and updating certificates.
	 *
	 * @return the certificates operations
	 */
	@Override
	public ReactiveCredHubCertificateOperations certificates() {
		return new ReactiveCredHubCertificateTemplate(this);
	}

	/**
	 * Get the operations for interpolating service binding credentials.
	 *
	 * @return the interpolation operations
	 */
	@Override
	public ReactiveCredHubInterpolationOperations interpolation() {
		return new ReactiveCredHubInterpolationTemplate(this);
	}

	/**
	 * Get the operations for retrieving CredHub server information.
	 *
	 * @return the info operations
	 */
	@Override
	public ReactiveCredHubInfoOperations info() {
		return new ReactiveCredHubInfoTemplate(this);
	}

	/**
	 * Allow interaction with the configured {@link WebClient} not provided
	 * by other methods.
	 *
	 * @param callback wrapper for the callback method
	 * @param <T> the credential implementation type
	 * @return the return value from the callback method
	 */
	@Override
	public <V, T extends Publisher<V>> T doWithWebClient(Function<WebClient, ? extends T> callback) {
		Assert.notNull(callback, "callback must not be null");

		try {
			return callback.apply(webClient);
		}
		catch (HttpStatusCodeException e) {
			throw new CredHubException(e);
		}
	}

	public boolean isUsingOAuth2() {
		return this.usingOAuth2;
	}
}
