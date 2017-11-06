package org.springframework.credhub.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.credhub.configuration.CredHubOAuth2AutoConfigurationTest.TestConfig;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.OAuth2CredHubTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Daniel Lavoie
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class, value = {
		"spring.credhub.url=https://localhost",
		"spring.credhub.oauth2.client-id=test-user", "debug" })
public class CredHubOAuth2AutoConfigurationTest {
	@Autowired
	private OAuth2CredHubTemplate oauth2CredHubTemplate;

	@Autowired
	private CredHubOperations credHubOptions;

	@Test
	public void contextLoads() {
		Assert.assertNotNull(oauth2CredHubTemplate);
		Assert.assertTrue(credHubOptions instanceof OAuth2CredHubTemplate);
	}

	@SpringBootApplication
	public static class TestConfig {

	}
}
