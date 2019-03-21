/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.autoconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.credhub.configuration.CredHubTemplateFactory;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.ReactiveCredHubOperations;
import org.springframework.credhub.core.ReactiveCredHubTemplate;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link CredHubTemplate}.
 *
 * @author Daniel Lavoie
 * @author Scott Frederick
 */
@Configuration
@AutoConfigureAfter({CredHubAutoConfiguration.class,
		CredHubOAuth2AutoConfiguration.class,
		OAuth2ClientAutoConfiguration.class,
		ReactiveOAuth2ClientAutoConfiguration.class})
@ConditionalOnBean(CredHubProperties.class)
public class CredHubTemplateAutoConfiguration {
	private final CredHubTemplateFactory credHubTemplateFactory = new CredHubTemplateFactory();

	/**
	 * Create the {@link CredHubTemplate} that the application will use to interact
	 * with CredHub.
	 *
	 * @param credHubProperties            {@link CredHubProperties} for CredHub
	 * @param clientOptions                client connection options
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientService      a repository of authorized OAuth2 clients
	 * @return the {@link CredHubOperations} bean
	 */
	@Bean
	@ConditionalOnMissingBean
	public CredHubOperations credHubTemplate(
			CredHubProperties credHubProperties, ClientOptions clientOptions,
			@Autowired(required = false) ClientRegistrationRepository clientRegistrationRepository,
			@Autowired(required = false) OAuth2AuthorizedClientService authorizedClientService) {

		if (credHubProperties.getOauth2() == null || credHubProperties.getOauth2().getRegistrationId() == null) {
			return credHubTemplateFactory.credHubTemplate(credHubProperties, clientOptions);
		}

		if (clientRegistrationRepository == null || authorizedClientService == null) {
			throw misconfiguredException();
		}

		return credHubTemplateFactory.credHubTemplate(credHubProperties, clientOptions,
				clientRegistrationRepository, authorizedClientService);
	}

	/**
	 * Create the {@link ReactiveCredHubTemplate} that the application will use to interact
	 * with CredHub.
	 *
	 * @param credHubProperties            {@link CredHubProperties} for CredHub
	 * @param clientOptions                client connection options
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientRepository   a repository of OAuth2 authorized clients
	 * @return the {@link CredHubTemplate} bean
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
	public ReactiveCredHubOperations reactiveCredHubTemplate(
			CredHubProperties credHubProperties, ClientOptions clientOptions,
			@Autowired(required = false) ReactiveClientRegistrationRepository clientRegistrationRepository,
			@Autowired(required = false) ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {

		if (credHubProperties.getOauth2() == null || credHubProperties.getOauth2().getRegistrationId() == null) {
			return credHubTemplateFactory.reactiveCredHubTemplate(credHubProperties, clientOptions);
		}

		if (clientRegistrationRepository == null || authorizedClientRepository == null) {
			throw misconfiguredException();
		}
		return credHubTemplateFactory.reactiveCredHubTemplate(credHubProperties, clientOptions,
				clientRegistrationRepository, authorizedClientRepository);
	}

	private IllegalArgumentException misconfiguredException() {
		return new IllegalArgumentException("A CredHub OAuth2 client registration is configured " +
				"but Spring Security is not available or the Spring Security OAuth2 " +
				"client registration is misconfigured");
	}

}
