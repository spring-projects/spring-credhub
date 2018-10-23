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
import org.springframework.credhub.core.CredHubPermissionsOperations;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.CredentialPermission;
import org.springframework.credhub.support.permissions.Operation;
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
public class PermissionsIntegrationTests {
	private static final SimpleCredentialName CREDENTIAL_NAME =
			new SimpleCredentialName("spring-credhub", "integration-test", "test-permissions-credential");
	private static final String CREDENTIAL_VALUE = "test-value";

	@Autowired
	private CredHubOperations operations;

	private CredHubCredentialsOperations credentials;
	private CredHubPermissionsOperations permissions;

	@Before
	public void setUp() {
		credentials = operations.credentials();
		permissions = operations.permissions();
	}

	@Test
	public void managePermissions() {
		credentials.write(ValueCredentialRequest.builder()
				.name(CREDENTIAL_NAME)
				.value(CREDENTIAL_VALUE)
				.build());

		CredentialPermission appPermission = CredentialPermission.builder()
				.app("app1")
				.operation(Operation.READ)
				.build();
		CredentialPermission userPermission = CredentialPermission.builder()
				.user("user1")
				.operations(Operation.READ, Operation.WRITE, Operation.DELETE)
				.build();
		CredentialPermission clientPermission = CredentialPermission.builder()
				.client("client1")
				.operations(Operation.READ_ACL, Operation.WRITE_ACL)
				.build();

		permissions.addPermissions(CREDENTIAL_NAME,
				appPermission,
				userPermission,
				clientPermission);

		List<CredentialPermission> retrievedPermissions = permissions.getPermissions(CREDENTIAL_NAME);
		assertThat(retrievedPermissions).hasSize(3);

		assertThat(retrievedPermissions).containsExactlyInAnyOrder(appPermission, userPermission, clientPermission);

		permissions.deletePermission(CREDENTIAL_NAME, Actor.app("app1"));
		permissions.deletePermission(CREDENTIAL_NAME, Actor.user("user1"));
		permissions.deletePermission(CREDENTIAL_NAME, Actor.client("client1"));

		List<CredentialPermission> afterDelete = permissions.getPermissions(CREDENTIAL_NAME);
		assertThat(afterDelete).hasSize(0);

		operations.credentials().deleteByName(CREDENTIAL_NAME);
	}
}
