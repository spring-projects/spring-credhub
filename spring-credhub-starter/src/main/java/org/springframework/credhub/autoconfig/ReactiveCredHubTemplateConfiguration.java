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

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.credhub.autoconfig.ReactiveCredHubTemplateConfiguration.ReactiveCredHubTemplateBaseConfiguration;
import org.springframework.credhub.autoconfig.ReactiveCredHubTemplateConfiguration.ReactiveCredHubTemplateOAuth2ClientConfiguration;
import org.springframework.credhub.autoconfig.ReactiveCredHubTemplateConfiguration.ReactiveCredHubTemplateOAuth2Configuration;
import org.springframework.credhub.configuration.CredHubTemplateFactory;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.ReactiveCredHubOperations;
import org.springframework.credhub.core.ReactiveCredHubTemplate;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;

/**
 * Configuration for {@link ReactiveCredHubTemplate}.
 *
 * @author Scott Frederick
 */
@Import({ ReactiveCredHubTemplateBaseConfiguration.class, ReactiveCredHubTemplateOAuth2Configuration.class,
		ReactiveCredHubTemplateOAuth2ClientConfiguration.class })
public class ReactiveCredHubTemplateConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
	@ConditionalOnProperty(prefix = "spring.credhub.oauth2", name = "registration-id", havingValue = "false",
			matchIfMissing = true)
	static class ReactiveCredHubTemplateBaseConfiguration {

		/**
		 * Create the {@link ReactiveCredHubTemplate} that the application will use to
		 * interact with CredHub.
		 * @param credHubProperties {@link CredHubProperties} for CredHub
		 * @param clientOptions client connection options
		 * @return the {@link CredHubTemplate} bean
		 */
		@Bean
		@ConditionalOnMissingBean
		ReactiveCredHubOperations reactiveCredHubTemplate(CredHubProperties credHubProperties,
				ClientOptions clientOptions) {

			return new CredHubTemplateFactory().reactiveCredHubTemplate(credHubProperties, clientOptions);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = { "org.springframework.web.reactive.function.client.WebClient" })
	@ConditionalOnProperty(prefix = "spring.credhub.oauth2", name = "registration-id")
	@ConditionalOnMissingBean(ReactiveOAuth2AuthorizedClientManager.class)
	static class ReactiveCredHubTemplateOAuth2Configuration {

		/**
		 * Create the {@link ReactiveCredHubTemplate} that the application will use to
		 * interact with CredHub.
		 * @param credHubProperties {@link CredHubProperties} for CredHub
		 * @param clientOptions client connection options
		 * @param clientRegistrationRepository a repository of OAuth2 client registrations
		 * @param authorizedClientRepository a repository of OAuth2 authorized clients
		 * @return the {@link CredHubTemplate} bean
		 */
		@Bean
		@ConditionalOnMissingBean
		ReactiveCredHubOperations reactiveCredHubTemplate(CredHubProperties credHubProperties,
				ClientOptions clientOptions, ReactiveClientRegistrationRepository clientRegistrationRepository,
				ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {

			return new CredHubTemplateFactory().reactiveCredHubTemplate(credHubProperties, clientOptions,
					clientRegistrationRepository, authorizedClientRepository);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
	@ConditionalOnProperty(prefix = "spring.credhub.oauth2", name = "registration-id")
	@ConditionalOnBean(ReactiveOAuth2AuthorizedClientManager.class)
	static class ReactiveCredHubTemplateOAuth2ClientConfiguration {

		/**
		 * Create the {@link ReactiveCredHubTemplate} that the application will use to
		 * interact with CredHub.
		 * @param credHubProperties {@link CredHubProperties} for CredHub
		 * @param clientOptions client connection options
		 * @param clientManager an OAuth2 authorization client manager
		 * @return the {@link CredHubTemplate} bean
		 */
		@Bean
		@ConditionalOnMissingBean
		ReactiveCredHubOperations reactiveCredHubTemplate(CredHubProperties credHubProperties,
				ClientOptions clientOptions, ReactiveOAuth2AuthorizedClientManager clientManager) {

			return new CredHubTemplateFactory().reactiveCredHubTemplate(credHubProperties, clientOptions,
					clientManager);
		}

	}

}
