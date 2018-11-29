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

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration.ClientFactoryWrapper;
import org.springframework.credhub.configuration.CredHubTemplateFactory;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.ReactiveCredHubOperations;
import org.springframework.credhub.core.ReactiveCredHubTemplate;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link CredHubTemplate} with
 * OAuth2 credentials.
 *
 * @author Daniel Lavoie
 * @author Scott Frederick
 */
@Configuration
@AutoConfigureAfter({CredHubAutoConfiguration.class,
		CredHubOAuth2AutoConfiguration.class,
		OAuth2ClientAutoConfiguration.class,
		ReactiveOAuth2ClientAutoConfiguration.class})
@ConditionalOnProperty("spring.credhub.oauth2.registration-id")
public class CredHubOAuth2TemplateAutoConfiguration {
	private final CredHubTemplateFactory credHubTemplateFactory = new CredHubTemplateFactory();

	/**
	 * Create the {@link CredHubTemplate} that the application will use to interact
	 * with CredHub.
	 *
	 * @param credHubProperties            {@link CredHubProperties} for CredHub
	 * @param clientFactoryWrapper         a {@link ClientFactoryWrapper}
	 *                                     to customize CredHub http requests
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientService      a repository of authorized OAuth2 clients
	 * @return the {@link CredHubOperations} bean
	 */
	@Bean
	public CredHubOperations oAuth2credHubTemplate(CredHubProperties credHubProperties,
												   ClientFactoryWrapper clientFactoryWrapper,
												   ClientRegistrationRepository clientRegistrationRepository,
												   OAuth2AuthorizedClientService authorizedClientService) {
		return credHubTemplateFactory.credHubTemplate(credHubProperties,
				clientFactoryWrapper.getClientHttpRequestFactory(),
				clientRegistrationRepository,
				authorizedClientService);
	}

	/**
	 * Create the {@link ReactiveCredHubTemplate} that the application will use to interact
	 * with CredHub.
	 *
	 * @param credHubProperties            {@link CredHubProperties} for CredHub
	 * @param clientHttpConnector          a {@link ClientHttpConnector} to customize CredHub
	 *                                     http requests
	 * @param clientRegistrationRepository a repository of OAuth2 client registrations
	 * @param authorizedClientRepository   a repository of OAuth2 authorized clients
	 * @return the {@link CredHubTemplate} bean
	 */
	@Bean
	@ConditionalOnClass(WebClient.class)
	public ReactiveCredHubOperations oAuth2reactiveCredHubTemplate(CredHubProperties credHubProperties,
																   ClientHttpConnector clientHttpConnector,
																   ReactiveClientRegistrationRepository clientRegistrationRepository,
																   ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
		return credHubTemplateFactory.credHubTemplate(credHubProperties, clientHttpConnector,
				clientRegistrationRepository, authorizedClientRepository);
	}
}
