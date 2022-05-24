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

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.client.ClientsConfiguredCondition;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring CredHub OAuth2 support
 * beans.
 *
 * @author Scott Frederick
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OAuth2ClientProperties.class)
@AutoConfigureAfter({ OAuth2ClientAutoConfiguration.class, ReactiveOAuth2ClientAutoConfiguration.class })
@ConditionalOnClass(name = "org.springframework.security.oauth2.client.registration.ClientRegistration")
@ConditionalOnProperty("spring.credhub.oauth2.registration-id")
@Conditional(ClientsConfiguredCondition.class)
public class CredHubOAuth2AutoConfiguration {

	private final OAuth2ClientProperties properties;

	CredHubOAuth2AutoConfiguration(OAuth2ClientProperties properties) {
		this.properties = properties;
	}

	/**
	 * Create a {@code ClientRegistrationRepository} bean for use with an OAuth2-enabled
	 * {@code CredHubTemplate}.
	 * @return the {@code ClientRegistrationRepository}
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name = "javax.servlet.http.HttpServletRequest")
	public ClientRegistrationRepository credHubClientRegistrationRepository() {
		List<ClientRegistration> registrations = new ArrayList<>(
				OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(this.properties).values());
		return new InMemoryClientRegistrationRepository(registrations);
	}

	/**
	 * Create an {@code OAuth2AuthorizedClientRepository} bean for use with an
	 * OAuth2-enabled {@code CredHubTemplate}.
	 * @param clientRegistrationRepository a {@code ClientRegistrationRepository}
	 * @return the {@code OAuth2AuthorizedClientRepository}
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name = "javax.servlet.http.HttpServletRequest")
	public OAuth2AuthorizedClientRepository credHubAuthorizedClientRepository(
			ClientRegistrationRepository clientRegistrationRepository) {
		return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(
				new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository));
	}

	/**
	 * Create a {@code ReactiveClientRegistrationRepository} bean for use with an
	 * OAuth2-enabled {@code ReactiveCredHubTemplate}, in case
	 * {@link ReactiveOAuth2ClientAutoConfiguration} doesn't configure one.
	 * @return the {@code ReactiveClientRegistrationRepository}
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
	public ReactiveClientRegistrationRepository credHubReactiveClientRegistrationRepository() {
		List<ClientRegistration> registrations = new ArrayList<>(
				OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(this.properties).values());
		return new InMemoryReactiveClientRegistrationRepository(registrations);
	}

	/**
	 * Create a {@code ReactiveOAuth2AuthorizedClientManager} bean for use with an
	 * OAuth2-enabled {@code ReactiveCredHubTemplate}, to override the default provided by
	 * {@link ReactiveOAuth2ClientAutoConfiguration}.
	 * @param clientRegistrationRepository a {@code ReactiveClientRegistrationRepository}
	 * @param authorizedClientService a {@code ReactiveOAuth2AuthorizedClientService}
	 * @return the {@code ReactiveOAuth2AuthorizedClientManager}
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
	public ReactiveOAuth2AuthorizedClientManager credHubReactiveAuthorizedClientManager(
			ReactiveClientRegistrationRepository clientRegistrationRepository,
			ReactiveOAuth2AuthorizedClientService authorizedClientService) {
		return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository,
				authorizedClientService);
	}

	/**
	 * Create a {@code ReactiveOAuth2AuthorizedClientService} bean for use with an
	 * OAuth2-enabled {@code ReactiveCredHubTemplate}, to override the default provided by
	 * {@link ReactiveOAuth2ClientAutoConfiguration}.
	 * @param clientRegistrationRepository a {@code ReactiveClientRegistrationRepository}
	 * @return the {@code ReactiveOAuth2AuthorizedClientService}
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
	public ReactiveOAuth2AuthorizedClientService credHubReactiveAuthorizedClientService(
			ReactiveClientRegistrationRepository clientRegistrationRepository) {
		return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
	}

}
