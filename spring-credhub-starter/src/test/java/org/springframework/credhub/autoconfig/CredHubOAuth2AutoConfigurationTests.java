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

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.assertj.ApplicationContextAssertProvider;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

public class CredHubOAuth2AutoConfigurationTests {

	private final Class[] configurations = { CredHubAutoConfiguration.class, CredHubOAuth2AutoConfiguration.class };

	private final String[] oAuth2ClientProperties = { "spring.credhub.oauth2.registration-id=test-client",
			"spring.security.oauth2.client.registration.credhub-client.provider=uaa",
			"spring.security.oauth2.client.registration.credhub-client.client-id=test-client",
			"spring.security.oauth2.client.registration.credhub-client.client-secret=test-secret",
			"spring.security.oauth2.client.registration.credhub-client.authorization-grant-type=client_credentials",
			"spring.security.oauth2.client.provider.uaa.token-uri=https://example.com/uaa/oauth/token" };

	private final ApplicationContextRunner context = new ApplicationContextRunner().withPropertyValues("debug=true")
			.withConfiguration(AutoConfigurations.of(this.configurations));

	@Test
	public void oauth2ContextConfigured() {
		this.context.withPropertyValues(this.oAuth2ClientProperties).run((context) -> {
			assertServletOAuth2ContextConfigured(context);
			assertReactiveOAuth2ContextConfigured(context);
		});
	}

	@Test
	public void oauth2ContextConfiguredWithoutWebClient() {
		this.context.withClassLoader(new FilteredClassLoader(WebClient.class))
				.withPropertyValues(this.oAuth2ClientProperties).run((context) -> {
					assertServletOAuth2ContextConfigured(context);
					assertReactiveOAuth2ContextNotConfigured(context);
				});
	}

	@Test
	public void oauth2ContextConfiguredWithWebApp() {
		new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(this.configurations))
				.withPropertyValues(this.oAuth2ClientProperties).run((context) -> {
					assertServletOAuth2ContextConfigured(context);
					assertReactiveOAuth2ContextConfigured(context);
				});
	}

	@Test
	public void oauth2ContextConfiguredWithReactiveWebApp() {
		new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(this.configurations))
				.withPropertyValues(this.oAuth2ClientProperties).run((context) -> {
					assertServletOAuth2ContextConfigured(context);
					assertReactiveOAuth2ContextConfigured(context);
				});
	}

	@Test
	public void oauth2ContextConfiguredWithReactiveWebAppNoServlet() {
		new ReactiveWebApplicationContextRunner().withClassLoader(new FilteredClassLoader("javax.servlet"))
				.withConfiguration(AutoConfigurations.of(this.configurations))
				.withPropertyValues(this.oAuth2ClientProperties).run((context) -> {
					assertServletOAuth2ContextNotConfigured(context);
					assertReactiveOAuth2ContextConfigured(context);
				});
	}

	@Test
	public void oauth2ContextNotConfiguredWithoutProperties() {
		this.context.run(this::assertOAuth2ContextNotConfigured);
	}

	@Test
	public void oauth2ContextNotConfiguredWithoutSpringSecurity() {
		this.context.withClassLoader(new FilteredClassLoader(ClientRegistration.class))
				.run(this::assertOAuth2ContextNotConfigured);
	}

	private void assertServletOAuth2ContextConfigured(
			ApplicationContextAssertProvider<? extends ApplicationContext> context) {

		assertThat(context).hasBean("credHubClientRegistrationRepository");
		assertThat(context).hasBean("credHubAuthorizedClientRepository");
	}

	private void assertServletOAuth2ContextNotConfigured(
			ApplicationContextAssertProvider<? extends ApplicationContext> context) {

		assertThat(context).doesNotHaveBean("credHubClientRegistrationRepository");
		assertThat(context).doesNotHaveBean("credHubAuthorizedClientRepository");
	}

	private void assertReactiveOAuth2ContextConfigured(
			ApplicationContextAssertProvider<? extends ApplicationContext> context) {

		assertThat(context).hasBean("credHubReactiveClientRegistrationRepository");
		assertThat(context).hasBean("credHubReactiveAuthorizedClientRepository");
	}

	private void assertReactiveOAuth2ContextNotConfigured(
			ApplicationContextAssertProvider<? extends ApplicationContext> context) {

		assertThat(context).doesNotHaveBean("credHubReactiveClientRegistrationRepository");
		assertThat(context).doesNotHaveBean("credHubReactiveAuthorizedClientRepository");
	}

	private void assertOAuth2ContextNotConfigured(
			ApplicationContextAssertProvider<? extends ApplicationContext> context) {

		assertServletOAuth2ContextNotConfigured(context);
		assertReactiveOAuth2ContextNotConfigured(context);
	}

}
