/*
 * Copyright 2016-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.integration;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.credhub.core.credential.CredHubCredentialOperations;
import org.springframework.credhub.core.permissionV2.CredHubPermissionV2Operations;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.ActorType;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.credhub.support.value.ValueCredentialRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class PermissionV2IntegrationTests extends CredHubIntegrationTests {

	private static final SimpleCredentialName CREDENTIAL_NAME = new SimpleCredentialName("spring-credhub",
			"integration-test", "test-permissionsV2-credential");

	private static final String CREDENTIAL_VALUE = "test-value";

	private CredHubCredentialOperations credentials;

	private CredHubPermissionV2Operations permissions;

	@BeforeEach
	public void setUp() {
		this.credentials = this.operations.credentials();
		this.permissions = this.operations.permissionsV2();

		deleteCredentialIfExists(CREDENTIAL_NAME);
	}

	@AfterEach
	public void tearDown() {
		deleteCredentialIfExists(CREDENTIAL_NAME);
	}

	@Test
	public void managePermissions() {
		assumeTrue(serverApiIsV2());

		this.credentials.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value(CREDENTIAL_VALUE).build());

		Permission clientPermission = Permission.builder()
			.client("client1")
			.operations(Operation.READ, Operation.WRITE, Operation.DELETE)
			.build();

		CredentialPermission added = this.permissions.addPermissions(CREDENTIAL_NAME, clientPermission);
		assertThat(added.getId()).isNotNull();
		assertThat(added.getPath()).isEqualTo(CREDENTIAL_NAME.getName());
		assertPermissions(added, ActorType.OAUTH_CLIENT, "client1", Operation.READ, Operation.WRITE, Operation.DELETE);

		String permissionId = added.getId();

		CredentialPermission retrieved = this.permissions.getPermissions(permissionId);
		assertThat(retrieved.getId()).isEqualTo(permissionId);
		assertThat(retrieved.getPath()).isEqualTo(CREDENTIAL_NAME.getName());
		assertPermissions(retrieved, ActorType.OAUTH_CLIENT, "client1", Operation.READ, Operation.WRITE,
				Operation.DELETE);

		this.permissions.deletePermission(permissionId);
	}

	@Test
	public void updatePermissions() {
		assumeTrue(serverApiIsV2());

		this.credentials.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value(CREDENTIAL_VALUE).build());

		Permission clientPermission = Permission.builder()
			.client("client1")
			.operations(Operation.READ, Operation.WRITE, Operation.DELETE)
			.build();

		CredentialPermission added = this.permissions.addPermissions(CREDENTIAL_NAME, clientPermission);
		assertThat(added.getId()).isNotNull();
		assertThat(added.getPath()).isEqualTo(CREDENTIAL_NAME.getName());
		assertPermissions(added, ActorType.OAUTH_CLIENT, "client1", Operation.READ, Operation.WRITE, Operation.DELETE);

		String permissionId = added.getId();

		Permission newPermission = Permission.builder()
			.client("client1")
			.operations(Operation.READ_ACL, Operation.WRITE_ACL)
			.build();

		CredentialPermission updated = this.permissions.updatePermissions(permissionId, CREDENTIAL_NAME, newPermission);
		assertThat(updated.getId()).isEqualTo(permissionId);
		assertThat(updated.getPath()).isEqualTo(CREDENTIAL_NAME.getName());
		assertPermissions(updated, ActorType.OAUTH_CLIENT, "client1", Operation.READ_ACL, Operation.WRITE_ACL);

		CredentialPermission retrieved = this.permissions.getPermissions(permissionId);
		assertThat(retrieved.getId()).isEqualTo(permissionId);
		assertThat(retrieved.getPath()).isEqualTo(CREDENTIAL_NAME.getName());
		assertPermissions(retrieved, ActorType.OAUTH_CLIENT, "client1", Operation.READ_ACL, Operation.WRITE_ACL);

		this.permissions.deletePermission(permissionId);
	}

	private void assertPermissions(CredentialPermission credentialPermission, ActorType actorType, String actorId,
			Operation... operations) {
		Actor actor = credentialPermission.getPermission().getActor();
		assertThat(actor.getAuthType()).isEqualTo(actorType);
		assertThat(actor.getPrimaryIdentifier()).isEqualTo(actorId);

		List<Operation> ops = credentialPermission.getPermission().getOperations();
		assertThat(ops).containsExactlyInAnyOrder(operations);
	}

}
