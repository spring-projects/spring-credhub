/*
 * Copyright 2016-2026 the original author or authors.
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

import java.util.function.Consumer;

import org.springframework.credhub.core.certificate.CredHubCertificateOperations;
import org.springframework.credhub.core.credential.CredHubCredentialOperations;
import org.springframework.credhub.core.info.CredHubInfoOperations;
import org.springframework.credhub.core.interpolation.CredHubInterpolationOperations;
import org.springframework.credhub.core.permission.CredHubPermissionOperations;
import org.springframework.credhub.core.permissionV2.CredHubPermissionV2Operations;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

/**
 * The main entry point for interacting with a CredHub server using a {@link RestClient}.
 * This is the {@link RestClient}-based successor to {@link CredHubTemplate}.
 * <p>
 * Instances are created with a {@link #builder() builder}, in the style of
 * {@link RestClient#builder()}:
 *
 * <pre class="code">
 * CredHubClient client = CredHubClient.builder()
 *     .baseUrl("https://credhub.example.com:8844")
 *     .requestFactory(requestFactory)
 *     .requestInterceptor(authenticationInterceptor)
 *     .build();
 * client.credentials().getByName(name, PasswordCredential.class);
 * </pre>
 * <p>
 * Error responses from CredHub are translated to {@link CredHubException} for every
 * operation, so there is no callback-based error handling to remember when using
 * {@link #restClient()} directly.
 *
 * @author Roy Clarkson
 * @since 4.2.0
 */
public interface CredHubClient {

	/**
	 * Get the operations for saving, retrieving, and deleting credentials.
	 * @return the credentials operations
	 */
	CredHubCredentialOperations credentials();

	/**
	 * Get the operations for adding, retrieving, and deleting credential permissions
	 * using the CredHub v1 permissions API.
	 * @return the v1 permissions operations
	 */
	CredHubPermissionOperations permissions();

	/**
	 * Get the operations for adding, retrieving, updating, and deleting credential
	 * permissions using the CredHub v2 permissions API.
	 * @return the v2 permissions operations
	 */
	CredHubPermissionV2Operations permissionsV2();

	/**
	 * Get the operations for managing certificates and their versions.
	 * @return the certificate operations
	 */
	CredHubCertificateOperations certificates();

	/**
	 * Get the operations for interpolating service binding credentials.
	 * @return the interpolation operations
	 */
	CredHubInterpolationOperations interpolation();

	/**
	 * Get the operations for retrieving information about the CredHub server.
	 * @return the information operations
	 */
	CredHubInfoOperations info();

	/**
	 * Get the underlying {@link RestClient}, already configured for the CredHub server,
	 * for interactions not provided by other methods. This replaces the callback-based
	 * {@code doWithRest} and {@code doWithWebClient} escape hatches.
	 * @return the configured {@link RestClient}
	 */
	RestClient restClient();

	/**
	 * Create a new {@link Builder}.
	 * @return a new builder
	 */
	static Builder builder() {
		return new DefaultCredHubClient.DefaultBuilder();
	}

	/**
	 * A mutable builder for creating a {@link CredHubClient}.
	 */
	interface Builder {

		/**
		 * Set the base URL of the CredHub server. Required.
		 * @param baseUrl the base URL, for example
		 * {@code https://credhub.example.com:8844}
		 * @return this builder
		 */
		Builder baseUrl(String baseUrl);

		/**
		 * Set the {@link ClientHttpRequestFactory} used to create connections, for
		 * example to trust a custom certificate authority.
		 * @param requestFactory the request factory
		 * @return this builder
		 */
		Builder requestFactory(ClientHttpRequestFactory requestFactory);

		/**
		 * Add an interceptor applied to every request, typically to authenticate.
		 * @param interceptor the interceptor
		 * @return this builder
		 */
		Builder requestInterceptor(ClientHttpRequestInterceptor interceptor);

		/**
		 * Authenticate requests with OAuth2, using the {@code client_credentials} grant
		 * and the client registration with the given ID. The access tokens are managed by
		 * a manager created from the given repositories, which calls the token server
		 * with the {@link #requestFactory(ClientHttpRequestFactory) request factory}.
		 * @param registrationId the ID of the client registration to authorize with
		 * @param clientRegistrationRepository a repository of OAuth2 client registrations
		 * @param authorizedClientRepository a repository of authorized OAuth2 clients
		 * @return this builder
		 */
		Builder oauth2(String registrationId, ClientRegistrationRepository clientRegistrationRepository,
				OAuth2AuthorizedClientRepository authorizedClientRepository);

		/**
		 * Authenticate requests with OAuth2, using the {@code client_credentials} grant
		 * and the client registration with the given ID, keeping access tokens in the
		 * given service. Unlike the overload that takes an
		 * {@link OAuth2AuthorizedClientRepository}, this does not need a current servlet
		 * request, so the client can be used from any thread, such as application
		 * start-up code or a background task.
		 * @param registrationId the ID of the client registration to authorize with
		 * @param clientRegistrationRepository a repository of OAuth2 client registrations
		 * @param authorizedClientService the service that stores authorized clients
		 * @return this builder
		 */
		Builder oauth2(String registrationId, ClientRegistrationRepository clientRegistrationRepository,
				OAuth2AuthorizedClientService authorizedClientService);

		/**
		 * Authenticate requests with OAuth2, using the client registration with the given
		 * ID and an access token obtained from the given manager.
		 * @param registrationId the ID of the client registration to authorize with
		 * @param clientRegistrationRepository a repository of OAuth2 client registrations
		 * @param clientManager an OAuth2 authorized client manager
		 * @return this builder
		 */
		Builder oauth2(String registrationId, ClientRegistrationRepository clientRegistrationRepository,
				OAuth2AuthorizedClientManager clientManager);

		/**
		 * Customize the underlying {@link RestClient.Builder} after the CredHub defaults
		 * have been applied.
		 * @param customizer the customizer
		 * @return this builder
		 */
		Builder restClientCustomizer(Consumer<RestClient.Builder> customizer);

		/**
		 * Build the {@link CredHubClient}.
		 * @return a new client
		 */
		CredHubClient build();

	}

}
