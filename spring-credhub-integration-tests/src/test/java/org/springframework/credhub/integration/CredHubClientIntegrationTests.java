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

package org.springframework.credhub.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.credhub.configuration.ClientHttpRequestFactoryFactory;
import org.springframework.credhub.core.CredHubClient;
import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.credhub.support.CredentialName;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for integration tests of {@link CredHubClient}, mirroring
 * {@link CredHubIntegrationTests} for the {@code RestTemplate}-based API. The client
 * under test is the one the starter auto-configures, which authenticates with OAuth2
 * against UAA.
 */
@SpringBootTest(classes = { TestApplication.class })
@ActiveProfiles("test")
abstract class CredHubClientIntegrationTests {

	@Autowired
	private CredHubProperties properties;

	@Autowired
	private ClientOptions clientOptions;

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	@Autowired
	private OAuth2AuthorizedClientRepository authorizedClientRepository;

	/**
	 * The client created by the starter's auto-configuration, which authenticates with
	 * OAuth2 using the registration in {@code application-test.yml}.
	 */
	@Autowired
	protected CredHubClient client;

	void deleteCredentialIfExists(CredentialName credentialName) {
		try {
			this.client.credentials().deleteByName(credentialName);
		}
		catch (CredHubException ex) {
			// ignore failing deletes on cleanup
		}
	}

	/**
	 * Create a client whose OAuth2 access tokens come from Spring Security's
	 * servlet-based authorized client manager, which needs a current servlet request.
	 * @return a client authenticated with OAuth2
	 */
	protected CredHubClient repositoryBasedClient() {
		return CredHubClient.builder()
			.baseUrl(this.properties.getUrl())
			.requestFactory(requestFactory())
			.oauth2(this.properties.getOauth2().getRegistrationId(), this.clientRegistrationRepository,
					this.authorizedClientRepository)
			.build();
	}

	/**
	 * Create a client with no authentication, to observe how CredHub rejects requests.
	 * @return an unauthenticated client
	 */
	protected CredHubClient unauthenticatedClient() {
		return CredHubClient.builder().baseUrl(this.properties.getUrl()).requestFactory(requestFactory()).build();
	}

	private ClientHttpRequestFactory requestFactory() {
		return ClientHttpRequestFactoryFactory.create(this.clientOptions);
	}

}
