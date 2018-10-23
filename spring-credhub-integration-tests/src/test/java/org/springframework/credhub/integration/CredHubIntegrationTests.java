package org.springframework.credhub.integration;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubOAuth2TemplateAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubTemplateAutoConfiguration;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class,
		CredHubAutoConfiguration.class,
		CredHubTemplateAutoConfiguration.class,
		CredHubOAuth2TemplateAutoConfiguration.class})
@ActiveProfiles("test")
public abstract class CredHubIntegrationTests {

	@Autowired
	protected CredHubOperations operations;

}
