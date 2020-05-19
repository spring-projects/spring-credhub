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
import org.springframework.credhub.autoconfig.CredHubTemplateConfiguration.CredHubTemplateBaseConfiguration;
import org.springframework.credhub.autoconfig.CredHubTemplateConfiguration.CredHubTemplateOAuth2ClientConfiguration;
import org.springframework.credhub.autoconfig.CredHubTemplateConfiguration.CredHubTemplateOAuth2Configuration;
import org.springframework.credhub.configuration.CredHubTemplateFactory;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

/**
 * Configuration for {@link CredHubTemplate}.
 *
 * @author Daniel Lavoie
 * @author Scott Frederick
 */
@Import({ CredHubTemplateBaseConfiguration.class, CredHubTemplateOAuth2Configuration.class,
		CredHubTemplateOAuth2ClientConfiguration.class })
public class CredHubTemplateConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = "javax.servlet.http.HttpServletRequest")
	@ConditionalOnProperty(prefix = "spring.credhub.oauth2", name = "registration-id", havingValue = "false",
			matchIfMissing = true)
	static class CredHubTemplateBaseConfiguration {

		/**
		 * Create the {@link CredHubTemplate} that the application will use to interact
		 * with CredHub.
		 * @param credHubProperties {@link CredHubProperties} for CredHub
		 * @param clientOptions client connection options
		 * @return the {@link CredHubOperations} bean
		 */
		@Bean
		@ConditionalOnMissingBean
		CredHubOperations credHubTemplate(CredHubProperties credHubProperties, ClientOptions clientOptions) {

			return new CredHubTemplateFactory().credHubTemplate(credHubProperties, clientOptions);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = "javax.servlet.http.HttpServletRequest")
	@ConditionalOnProperty(prefix = "spring.credhub.oauth2", name = "registration-id")
	@ConditionalOnMissingBean(OAuth2AuthorizedClientManager.class)
	static class CredHubTemplateOAuth2Configuration {

		/**
		 * Create the {@link CredHubTemplate} that the application will use to interact
		 * with CredHub.
		 * @param credHubProperties {@link CredHubProperties} for CredHub
		 * @param clientOptions client connection options
		 * @param clientRegistrationRepository a repository of OAuth2 client registrations
		 * @param authorizedClientRepository a repository of authorized OAuth2 clients
		 * @return the {@link CredHubOperations} bean
		 */
		@Bean
		@ConditionalOnMissingBean
		CredHubOperations credHubTemplate(CredHubProperties credHubProperties, ClientOptions clientOptions,
				ClientRegistrationRepository clientRegistrationRepository,
				OAuth2AuthorizedClientRepository authorizedClientRepository) {

			return new CredHubTemplateFactory().credHubTemplate(credHubProperties, clientOptions,
					clientRegistrationRepository, authorizedClientRepository);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(name = "javax.servlet.http.HttpServletRequest")
	@ConditionalOnProperty(prefix = "spring.credhub.oauth2", name = "registration-id")
	@ConditionalOnBean(OAuth2AuthorizedClientManager.class)
	static class CredHubTemplateOAuth2ClientConfiguration {

		/**
		 * Create the {@link CredHubTemplate} that the application will use to interact
		 * with CredHub.
		 * @param credHubProperties {@link CredHubProperties} for CredHub
		 * @param clientOptions client connection options
		 * @param clientRegistrationRepository a repository of OAuth2 client registrations
		 * @param clientManager an OAuth2 authorization client manager
		 * @return the {@link CredHubTemplate} bean
		 */
		@Bean
		@ConditionalOnMissingBean
		CredHubOperations credHubTemplate(CredHubProperties credHubProperties, ClientOptions clientOptions,
				ClientRegistrationRepository clientRegistrationRepository,
				OAuth2AuthorizedClientManager clientManager) {

			return new CredHubTemplateFactory().credHubTemplate(credHubProperties, clientOptions,
					clientRegistrationRepository, clientManager);
		}

	}

}
