package org.springframework.credhub.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.credhub.autoconfig.security.CredHubCredentialsDetails;
import org.springframework.credhub.configuration.CredHubOAuth2TemplateAutoConfigurationTest.TestConfig;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.OAuth2CredHubTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Daniel Lavoie
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class, properties = {
		"spring.credhub.url=https://localhost",
		"spring.credhub.oauth2.client-id=test-user",
		"spring.credhub.oauth2.client-secret=test-secret",
		"spring.credhub.oauth2.access-token-uri=https://uaa.example.com/oauth/token",
		"debug"
})
public class CredHubOAuth2TemplateAutoConfigurationTest {
	@Autowired
	private CredHubOperations credHubOperations;

	@Autowired
	@CredHubCredentialsDetails
	private ClientCredentialsResourceDetails credentialsDetails;

	@Test
	public void contextLoads() {
		assertThat(credHubOperations).isNotNull();
		assertThat(credHubOperations).isInstanceOf(OAuth2CredHubTemplate.class);

		assertThat(credentialsDetails).isNotNull();
		assertThat("test-user").isEqualTo(credentialsDetails.getClientId());
		assertThat("test-secret").isEqualTo(credentialsDetails.getClientSecret());
		assertThat("https://uaa.example.com/oauth/token").isEqualTo(credentialsDetails.getAccessTokenUri());
	}

	@SpringBootApplication
	public static class TestConfig {

	}
}
