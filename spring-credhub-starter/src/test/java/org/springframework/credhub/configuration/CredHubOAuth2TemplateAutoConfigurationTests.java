/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.configuration;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubOAuth2AutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubOAuth2TemplateAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubTemplateAutoConfiguration;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.ReactiveCredHubTemplate;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Daniel Lavoie
 */
public class CredHubOAuth2TemplateAutoConfigurationTests {

	private ApplicationContextRunner context = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					ReactiveOAuth2ClientAutoConfiguration.class,
					CredHubAutoConfiguration.class,
					CredHubOAuth2AutoConfiguration.class,
					CredHubOAuth2TemplateAutoConfiguration.class,
					CredHubTemplateAutoConfiguration.class))
			.withPropertyValues(
					"spring.credhub.url=https://localhost",
					"spring.credhub.oauth2.registration-id=credhub-client",
					
					"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
					"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
					"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
					"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
					"spring.security.oauth2.client.provider.uaa.token-uri=http://example.com/uaa/oauth/token",
					"debug=true"
			);

	@Test
	public void credHubTemplateConfiguredWithOAuth2() {
		context.run((context) -> {
			assertThat(context).hasSingleBean(CredHubTemplate.class);
			assertThat(context).hasSingleBean(CredHubAutoConfiguration.ClientFactoryWrapper.class);

			assertThat(context).hasSingleBean(ClientRegistrationRepository.class);
		});
	}

	@Test
	public void reactiveCredHubTemplateConfiguredWithOAuth2() {
		context.run((context) -> {
			assertThat(context).hasSingleBean(ReactiveCredHubTemplate.class);
			assertThat(context).hasSingleBean(ClientHttpConnector.class);

			assertThat(context).hasSingleBean(ReactiveClientRegistrationRepository.class);
			assertThat(context).hasSingleBean(ServerOAuth2AuthorizedClientRepository.class);
		});
	}
}
