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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
		assertNotNull(credHubOperations);
		assertThat(credHubOperations instanceof OAuth2CredHubTemplate);

		assertNotNull(credentialsDetails);
		assertEquals("test-user", credentialsDetails.getClientId());
		assertEquals("test-secret", credentialsDetails.getClientSecret());
		assertEquals("https://uaa.example.com/oauth/token", credentialsDetails.getAccessTokenUri());
	}

	@SpringBootApplication
	public static class TestConfig {

	}
}
