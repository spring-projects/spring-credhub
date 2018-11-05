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

package org.springframework.credhub.configuration;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration.ClientFactoryWrapper;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.support.ClientOptions;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class CredHubAutoConfigurationTests {

	@Test
	public void contextLoads() {
		new ApplicationContextRunner()
				.withConfiguration(AutoConfigurations.of(CredHubAutoConfiguration.class))
				.withPropertyValues(
						"spring.credhub.url=https://localhost",
						"spring.credhub.connection-timeout=30",
						"spring.credhub.read-timeout=60",
						"spring.credhub.oauth2.client-id=test-client"
				)
				.run((context) -> {
					assertThat(context).hasSingleBean(CredHubProperties.class);
					CredHubProperties properties = context.getBean(CredHubProperties.class);
					assertThat(properties.getUrl()).isEqualTo("https://localhost");
					assertThat(properties.getOauth2().getClientId()).isEqualTo("test-client");

					assertThat(context).hasSingleBean(ClientOptions.class);
					ClientOptions options = context.getBean(ClientOptions.class);
					assertThat(options.getConnectionTimeout()).isEqualTo(Duration.ofMillis(30));
					assertThat(options.getReadTimeout()).isEqualTo(Duration.ofMillis(60));

					assertThat(context).hasSingleBean(ClientFactoryWrapper.class);
				});
	}
}
