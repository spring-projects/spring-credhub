package org.springframework.credhub.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(OAuth2ClientProperties.class)
@AutoConfigureBefore(ReactiveOAuth2ClientAutoConfiguration.class)
@ConditionalOnClass(ClientRegistration.class)
public class CredHubOAuth2AutoConfiguration {

	private final OAuth2ClientProperties properties;

	CredHubOAuth2AutoConfiguration(OAuth2ClientProperties properties) {
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	public ClientRegistrationRepository credHubClientRegistrationRepository() {
		List<ClientRegistration> registrations = new ArrayList<>(
				OAuth2ClientPropertiesRegistrationAdapter
						.getClientRegistrations(this.properties).values());
		return new InMemoryClientRegistrationRepository(registrations);
	}

	@Bean
	@ConditionalOnMissingBean
	public OAuth2AuthorizedClientService credHubAuthorizedClientService(
			ClientRegistrationRepository clientRegistrationRepository) {
		return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
	}

	/**
	 * Create a {@code ServerOAuth2AuthorizedClientRepository} bean to override the default
	 * provided by Spring Boot auto-configuration.
	 *
	 * @return the {@code ServerOAuth2AuthorizedClientRepository}
	 */
	@Bean
	@ConditionalOnMissingBean
	public ServerOAuth2AuthorizedClientRepository credHubAuthorizedClientRepository() {
		return new UnAuthenticatedServerOAuth2AuthorizedClientRepository();
	}
}