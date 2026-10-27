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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import org.springframework.credhub.core.certificate.CredHubCertificateOperations;
import org.springframework.credhub.core.certificate.DefaultCredHubCertificateOperations;
import org.springframework.credhub.core.credential.CredHubCredentialOperations;
import org.springframework.credhub.core.credential.DefaultCredHubCredentialOperations;
import org.springframework.credhub.core.info.CredHubInfoOperations;
import org.springframework.credhub.core.info.DefaultCredHubInfoOperations;
import org.springframework.credhub.core.interpolation.CredHubInterpolationOperations;
import org.springframework.credhub.core.interpolation.DefaultCredHubInterpolationOperations;
import org.springframework.credhub.core.permission.CredHubPermissionOperations;
import org.springframework.credhub.core.permission.DefaultCredHubPermissionOperations;
import org.springframework.credhub.core.permissionV2.CredHubPermissionV2Operations;
import org.springframework.credhub.core.permissionV2.DefaultCredHubPermissionV2Operations;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

/**
 * Default {@link CredHubClient} implementation.
 *
 * @author Roy Clarkson
 */
final class DefaultCredHubClient implements CredHubClient {

	private final RestClient restClient;

	private final CredHubCredentialOperations credentials;

	private final CredHubPermissionOperations permissions;

	private final CredHubPermissionV2Operations permissionsV2;

	private final CredHubCertificateOperations certificates;

	private final CredHubInterpolationOperations interpolation;

	private final CredHubInfoOperations info;

	DefaultCredHubClient(RestClient restClient) {
		Assert.notNull(restClient, "restClient must not be null");
		this.restClient = restClient;
		this.credentials = new DefaultCredHubCredentialOperations(restClient);
		this.permissions = new DefaultCredHubPermissionOperations(restClient);
		this.permissionsV2 = new DefaultCredHubPermissionV2Operations(restClient);
		this.certificates = new DefaultCredHubCertificateOperations(restClient);
		this.interpolation = new DefaultCredHubInterpolationOperations(restClient);
		this.info = new DefaultCredHubInfoOperations(restClient);
	}

	@Override
	public CredHubCredentialOperations credentials() {
		return this.credentials;
	}

	@Override
	public CredHubPermissionOperations permissions() {
		return this.permissions;
	}

	@Override
	public CredHubPermissionV2Operations permissionsV2() {
		return this.permissionsV2;
	}

	@Override
	public CredHubCertificateOperations certificates() {
		return this.certificates;
	}

	@Override
	public CredHubInterpolationOperations interpolation() {
		return this.interpolation;
	}

	@Override
	public CredHubInfoOperations info() {
		return this.info;
	}

	@Override
	public RestClient restClient() {
		return this.restClient;
	}

	static final class DefaultBuilder implements Builder {

		private @Nullable String baseUrl;

		private @Nullable ClientHttpRequestFactory requestFactory;

		private @Nullable String registrationId;

		private @Nullable ClientRegistrationRepository clientRegistrationRepository;

		private @Nullable OAuth2AuthorizedClientRepository authorizedClientRepository;

		private @Nullable OAuth2AuthorizedClientService authorizedClientService;

		private @Nullable OAuth2AuthorizedClientManager clientManager;

		private final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

		private final List<Consumer<RestClient.Builder>> customizers = new ArrayList<>();

		@Override
		public Builder baseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		@Override
		public Builder requestFactory(ClientHttpRequestFactory requestFactory) {
			Assert.notNull(requestFactory, "requestFactory must not be null");
			this.requestFactory = requestFactory;
			return this;
		}

		@Override
		public Builder oauth2(String registrationId, ClientRegistrationRepository clientRegistrationRepository,
				OAuth2AuthorizedClientRepository authorizedClientRepository) {
			Assert.hasText(registrationId, "registrationId must not be empty");
			Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository must not be null");
			Assert.notNull(authorizedClientRepository, "authorizedClientRepository must not be null");
			this.registrationId = registrationId;
			this.clientRegistrationRepository = clientRegistrationRepository;
			this.authorizedClientRepository = authorizedClientRepository;
			this.authorizedClientService = null;
			this.clientManager = null;
			return this;
		}

		@Override
		public Builder oauth2(String registrationId, ClientRegistrationRepository clientRegistrationRepository,
				OAuth2AuthorizedClientManager clientManager) {
			Assert.hasText(registrationId, "registrationId must not be empty");
			Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository must not be null");
			Assert.notNull(clientManager, "clientManager must not be null");
			this.registrationId = registrationId;
			this.clientRegistrationRepository = clientRegistrationRepository;
			this.clientManager = clientManager;
			this.authorizedClientRepository = null;
			this.authorizedClientService = null;
			return this;
		}

		@Override
		public Builder oauth2(String registrationId, ClientRegistrationRepository clientRegistrationRepository,
				OAuth2AuthorizedClientService authorizedClientService) {
			Assert.hasText(registrationId, "registrationId must not be empty");
			Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository must not be null");
			Assert.notNull(authorizedClientService, "authorizedClientService must not be null");
			this.registrationId = registrationId;
			this.clientRegistrationRepository = clientRegistrationRepository;
			this.authorizedClientService = authorizedClientService;
			this.authorizedClientRepository = null;
			this.clientManager = null;
			return this;
		}

		@Override
		public Builder requestInterceptor(ClientHttpRequestInterceptor interceptor) {
			Assert.notNull(interceptor, "interceptor must not be null");
			this.interceptors.add(interceptor);
			return this;
		}

		@Override
		public Builder restClientCustomizer(Consumer<RestClient.Builder> customizer) {
			Assert.notNull(customizer, "customizer must not be null");
			this.customizers.add(customizer);
			return this;
		}

		private OAuth2AuthorizedClientManager oauth2ClientManager(
				ClientRegistrationRepository clientRegistrationRepository) {
			if (this.clientManager != null) {
				return this.clientManager;
			}
			if (this.authorizedClientService != null) {
				return CredHubOAuth2.clientManager(clientRegistrationRepository, this.authorizedClientService,
						this.requestFactory);
			}
			Assert.state(this.authorizedClientRepository != null, "an OAuth2 authorized client store is required");
			return CredHubOAuth2.clientManager(clientRegistrationRepository, this.authorizedClientRepository,
					this.requestFactory);
		}

		@Override
		public CredHubClient build() {
			Assert.hasText(this.baseUrl, "baseUrl must not be empty");

			RestClient.Builder builder = CredHubRestClientFactory.builder(this.baseUrl);
			if (this.requestFactory != null) {
				builder.requestFactory(this.requestFactory);
			}
			if (this.registrationId != null && this.clientRegistrationRepository != null) {
				OAuth2AuthorizedClientManager manager = oauth2ClientManager(this.clientRegistrationRepository);
				builder.requestInterceptor(
						CredHubOAuth2.interceptor(this.registrationId, this.clientRegistrationRepository, manager));
			}
			this.interceptors.forEach(builder::requestInterceptor);
			this.customizers.forEach((customizer) -> customizer.accept(builder));
			return new DefaultCredHubClient(builder.build());
		}

	}

}
