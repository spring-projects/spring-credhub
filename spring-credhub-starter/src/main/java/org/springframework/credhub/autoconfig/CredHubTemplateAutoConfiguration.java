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

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration.ClientFactoryWrapper;
import org.springframework.credhub.configuration.CredHubTemplateFactory;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.core.CredHubTemplate;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link CredHubTemplate}.
 * 
 * @author Scott Frederick
 * @author Daniel Lavoie
 */

@Configuration
@AutoConfigureAfter(CredHubOAuth2TemplateAutoConfiguration.class)
@ConditionalOnProperty(value = "spring.credhub.url")
public class CredHubTemplateAutoConfiguration {
	private final CredHubTemplateFactory credHubTemplateFactory = new CredHubTemplateFactory();

	/**
	 * Create the {@link CredHubTemplate} that the application will use to interact
	 * with CredHub.
	 *
	 * @param credHubProperties {@link CredHubProperties} for CredHub
	 * @param clientFactoryWrapper a {@link ClientFactoryWrapper} to customize CredHub
	 * http requests
	 * @return the {@link CredHubTemplate} bean
	 */
	@Bean
	@ConditionalOnMissingBean
	public CredHubOperations credHubTemplate(CredHubProperties credHubProperties,
											 ClientFactoryWrapper clientFactoryWrapper) {
		return credHubTemplateFactory.credHubTemplate(credHubProperties,
				clientFactoryWrapper.getClientHttpRequestFactory());
	}
}
