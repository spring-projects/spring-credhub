package org.springframework.credhub.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration.ClientFactoryWrapper;
import org.springframework.credhub.autoconfig.security.CredHubCredentialsDetails;
import org.springframework.credhub.configuration.OAuth2CredHubTemplateFactory;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * Auto configure a {@link OAuth2RestTemplate} with
 * {@link ClientCredentialsResourceDetails} if spring-security-oauth2 and proper
 * properties are available.
 * 
 * @author Daniel Lavoie
 */
@Configuration
@AutoConfigureBefore(CredHubAutoConfiguration.class)
@ConditionalOnProperty("spring.credhub.oauth2.client-id")
@ConditionalOnClass(name = "org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails")
public class CredHubOAuth2AutoConfiguration {

	public class CredHubOAuth2Configuration {
		private final OAuth2CredHubTemplateFactory credHubTemplateFactory = new OAuth2CredHubTemplateFactory();

		/**
		 * Bean that holds OAuth2 credential informations for CredHub.
		 */
		@Bean
		public CredHubCredentialsDetails credHubCredentialsDetails() {
			return new CredHubCredentialsDetails();
		}

		/**
		 * Preconfigured {@link OAuth2RestTemplate} with OAuth2 credentials for CredHub.
		 */
		@Bean
		public CredHubOperations oauth2CredHubTemplate(
				CredHubProperties credHubProperties,
				ClientFactoryWrapper clientFactoryWrapper) {
			return credHubTemplateFactory.credHubTemplate(credHubCredentialsDetails(),
					credHubProperties,
					clientFactoryWrapper.getClientHttpRequestFactory());
		}
	}
}
