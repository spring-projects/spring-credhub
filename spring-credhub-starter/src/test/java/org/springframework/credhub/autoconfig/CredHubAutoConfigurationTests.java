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

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class CredHubAutoConfigurationTests {

	private final ApplicationContextRunner context = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					CredHubAutoConfiguration.class
			));

	@Test
	public void autoConfiguredWithDefaultProperties() {
		context
				.withPropertyValues(
						"spring.credhub.url=https://localhost",
						"spring.credhub.oauth2.registration-id=test-client",
						"spring.credhub.connection-timeout=30",
						"spring.credhub.read-timeout=60",
						"debug=true"
				)
				.run(this::assertPropertiesConfigured);
	}

	@Test
	public void autoConfiguredWithCustomProperties() {
		context
				.withConfiguration(AutoConfigurations.of(CustomPropertiesConfiguration.class))
				.withPropertyValues(
						"my.custom.credhub.url=https://localhost",
						"my.custom.credhub.oauth2.registration-id=test-client",
						"my.custom.credhub.connection-timeout=30",
						"my.custom.credhub.read-timeout=60"
				)
				.run(this::assertPropertiesConfigured);
	}

	@Test
	public void webClientConnectorNotConfigured() {
		context
				.withClassLoader(new FilteredClassLoader(WebClient.class))
				.withPropertyValues(
						"spring.credhub.url=https://localhost",
						"spring.credhub.oauth2.registration-id=test-client",
						"spring.credhub.connection-timeout=30",
						"spring.credhub.read-timeout=60"
				)
				.run(context -> assertThat(context).doesNotHaveBean(ClientHttpConnector.class));
	}

	private void assertPropertiesConfigured(AssertableApplicationContext context) {
		assertThat(context).hasSingleBean(CredHubProperties.class);
		CredHubProperties properties = context.getBean(CredHubProperties.class);
		assertThat(properties.getUrl()).isEqualTo("https://localhost");
		assertThat(properties.getOauth2().getRegistrationId()).isEqualTo("test-client");

		assertThat(context).hasSingleBean(ClientOptions.class);
		ClientOptions options = context.getBean(ClientOptions.class);
		assertThat(options.getConnectionTimeout()).isEqualTo(Duration.ofMillis(30));
		assertThat(options.getReadTimeout()).isEqualTo(Duration.ofMillis(60));
	}

	@Configuration
	@AutoConfigureBefore(CredHubAutoConfiguration.class)
	public static class CustomPropertiesConfiguration {
		@Bean
		@ConfigurationProperties(prefix = "my.custom.credhub")
		public CredHubProperties credHubProperties() {
			return new CredHubProperties();
		}

		@Bean
		@ConfigurationProperties(prefix = "my.custom.credhub")
		public ClientOptions clientOptions() {
			return new ClientOptions();
		}
	}
}
