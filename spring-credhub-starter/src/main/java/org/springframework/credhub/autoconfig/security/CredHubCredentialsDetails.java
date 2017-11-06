package org.springframework.credhub.autoconfig.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * Provides a {@link ClientCredentialsResourceDetails} for use to a
 * {@link OAuth2RestTemplate}.
 * 
 * @author Daniel Lavoie
 */
@ConfigurationProperties("spring.credhub.oauth2")
public class CredHubCredentialsDetails extends ClientCredentialsResourceDetails {

}
