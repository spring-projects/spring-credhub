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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CredHubWebClientFactoryTests {

	@Mock
	private ClientHttpConnector clientHttpConnector;

	@Mock
	private ReactiveClientRegistrationRepository clientRegistrationRepository;

	@Mock
	private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

	@Test
	public void webClientIsCreated() {
		WebClient webClient = CredHubWebClientFactory.createWebClient(new CredHubProperties(), this.clientHttpConnector,
				this.clientRegistrationRepository, this.authorizedClientRepository);

		assertThat(webClient).isNotNull();
	}

}
