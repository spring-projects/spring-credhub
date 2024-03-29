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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.support.ClientOptions;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring CredHub support beans.
 *
 * @author Scott Frederick
 * @author Daniel Lavoie
 */
@AutoConfiguration
@EnableConfigurationProperties
public class CredHubAutoConfiguration {

	/**
	 * Create a {@link CredHubProperties} bean and populate it from properties.
	 * @return a {@link CredHubProperties} bean
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty("spring.credhub.url")
	@ConfigurationProperties(prefix = "spring.credhub")
	public CredHubProperties credHubProperties() {
		return new CredHubProperties();
	}

	/**
	 * Create a {@link ClientOptions} bean and populate it from properties.
	 * @return a {@link ClientOptions} bean
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty("spring.credhub.url")
	@ConfigurationProperties(prefix = "spring.credhub")
	public ClientOptions clientOptions() {
		return new ClientOptions();
	}

}
