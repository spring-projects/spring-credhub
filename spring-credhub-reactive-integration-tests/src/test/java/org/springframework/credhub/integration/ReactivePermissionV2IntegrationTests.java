/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.credhub.core.credential.ReactiveCredHubCredentialOperations;
import org.springframework.credhub.core.permissionV2.ReactiveCredHubPermissionV2Operations;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.ActorType;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.credhub.support.value.ValueCredentialRequest;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.springframework.credhub.support.permissions.ActorType.OAUTH_CLIENT;

public class ReactivePermissionV2IntegrationTests extends ReactiveCredHubIntegrationTests {
	private static final SimpleCredentialName CREDENTIAL_NAME =
			new SimpleCredentialName("spring-credhub", "integration-test", "test-permissionsV2-credential");
	private static final String CREDENTIAL_VALUE = "test-value";

	private ReactiveCredHubCredentialOperations credentials;
	private ReactiveCredHubPermissionV2Operations permissions;

	@Before
	public void setUp() {
		credentials = operations.credentials();
		permissions = operations.permissionsV2();

		deleteCredentialIfExists(CREDENTIAL_NAME);
	}

	@After
	public void tearDown() {
		deleteCredentialIfExists(CREDENTIAL_NAME);
	}

	@Test
	public void managePermissions() {
		assumeTrue(serverApiIsV2());

		AtomicReference<String> permissionId = new AtomicReference<>();

		StepVerifier.create(credentials.write(ValueCredentialRequest.builder()
				.name(CREDENTIAL_NAME)
				.value(CREDENTIAL_VALUE)
				.build()))
				.assertNext(response -> {
					assertThat(response.getName()).isEqualTo(CREDENTIAL_NAME);
					assertThat(response.getId()).isNotNull();
				})
				.verifyComplete();

		Permission clientPermission = Permission.builder()
				.client("client1")
				.operations(Operation.READ, Operation.WRITE, Operation.DELETE)
				.build();

		StepVerifier.create(permissions.addPermissions(CREDENTIAL_NAME, clientPermission))
				.assertNext(response -> {
					assertThat(response.getId()).isNotNull();
					assertThat(response.getPath()).isEqualTo(CREDENTIAL_NAME.getName());
					assertPermissions(response, OAUTH_CLIENT, "client1",
							Operation.READ, Operation.WRITE, Operation.DELETE);

					permissionId.set(response.getId());
				})
				.verifyComplete();

		StepVerifier.create(permissions.getPermissions(permissionId.get()))
				.assertNext(response -> {
					assertThat(response.getId()).isEqualTo(permissionId.get());
					assertThat(response.getPath()).isEqualTo(CREDENTIAL_NAME.getName());
					assertPermissions(response, OAUTH_CLIENT, "client1",
							Operation.READ, Operation.WRITE, Operation.DELETE);
				})
				.verifyComplete();

		StepVerifier.create(permissions.deletePermission(permissionId.get()))
				.expectComplete()
				.verify();
	}

	@Test
	public void updatePermissions() {
		assumeTrue(serverApiIsV2());

		AtomicReference<String> permissionId = new AtomicReference<>();

		StepVerifier.create(credentials.write(ValueCredentialRequest.builder()
				.name(CREDENTIAL_NAME)
				.value(CREDENTIAL_VALUE)
				.build()))
				.assertNext(response -> {
					assertThat(response.getName()).isEqualTo(CREDENTIAL_NAME);
					assertThat(response.getId()).isNotNull();
				})
				.verifyComplete();

		Permission clientPermission = Permission.builder()
				.client("client1")
				.operations(Operation.READ, Operation.WRITE, Operation.DELETE)
				.build();

		StepVerifier.create(permissions.addPermissions(CREDENTIAL_NAME, clientPermission))
				.assertNext(response -> {
					assertThat(response.getId()).isNotNull();
					assertThat(response.getPath()).isEqualTo(CREDENTIAL_NAME.getName());
					assertPermissions(response, OAUTH_CLIENT, "client1",
							Operation.READ, Operation.WRITE, Operation.DELETE);

					permissionId.set(response.getId());
				})
				.verifyComplete();

		Permission newPermission = Permission.builder()
				.client("client1")
				.operations(Operation.READ_ACL, Operation.WRITE_ACL)
				.build();

		StepVerifier.create(permissions.updatePermissions(permissionId.get(), CREDENTIAL_NAME, newPermission))
				.assertNext(response -> {
					assertThat(response.getId()).isEqualTo(permissionId.get());
					assertThat(response.getPath()).isEqualTo(CREDENTIAL_NAME.getName());
					assertPermissions(response, OAUTH_CLIENT, "client1",
							Operation.READ_ACL, Operation.WRITE_ACL);
				})
				.verifyComplete();

		StepVerifier.create(permissions.getPermissions(permissionId.get()))
				.assertNext(response -> {
					assertThat(response.getId()).isEqualTo(permissionId.get());
					assertThat(response.getPath()).isEqualTo(CREDENTIAL_NAME.getName());
					assertPermissions(response, OAUTH_CLIENT, "client1",
							Operation.READ_ACL, Operation.WRITE_ACL);
				})
				.verifyComplete();

		StepVerifier.create(permissions.deletePermission(permissionId.get()))
				.expectComplete()
				.verify();
	}

	private void assertPermissions(CredentialPermission credentialPermission,
								   ActorType actorType, String actorId, Operation... operations) {
		Actor actor = credentialPermission.getPermission().getActor();
		assertThat(actor.getAuthType()).isEqualTo(actorType);
		assertThat(actor.getPrimaryIdentifier()).isEqualTo(actorId);

		List<Operation> ops = credentialPermission.getPermission().getOperations();
		assertThat(ops).containsExactlyInAnyOrder(operations);
	}
}
