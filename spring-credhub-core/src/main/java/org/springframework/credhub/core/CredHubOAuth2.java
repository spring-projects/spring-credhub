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

import java.util.Collections;

import org.jspecify.annotations.Nullable;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestClient;

/**
 * Support for authenticating a {@link CredHubClient} with OAuth2. Requests are authorized
 * with the {@code client_credentials} grant: the access token is obtained from the
 * {@link OAuth2AuthorizedClientManager} for the configured client registration and sent
 * as a bearer token.
 *
 * @author Roy Clarkson
 */
final class CredHubOAuth2 {

	private CredHubOAuth2() {
	}

	/**
	 * Create the interceptor that authorizes requests with the given client registration.
	 * @param registrationId the ID of the client registration to authorize with
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param clientManager the manager that obtains access tokens
	 * @return the interceptor
	 * @throws IllegalStateException if the registration ID is not a registered client
	 */
	static ClientHttpRequestInterceptor interceptor(String registrationId,
			ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientManager clientManager) {
		ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
		if (clientRegistration == null) {
			throw new IllegalStateException("The CredHub OAuth2 client registration ID '" + registrationId
					+ "' is not a valid Spring Security OAuth2 client registration");
		}

		OAuth2ClientHttpRequestInterceptor interceptor = new OAuth2ClientHttpRequestInterceptor(clientManager);
		interceptor.setClientRegistrationIdResolver((request) -> registrationId);
		interceptor.setPrincipalResolver((request) -> new ClientCredentialsAuthenticationToken(clientRegistration));
		return interceptor;
	}

	/**
	 * Create a manager that obtains access tokens with the {@code client_credentials}
	 * grant, calling the token server with the given request factory.
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientRepository a repository of authorized OAuth2 clients
	 * @param requestFactory the request factory for calls to the token server, or
	 * {@code null} to use the default
	 * @return the manager
	 */
	static OAuth2AuthorizedClientManager clientManager(ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientRepository authorizedClientRepository,
			@Nullable ClientHttpRequestFactory requestFactory) {
		DefaultOAuth2AuthorizedClientManager clientManager = new DefaultOAuth2AuthorizedClientManager(
				clientRegistrationRepository, authorizedClientRepository);
		clientManager.setAuthorizedClientProvider(authorizedClientProvider(requestFactory));
		return clientManager;
	}

	/**
	 * Create a manager that obtains access tokens with the {@code client_credentials}
	 * grant and keeps them in an {@link OAuth2AuthorizedClientService}. Unlike the
	 * manager created from an {@link OAuth2AuthorizedClientRepository}, it does not need
	 * a current servlet request, so it works from any thread.
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientService the service that stores authorized clients
	 * @param requestFactory the request factory for calls to the token server, or
	 * {@code null} to use the default
	 * @return the manager
	 */
	static OAuth2AuthorizedClientManager clientManager(ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientService authorizedClientService, @Nullable ClientHttpRequestFactory requestFactory) {
		AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
				clientRegistrationRepository, authorizedClientService);
		clientManager.setAuthorizedClientProvider(authorizedClientProvider(requestFactory));
		return clientManager;
	}

	private static OAuth2AuthorizedClientProvider authorizedClientProvider(
			@Nullable ClientHttpRequestFactory requestFactory) {
		return OAuth2AuthorizedClientProviderBuilder.builder()
			.authorizationCode()
			.clientCredentials((builder) -> builder.accessTokenResponseClient(tokenResponseClient(requestFactory)))
			.build();
	}

	private static OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> tokenResponseClient(
			@Nullable ClientHttpRequestFactory requestFactory) {
		RestClientClientCredentialsTokenResponseClient tokenResponseClient = new RestClientClientCredentialsTokenResponseClient();
		tokenResponseClient.setRestClient(tokenServerRestClient(requestFactory));
		return tokenResponseClient;
	}

	private static RestClient tokenServerRestClient(@Nullable ClientHttpRequestFactory requestFactory) {
		OAuth2ErrorResponseErrorHandler errorHandler = new OAuth2ErrorResponseErrorHandler();
		RestClient.Builder builder = RestClient.builder()
			.configureMessageConverters((converters) -> converters.disableDefaults()
				.withFormConverter(new FormHttpMessageConverter())
				.addCustomConverter(new OAuth2AccessTokenResponseHttpMessageConverter()))
			.defaultStatusHandler(HttpStatusCode::isError,
					(request, response) -> errorHandler.handleError(request.getURI(), request.getMethod(), response));
		if (requestFactory != null) {
			builder.requestFactory(requestFactory);
		}
		return builder.build();
	}

	/**
	 * The principal the client credentials grant authorizes as: the client itself.
	 */
	@SuppressWarnings("serial")
	private static final class ClientCredentialsAuthenticationToken extends AbstractAuthenticationToken {

		private final ClientRegistration clientRegistration;

		ClientCredentialsAuthenticationToken(ClientRegistration clientRegistration) {
			super(Collections.emptyList());
			this.clientRegistration = clientRegistration;
		}

		@Override
		public @Nullable Object getCredentials() {
			return null;
		}

		@Override
		public Object getPrincipal() {
			return this.clientRegistration.getClientId();
		}

	}

}
