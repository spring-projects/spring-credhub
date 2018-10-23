package org.springframework.credhub.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubOAuth2TemplateAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubTemplateAutoConfiguration;
import org.springframework.credhub.core.CredHubCredentialsOperations;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.value.ValueCredential;
import org.springframework.credhub.support.value.ValueCredentialRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class,
				CredHubAutoConfiguration.class,
				CredHubTemplateAutoConfiguration.class,
				CredHubOAuth2TemplateAutoConfiguration.class})
@ActiveProfiles("test")
public class CredentialIntegrationTests {
	private static final SimpleCredentialName CREDENTIAL_NAME =
			new SimpleCredentialName("spring-credhub", "integration-test", "test-value-credential");
	private static final String CREDENTIAL_VALUE = "test-value";

	@Autowired
	private CredHubOperations operations;

	private CredHubCredentialsOperations credentials;

	@Before
	public void setUp() {
		credentials = operations.credentials();
	}
	
	@Test
	public void writeCredential() {
		CredentialDetails<ValueCredential> written = credentials.write(ValueCredentialRequest.builder()
				.name(CREDENTIAL_NAME)
				.value(CREDENTIAL_VALUE)
				.build());
		assertThat(written.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(written.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
		assertThat(written.getCredentialType()).isEqualTo(CredentialType.VALUE);
		assertThat(written.getId()).isNotNull();

		CredentialDetails<ValueCredential> byId = credentials.getById(written.getId(), ValueCredential.class);
		assertThat(byId.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(byId.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
		assertThat(byId.getCredentialType()).isEqualTo(CredentialType.VALUE);

		CredentialDetails<ValueCredential> byName = credentials.getByName(CREDENTIAL_NAME, ValueCredential.class);
		assertThat(byName.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(byName.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
		assertThat(byName.getCredentialType()).isEqualTo(CredentialType.VALUE);

		List<CredentialSummary> foundByName = credentials.findByName(new SimpleCredentialName("/test"));
		assertThat(foundByName).hasSize(1);
		assertThat(foundByName).extracting("name").extracting("name").containsExactly(CREDENTIAL_NAME.getName());

		List<CredentialSummary> foundByPath = credentials.findByPath("/spring-credhub/integration-test");
		assertThat(foundByPath).hasSize(1);
		assertThat(foundByPath).extracting("name").extracting("name").containsExactly(CREDENTIAL_NAME.getName());

		credentials.deleteByName(CREDENTIAL_NAME);
		
		List<CredentialSummary> afterDelete = credentials.findByName(CREDENTIAL_NAME);
		assertThat(afterDelete).hasSize(0);
	}
}
