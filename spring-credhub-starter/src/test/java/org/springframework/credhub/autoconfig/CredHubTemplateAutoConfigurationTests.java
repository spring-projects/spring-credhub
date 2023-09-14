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

package org.springframework.credhub.autoconfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.ReactiveCredHubTemplate;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

public class CredHubTemplateAutoConfigurationTests {

	private static final FilteredClassLoader SPRING_SECURITY_FILTERED_CLASS_LOADER = new FilteredClassLoader(
			"org.springframework.security.oauth2.client");

	private static final FilteredClassLoader SERVLET_AND_SECURITY_FILTERED_CLASS_LOADER = new FilteredClassLoader(
			"javax.servlet.http.HttpServletRequest", "org.springframework.security.oauth2.client");

	private static final FilteredClassLoader WEB_CLIENT_AND_SECURITY_FILTERED_CLASS_LOADER = new FilteredClassLoader(
			"org.springframework.web.reactive.function.client", "org.springframework.security.oauth2.client");

	private final ApplicationContextRunner context = new ApplicationContextRunner()
		.withConfiguration(
				AutoConfigurations.of(ReactiveOAuth2ClientAutoConfiguration.class, CredHubAutoConfiguration.class,
						CredHubOAuth2AutoConfiguration.class, CredHubTemplateAutoConfiguration.class))
		.withInitializer(new ConditionEvaluationReportLoggingListener(LogLevel.INFO));

	@Test
	public void credHubTemplatesConfigured() {
		this.context.withPropertyValues("spring.credhub.url=https://localhost")
			.withClassLoader(SPRING_SECURITY_FILTERED_CLASS_LOADER)
			.run((context) -> {
				assertThat(context).hasSingleBean(CredHubTemplate.class);
				CredHubTemplate credHubTemplate = context.getBean(CredHubTemplate.class);
				assertThat(credHubTemplate.isUsingOAuth2()).isFalse();

				assertThat(context).hasSingleBean(ReactiveCredHubTemplate.class);
				ReactiveCredHubTemplate reactiveCredHubTemplate = context.getBean(ReactiveCredHubTemplate.class);
				assertThat(reactiveCredHubTemplate.isUsingOAuth2()).isFalse();
			});
	}

