package org.springframework.credhub.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.credhub.configuration.CredHubTemplateAutoConfigurationTest.TestConfig;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.OAuth2CredHubTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Daniel Lavoie
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class, properties = "spring.credhub.url=http://localhost")
public class CredHubTemplateAutoConfigurationTest {
	@Autowired
	private CredHubTemplate credHubTemplate;
	
	@Autowired(required = false)
	private OAuth2CredHubTemplate oauth2CredHubTemplate;

	@Test
	public void contextLoads() {
		assertNotNull(credHubTemplate);
		assertTrue(credHubTemplate instanceof CredHubTemplate);

		assertNull(oauth2CredHubTemplate);
	}

	@SpringBootApplication
	public static class TestConfig {

	}
}
