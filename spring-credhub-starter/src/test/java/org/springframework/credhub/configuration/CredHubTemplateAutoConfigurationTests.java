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
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubOAuth2TemplateAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubTemplateAutoConfiguration;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.ReactiveCredHubTemplate;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

public class CredHubTemplateAutoConfigurationTests {

	private final ApplicationContextRunner context = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(CredHubAutoConfiguration.class,
					CredHubOAuth2TemplateAutoConfiguration.class,
					CredHubTemplateAutoConfiguration.class))
			.withPropertyValues(
					"spring.credhub.url=https://localhost",
					"debug");

	@Test
	public void credHubTemplateConfigured() {
		context.run((context) -> {
					assertThat(context).hasSingleBean(CredHubTemplate.class);
				});
	}

	@Test
	public void reactiveCredHubTemplateConfigured() {
		context.run((context) -> {
			assertThat(context).hasSingleBean(ReactiveCredHubTemplate.class);
			assertThat(context).hasSingleBean(ClientHttpConnector.class);
			assertThat(context).doesNotHaveBean(ReactiveClientRegistrationRepository.class);
			assertThat(context).doesNotHaveBean(ServerOAuth2AuthorizedClientRepository.class);
		});
	}

	@Test
	public void reactiveCredHubTemplateNotConfiguredWithoutWebClient() {
		context.withClassLoader(new FilteredClassLoader(WebClient.class))
				.run((context) -> {
					assertThat(context).doesNotHaveBean(ReactiveCredHubTemplate.class);
					assertThat(context).doesNotHaveBean(ClientHttpConnector.class);
				});
	}
}
