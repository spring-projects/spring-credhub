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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.credhub.autoconfig.CredHubClientAutoConfiguration.CredHubClientBaseConfiguration;
import org.springframework.credhub.autoconfig.CredHubClientAutoConfiguration.CredHubClientOAuth2ClientManagerConfiguration;
import org.springframework.credhub.autoconfig.CredHubClientAutoConfiguration.CredHubClientOAuth2Configuration;
import org.springframework.credhub.configuration.ClientHttpRequestFactoryFactory;
import org.springframework.credhub.core.CredHubClient;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 * Auto-configuration for the {@link CredHubClient}, the {@code RestClient}-based
 * counterpart to the auto-configured {@code CredHubTemplate}.
 * <p>
 * Like the template auto-configuration, it applies only when the servlet API is present,
 * so reactive-only applications do not get a blocking client. The client itself does not
 * depend on the servlet API and can be built directly with
 * {@link CredHubClient#builder()} anywhere.
 * <p>
 * The client authenticates with OAuth2 when {@code spring.credhub.oauth2.registration-id}
 * is set. It then uses an {@link OAuth2AuthorizedClientManager} bean if the application
 * defines one, and otherwise a manager that keeps access tokens in an in-memory
 * {@code OAuth2AuthorizedClientService}, which does not need a current request, so the
 * client can be used from any thread.
 * <p>
 * When OAuth2 is configured but the application has no
 * {@code ClientRegistrationRepository} bean, no {@code CredHubClient} is created and the
 * application still starts.
 *
 * @author Roy Clarkson
 * @since 4.2.0
 */
@AutoConfiguration(after = { CredHubAutoConfiguration.class, CredHubOAuth2AutoConfiguration.class,
		OAuth2ClientAutoConfiguration.class, ReactiveOAuth2ClientAutoConfiguration.class })
@ConditionalOnBean(CredHubProperties.class)
@ConditionalOnClass(CredHubClient.class)
@Import({ CredHubClientBaseConfiguration.class, CredHubClientOAuth2Configuration.class,
		CredHubClientOAuth2ClientManagerConfiguration.class })
public class CredHubClientAutoConfiguration {

	/**
	 * Create the auto-configuration.
	 */
	public CredHubClientAutoConfiguration() {
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = "jakarta.servlet.http.HttpServletRequest")
	@ConditionalOnProperty(prefix = "spring.credhub.oauth2", name = "registration-id", havingValue = "false",
			matchIfMissing = true)
	static class CredHubClientBaseConfiguration {

		@Bean
		@ConditionalOnMissingBean
		CredHubClient credHubClient(CredHubProperties credHubProperties, ClientOptions clientOptions) {
			return CredHubClient.builder()
				.baseUrl(credHubProperties.getUrl())
				.requestFactory(ClientHttpRequestFactoryFactory.create(clientOptions))
				.build();
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = { "jakarta.servlet.http.HttpServletRequest",
			"org.springframework.security.oauth2.client.registration.ClientRegistration" })
	@ConditionalOnProperty(prefix = "spring.credhub.oauth2", name = "registration-id")
	@ConditionalOnBean(ClientRegistrationRepository.class)
	@ConditionalOnMissingBean(OAuth2AuthorizedClientManager.class)
	static class CredHubClientOAuth2Configuration {

		@Bean
		@ConditionalOnMissingBean
		CredHubClient credHubClient(CredHubProperties credHubProperties, ClientOptions clientOptions,
				ClientRegistrationRepository clientRegistrationRepository) {
			return CredHubClient.builder()
				.baseUrl(credHubProperties.getUrl())
				.requestFactory(ClientHttpRequestFactoryFactory.create(clientOptions))
				.oauth2(credHubProperties.getOauth2().getRegistrationId(), clientRegistrationRepository,
						new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository))
				.build();
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = { "jakarta.servlet.http.HttpServletRequest",
			"org.springframework.security.oauth2.client.registration.ClientRegistration" })
	@ConditionalOnProperty(prefix = "spring.credhub.oauth2", name = "registration-id")
	@ConditionalOnBean({ ClientRegistrationRepository.class, OAuth2AuthorizedClientManager.class })
	static class CredHubClientOAuth2ClientManagerConfiguration {

		@Bean
		@ConditionalOnMissingBean
		CredHubClient credHubClient(CredHubProperties credHubProperties, ClientOptions clientOptions,
				ClientRegistrationRepository clientRegistrationRepository,
				OAuth2AuthorizedClientManager clientManager) {
			return CredHubClient.builder()
				.baseUrl(credHubProperties.getUrl())
				.requestFactory(ClientHttpRequestFactoryFactory.create(clientOptions))
				.oauth2(credHubProperties.getOauth2().getRegistrationId(), clientRegistrationRepository, clientManager)
				.build();
		}

	}

}
