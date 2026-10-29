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

package org.springframework.credhub.autoconfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.credhub.core.CredHubClient;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SuppressWarnings("deprecation")
class CredHubClientAutoConfigurationTests {

	private static final String[] OAUTH2_PROPERTIES = { "spring.credhub.url=https://localhost",
			"spring.credhub.oauth2.registration-id=credhub-client",

			"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
			"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
			"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
			"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
			"spring.security.oauth2.client.provider.uaa.token-uri=https://example.com/uaa/oauth/token" };

	private final ApplicationContextRunner context = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(ReactiveOAuth2ClientAutoConfiguration.class,
				CredHubAutoConfiguration.class, CredHubOAuth2AutoConfiguration.class,
				CredHubTemplateAutoConfiguration.class, CredHubClientAutoConfiguration.class));

	@Test
	void credHubClientConfigured() {
		this.context.withPropertyValues("spring.credhub.url=https://localhost").run((context) -> {
			assertThat(context).hasSingleBean(CredHubClient.class);
			assertThat(context).hasSingleBean(CredHubTemplate.class);
			assertThat(interceptors(context.getBean(CredHubClient.class)))
				.noneMatch(OAuth2ClientHttpRequestInterceptor.class::isInstance);
		});
	}

	@Test
	void credHubClientConfiguredWithoutSpringSecurity() {
		this.context.withPropertyValues("spring.credhub.url=https://localhost")
			.withClassLoader(new FilteredClassLoader("org.springframework.security.oauth2.client"))
			.run((context) -> assertThat(context).hasSingleBean(CredHubClient.class));
	}

	@Test
	void credHubClientNotConfiguredWithoutTheServletApi() {
		this.context.withPropertyValues("spring.credhub.url=https://localhost")
			.withClassLoader(new FilteredClassLoader("jakarta.servlet.http.HttpServletRequest"))
			.run((context) -> {
				assertThat(context).doesNotHaveBean(CredHubClient.class);
				assertThat(context).doesNotHaveBean(CredHubTemplate.class);
			});
	}

	@Test
	void credHubClientNotConfiguredWithOAuth2AndACustomClientManagerWithoutTheServletApi() {
		this.context.withPropertyValues(OAUTH2_PROPERTIES)
			.withUserConfiguration(NonServletOAuth2Configuration.class)
			.withClassLoader(new FilteredClassLoader("jakarta.servlet.http.HttpServletRequest"))
			.run((context) -> {
				assertThat(context).hasNotFailed();
				assertThat(context).hasSingleBean(OAuth2AuthorizedClientManager.class);
				assertThat(context).doesNotHaveBean(CredHubClient.class);
			});
	}

	@Test
	void credHubClientNotConfiguredWithoutUrl() {
		this.context.run((context) -> assertThat(context).doesNotHaveBean(CredHubClient.class));
	}

	@Test
	void credHubClientConfiguredWithOAuth2() {
		this.context.withPropertyValues(OAUTH2_PROPERTIES).run((context) -> {
			assertThat(context).hasSingleBean(CredHubClient.class);
			assertThat(context).hasSingleBean(ClientRegistrationRepository.class);
			assertThat(context).doesNotHaveBean(OAuth2AuthorizedClientManager.class);
			assertThat(interceptors(context.getBean(CredHubClient.class)))
				.anyMatch(OAuth2ClientHttpRequestInterceptor.class::isInstance);
		});
	}

	@Test
	void credHubClientConfiguredWithOAuth2AndCustomClientManager() {
		this.context.withPropertyValues(OAUTH2_PROPERTIES)
			.withUserConfiguration(ClientManagerConfiguration.class)
			.run((context) -> {
				assertThat(context).hasSingleBean(CredHubClient.class);
				RestClient.Builder builder = context.getBean(CredHubClient.class).restClient().mutate();
				MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
				server.expect(requestTo("https://localhost/version"))
					.andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer custom-token"))
					.andRespond(withSuccess("{\"version\":\"2.0.0\"}", MediaType.APPLICATION_JSON));

				String body = builder.build().get().uri("/version").retrieve().body(String.class);

				assertThat(body).contains("2.0.0");
				server.verify();
			});
	}

	@Test
	void credHubClientNotConfiguredButApplicationStartsWithOAuth2AndNoClientRegistrationRepository() {
		this.context.withPropertyValues(OAUTH2_PROPERTIES)
			.withClassLoader(new FilteredClassLoader("jakarta.servlet.http.HttpServletRequest"))
			.run((context) -> {
				assertThat(context).hasNotFailed();
				assertThat(context).doesNotHaveBean(ClientRegistrationRepository.class);
				assertThat(context).doesNotHaveBean(CredHubClient.class);
			});
	}

	@Test
	void credHubClientNotConfiguredWhenOAuth2IsConfiguredButSpringSecurityIsMissing() {
		// without the template auto-configuration, which fails to start in this case; the
		// client's own configuration currently backs off without an error
		new ApplicationContextRunner()
			.withConfiguration(
					AutoConfigurations.of(CredHubAutoConfiguration.class, CredHubClientAutoConfiguration.class))
			.withClassLoader(new FilteredClassLoader("org.springframework.security.oauth2.client"))
			.withPropertyValues(OAUTH2_PROPERTIES)
			.run((context) -> {
				assertThat(context).hasNotFailed();
				assertThat(context).doesNotHaveBean(CredHubClient.class);
			});
	}

	@Test
	void credHubClientNotConfiguredWithInvalidClientRegistration() {
		this.context.withPropertyValues(OAUTH2_PROPERTIES)
			.withPropertyValues("spring.credhub.oauth2.registration-id=invalid-client")
			.run((context) -> assertThat(context).getFailure()
				.hasMessageContaining("The CredHub OAuth2 client registration ID 'invalid-client' is not a valid"));
	}

	@Test
	void credHubClientBacksOffWhenDefinedByTheApplication() {
		this.context.withPropertyValues("spring.credhub.url=https://localhost")
			.withUserConfiguration(CustomClientConfiguration.class)
			.run((context) -> {
				assertThat(context).hasSingleBean(CredHubClient.class);
				assertThat(context.getBean(CredHubClient.class)).isSameAs(CustomClientConfiguration.CLIENT);
			});
	}

	private List<ClientHttpRequestInterceptor> interceptors(CredHubClient client) {
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		client.restClient().mutate().requestInterceptors(interceptors::addAll);
		return interceptors;
	}

	@Configuration(proxyBeanMethods = false)
	static class ClientManagerConfiguration {

		@Bean
		OAuth2AuthorizedClientManager clientManager(ClientRegistrationRepository registrations) {
			OAuth2AuthorizedClientManager manager = mock(OAuth2AuthorizedClientManager.class);
			Instant issuedAt = Instant.now();
			given(manager.authorize(any()))
				.willReturn(new OAuth2AuthorizedClient(registrations.findByRegistrationId("credhub-client"),
						"test-client", new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "custom-token",
								issuedAt, issuedAt.plusSeconds(3600))));
			return manager;
		}

	}

	@Configuration(proxyBeanMethods = false)
	static class NonServletOAuth2Configuration {

		@Bean
		ClientRegistrationRepository clientRegistrationRepository() {
			return new InMemoryClientRegistrationRepository(ClientRegistration.withRegistrationId("credhub-client")
				.clientId("test-client")
				.clientSecret("test-secret")
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.tokenUri("https://example.com/uaa/oauth/token")
				.build());
		}

		@Bean
		OAuth2AuthorizedClientManager clientManager() {
			return mock(OAuth2AuthorizedClientManager.class);
		}

	}

	@Configuration(proxyBeanMethods = false)
	static class CustomClientConfiguration {

		static final CredHubClient CLIENT = CredHubClient.builder().baseUrl("https://custom.example.com").build();

		@Bean
		CredHubClient customCredHubClient() {
			return CLIENT;
		}

	}

}