	@Test
	public void credHubTemplatesConfiguredWithSpringSecurityWithoutClientId() {
		this.context.withPropertyValues("spring.credhub.url=https://localhost").run((context) -> {
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
		this.context.withPropertyValues("spring.credhub.url=https://localhost")
			.withClassLoader(new FilteredClassLoader(WebClient.class))
			.run((context) -> {
				assertThat(context).hasSingleBean(CredHubTemplate.class);
				CredHubTemplate credHubTemplate = context.getBean(CredHubTemplate.class);
				assertThat(credHubTemplate.isUsingOAuth2()).isFalse();

				assertThat(context).doesNotHaveBean(ReactiveCredHubTemplate.class);
			});
	}

	@Test
	public void credHubTemplatesConfiguredWithOAuth2() {
		this.context.withPropertyValues("spring.credhub.url=https://localhost",
				"spring.credhub.oauth2.registration-id=credhub-client",

				"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
				"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
				"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
				"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
				"spring.security.oauth2.client.provider.uaa.token-uri=https://example.com/uaa/oauth/token")
			.run((context) -> {
				assertThat(context).hasSingleBean(CredHubTemplate.class);
				assertThat(context).hasSingleBean(ClientRegistrationRepository.class);
				assertThat(context).hasSingleBean(OAuth2AuthorizedClientRepository.class);
				assertThat(context).doesNotHaveBean(OAuth2AuthorizedClientManager.class);
				CredHubTemplate credHubTemplate = context.getBean(CredHubTemplate.class);
				assertThat(credHubTemplate.isUsingOAuth2()).isTrue();

				assertThat(context).hasSingleBean(ReactiveCredHubTemplate.class);
				assertThat(context).hasSingleBean(ReactiveClientRegistrationRepository.class);
				assertThat(context).hasSingleBean(ServerOAuth2AuthorizedClientRepository.class);
				assertThat(context).doesNotHaveBean(ReactiveOAuth2AuthorizedClientManager.class);
				ReactiveCredHubTemplate reactiveCredHubTemplate = context.getBean(ReactiveCredHubTemplate.class);
				assertThat(reactiveCredHubTemplate.isUsingOAuth2()).isTrue();
			});
	}

	@Test
	public void credHubTemplatesConfiguredWithOAuth2AndCustomClientManager() {
		this.context.withPropertyValues("spring.credhub.url=https://localhost",
				"spring.credhub.oauth2.registration-id=credhub-client",

				"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
				"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
				"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
				"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
				"spring.security.oauth2.client.provider.uaa.token-uri=https://example.com/uaa/oauth/token")
			.withUserConfiguration(ClientManagerConfiguration.class)
			.run((context) -> {
				assertThat(context).hasSingleBean(CredHubTemplate.class);
				assertThat(context).hasSingleBean(ClientRegistrationRepository.class);
				assertThat(context).hasSingleBean(OAuth2AuthorizedClientRepository.class);
				assertThat(context).hasSingleBean(AuthorizedClientServiceOAuth2AuthorizedClientManager.class);
				CredHubTemplate credHubTemplate = context.getBean(CredHubTemplate.class);
				assertThat(credHubTemplate.isUsingOAuth2()).isTrue();

				assertThat(context).hasSingleBean(ReactiveCredHubTemplate.class);
				assertThat(context).hasSingleBean(ReactiveClientRegistrationRepository.class);
				assertThat(context).hasSingleBean(ServerOAuth2AuthorizedClientRepository.class);
				assertThat(context).hasSingleBean(AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager.class);
				ReactiveCredHubTemplate reactiveCredHubTemplate = context.getBean(ReactiveCredHubTemplate.class);
				assertThat(reactiveCredHubTemplate.isUsingOAuth2()).isTrue();
			});
	}

	@Test
	public void credHubTemplatesNotConfiguredWithInvalidClientRegistration() {
		this.context.withPropertyValues("spring.credhub.url=https://localhost",
				"spring.credhub.oauth2.registration-id=invalid-client",

				"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
				"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
				"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
				"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
				"spring.security.oauth2.client.provider.uaa.token-uri=https://example.com/uaa/oauth/token")
			.run((context) -> assertThat(context).getFailure()
				.hasMessageContaining("The CredHub OAuth2 client registration ID 'invalid-client' is not a valid"));
	}

	@Test
	public void credHubTemplateNotConfiguredWithMissingClientRegistration() {
		this.context.withClassLoader(WEB_CLIENT_AND_SECURITY_FILTERED_CLASS_LOADER)
			.withPropertyValues("spring.credhub.url=https://localhost",
					"spring.credhub.oauth2.registration-id=credhub-client")
			.run((context) -> assertThat(context).getFailure()
				.hasMessageMatching(noSuchBeanExceptionMessage("ClientRegistrationRepository")));
	}

	@Test
	public void reactiveCredHubTemplateNotConfiguredWithMissingClientRegistration() {
		this.context.withClassLoader(SERVLET_AND_SECURITY_FILTERED_CLASS_LOADER)
			.withPropertyValues("spring.credhub.url=https://localhost",
					"spring.credhub.oauth2.registration-id=credhub-client")
			.run((context) -> assertThat(context).getFailure()
				.hasMessageMatching(noSuchBeanExceptionMessage("ReactiveClientRegistrationRepository")));
	}

	@Test
	public void credHubTemplateNotConfiguredWithOAuth2ButMissingSpringSecurity() {
		this.context.withClassLoader(WEB_CLIENT_AND_SECURITY_FILTERED_CLASS_LOADER)
			.withPropertyValues("spring.credhub.url=https://localhost",
					"spring.credhub.oauth2.registration-id=credhub-client",

					"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
					"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
					"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
					"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
					"spring.security.oauth2.client.provider.uaa.token-uri=https://example.com/uaa/oauth/token")
			.run((context) -> assertThat(context).hasFailed());
	}

	@Test
	public void reactiveCredHubTemplateNotConfiguredWithOAuth2ButMissingSpringSecurity() {
		this.context.withClassLoader(SERVLET_AND_SECURITY_FILTERED_CLASS_LOADER)
			.withPropertyValues("spring.credhub.url=https://localhost",
					"spring.credhub.oauth2.registration-id=credhub-client",

					"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
					"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
					"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
					"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
					"spring.security.oauth2.client.provider.uaa.token-uri=https://example.com/uaa/oauth/token")
			.run((context) -> assertThat(context).hasFailed());
	}

	private String noSuchBeanExceptionMessage(final String className) {
		return ".*No qualifying bean of type '.*" + className + "' available.*";
	}

	@Configuration(proxyBeanMethods = false)
	static class ClientManagerConfiguration {

		@Bean
		AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager(OAuth2ClientProperties properties) {
			List<ClientRegistration> registrations = new ArrayList<>(
					OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties).values());
			ClientRegistrationRepository clientRegistrationRepository = new InMemoryClientRegistrationRepository(
					registrations);
			OAuth2AuthorizedClientService authorizedClientService = new InMemoryOAuth2AuthorizedClientService(
					clientRegistrationRepository);
			return new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
					authorizedClientService);
		}

		@Bean
		AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager reactiveClientManager(
				OAuth2ClientProperties properties) {
			List<ClientRegistration> registrations = new ArrayList<>(
					OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties).values());
			ReactiveClientRegistrationRepository clientRegistrationRepository = new InMemoryReactiveClientRegistrationRepository(
					registrations);
			ReactiveOAuth2AuthorizedClientService authorizedClientService = new InMemoryReactiveOAuth2AuthorizedClientService(
					clientRegistrationRepository);
			return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository,
					authorizedClientService);
		}

	}

}
