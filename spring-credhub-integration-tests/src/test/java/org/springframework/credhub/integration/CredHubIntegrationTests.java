package org.springframework.credhub.integration;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubOAuth2TemplateAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubTemplateAutoConfiguration;
import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.credential.CredHubCredentialOperations;
import org.springframework.credhub.support.SimpleCredentialName;
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

	boolean serverApiIsV1() {
		return operations.info().version().isVersion1();
	}

	boolean serverApiIsV2() {
		return operations.info().version().isVersion2();
	}

	void deleteCredentialIfExists(CredHubCredentialOperations credentialOperations, SimpleCredentialName credentialName) {
		try {
			credentialOperations.deleteByName(credentialName);
		} catch (CredHubException e) {
			// ignore failing deletes on cleanup
		}
	}
}
