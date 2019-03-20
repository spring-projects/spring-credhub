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

package org.springframework.credhub.autoconfig;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.ReactiveCredHubTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

public class CredHubTemplateAutoConfigurationTests {

	private static final FilteredClassLoader SPRING_SECURITY_FILTERED_CLASS_LOADER =
			new FilteredClassLoader("org.springframework.security.oauth2.client");

	private final ApplicationContextRunner context = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					ReactiveOAuth2ClientAutoConfiguration.class,
					CredHubAutoConfiguration.class,
					CredHubOAuth2AutoConfiguration.class,
					CredHubTemplateAutoConfiguration.class
			))
			.withPropertyValues(
					"debug=true"
			);

	@Test
	public void credHubTemplatesConfigured() {
		context
				.withPropertyValues(
						"spring.credhub.url=https://localhost"
				)
				.withClassLoader(SPRING_SECURITY_FILTERED_CLASS_LOADER)
				.run(context -> {
					assertThat(context).hasSingleBean(CredHubTemplate.class);
					CredHubTemplate credHubTemplate = context.getBean(CredHubTemplate.class);
					assertThat(credHubTemplate.isUsingOAuth2()).isFalse();

					assertThat(context).hasSingleBean(ReactiveCredHubTemplate.class);
					ReactiveCredHubTemplate reactiveCredHubTemplate = context.getBean(ReactiveCredHubTemplate.class);
					assertThat(reactiveCredHubTemplate.isUsingOAuth2()).isFalse();
				});
	}

	@Test
	public void reactiveCredHubTemplateNotConfiguredWithoutWebClient() {
		context
				.withPropertyValues(
						"spring.credhub.url=https://localhost"
				)
				.withClassLoader(new FilteredClassLoader(WebClient.class))
				.run(context -> {
					assertThat(context).hasSingleBean(CredHubTemplate.class);
					CredHubTemplate credHubTemplate = context.getBean(CredHubTemplate.class);
					assertThat(credHubTemplate.isUsingOAuth2()).isFalse();

					assertThat(context).doesNotHaveBean(ReactiveCredHubTemplate.class);
				});
	}

	@Test
	public void credHubTemplatesConfiguredWithOAuth2() {
		context
				.withPropertyValues(
						"spring.credhub.url=https://localhost",
						"spring.credhub.oauth2.registration-id=credhub-client",

						"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
						"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
						"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
						"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
						"spring.security.oauth2.client.provider.uaa.token-uri=https://example.com/uaa/oauth/token"
				)
				.run(context -> {
					assertThat(context).hasSingleBean(CredHubTemplate.class);
					CredHubTemplate credHubTemplate = context.getBean(CredHubTemplate.class);
					assertThat(credHubTemplate.isUsingOAuth2()).isTrue();

					assertThat(context).hasSingleBean(ReactiveCredHubTemplate.class);
					ReactiveCredHubTemplate reactiveCredHubTemplate = context.getBean(ReactiveCredHubTemplate.class);
					assertThat(reactiveCredHubTemplate.isUsingOAuth2()).isTrue();
				});
	}

	@Test
	public void credHubTemplatesNotConfiguredWithInvalidClientRegistration() {
		context
				.withPropertyValues(
						"spring.credhub.url=https://localhost",
						"spring.credhub.oauth2.registration-id=invalid-credhub-client",

						"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
						"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
						"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
						"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
						"spring.security.oauth2.client.provider.uaa.token-uri=https://example.com/uaa/oauth/token"
				)
				.run(context -> assertThat(context)
						.getFailure()
						.hasMessageContaining("The CredHub OAuth2 client registration ID 'invalid-credhub-client' is not a valid"));
	}

	@Test
	public void credHubTemplatesNotConfiguredWithMissingClientRegistration() {
		context
				.withPropertyValues(
						"spring.credhub.url=https://localhost",
						"spring.credhub.oauth2.registration-id=credhub-client"
				)
				.run(context -> assertThat(context)
						.getFailure()
						.hasMessageContaining("A CredHub OAuth2 client registration is configured but"));
	}

	@Test
	public void credHubTemplatesNotConfiguredWithOAuth2ButMissingSpringSecurity() {
		context
				.withPropertyValues(
						"spring.credhub.url=https://localhost",
						"spring.credhub.oauth2.registration-id=credhub-client",

						"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
						"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
						"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
						"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
						"spring.security.oauth2.client.provider.uaa.token-uri=https://example.com/uaa/oauth/token"
				)
				.withClassLoader(SPRING_SECURITY_FILTERED_CLASS_LOADER)
				.run(context -> assertThat(context).hasFailed());
	}
}
