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
import org.springframework.credhub.core.permission.ReactiveCredHubPermissionOperations;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.ActorType;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.credhub.support.value.ValueCredentialRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

public class ReactivePermissionIntegrationTests extends ReactiveCredHubIntegrationTests {
	private static final SimpleCredentialName CREDENTIAL_NAME =
			new SimpleCredentialName("spring-credhub", "integration-test", "test-permissions-credential");
	private static final String CREDENTIAL_VALUE = "test-value";

	private ReactiveCredHubCredentialOperations credentials;
	private ReactiveCredHubPermissionOperations permissions;

	@Before
	public void setUp() {
		credentials = operations.credentials();
		permissions = operations.permissions();

		deleteCredentialIfExists(CREDENTIAL_NAME);
		deletePermissionsIfExist();
	}

	@After
	public void tearDown() {
		deleteCredentialIfExists(CREDENTIAL_NAME);
		deletePermissionsIfExist();
	}

	@Test
	public void managePermissionsServerV1() {
		assumeTrue(serverApiIsV1());

		StepVerifier.create(credentials.write(ValueCredentialRequest.builder()
				.name(CREDENTIAL_NAME)
				.value(CREDENTIAL_VALUE)
				.build()))
				.assertNext(response -> {
					assertThat(response.getName()).isEqualTo(CREDENTIAL_NAME);
					assertThat(response.getId()).isNotNull();
				})
				.verifyComplete();

		Permission appPermission = Permission.builder()
				.app("app1")
				.operation(Operation.READ)
				.build();
		Permission userPermission = Permission.builder()
				.user("user1")
				.operations(Operation.READ, Operation.WRITE, Operation.DELETE)
				.build();
		Permission clientPermission = Permission.builder()
				.client("client1")
				.operations(Operation.READ_ACL, Operation.WRITE_ACL)
				.build();

		StepVerifier.create(permissions.addPermissions(CREDENTIAL_NAME,
				appPermission,
				userPermission,
				clientPermission))
				.expectComplete()
				.verify();

		StepVerifier.create(permissions.getPermissions(CREDENTIAL_NAME))
				.assertNext(response -> assertThat(response.getActor().getAuthType()).isEqualTo(ActorType.OAUTH_CLIENT))
				.assertNext(response -> assertThat(response).isEqualTo(appPermission))
				.assertNext(response -> assertThat(response).isEqualTo(userPermission))
				.assertNext(response -> assertThat(response).isEqualTo(clientPermission))
				.verifyComplete();

		deletePermissionsIfExist();

		StepVerifier.create(permissions.getPermissions(CREDENTIAL_NAME))
				.assertNext(response -> assertThat(response.getActor().getAuthType()).isEqualTo(ActorType.OAUTH_CLIENT))
				.verifyComplete();
	}

	@Test
	public void managePermissionsServerV2() {
		assumeTrue(serverApiIsV2());

		StepVerifier.create(credentials.write(ValueCredentialRequest.builder()
				.name(CREDENTIAL_NAME)
				.value(CREDENTIAL_VALUE)
				.build()))
				.assertNext(response -> {
					assertThat(response.getName()).isEqualTo(CREDENTIAL_NAME);
					assertThat(response.getId()).isNotNull();
				})
				.verifyComplete();

		Permission appPermission = Permission.builder()
				.app("app1")
				.operation(Operation.READ)
				.build();
		Permission userPermission = Permission.builder()
				.user("user1")
				.operations(Operation.READ, Operation.WRITE, Operation.DELETE)
				.build();
		Permission clientPermission = Permission.builder()
				.client("client1")
				.operations(Operation.READ_ACL, Operation.WRITE_ACL)
				.build();

		StepVerifier.create(permissions.addPermissions(CREDENTIAL_NAME,
				appPermission,
				userPermission,
				clientPermission))
				.expectComplete()
				.verify();

		StepVerifier.create(permissions.getPermissions(CREDENTIAL_NAME))
				.assertNext(response -> assertThat(response).isEqualTo(appPermission))
				.assertNext(response -> assertThat(response).isEqualTo(userPermission))
				.assertNext(response -> assertThat(response).isEqualTo(clientPermission))
				.verifyComplete();

		deletePermissionsIfExist();

		StepVerifier.create(permissions.getPermissions(CREDENTIAL_NAME))
				.expectComplete()
				.verify();
	}

	private void deletePermissionsIfExist() {
		permissions.deletePermission(CREDENTIAL_NAME, Actor.app("app1"))
				.onErrorResume(e -> Mono.empty())
				.block();
		permissions.deletePermission(CREDENTIAL_NAME, Actor.user("user1"))
				.onErrorResume(e -> Mono.empty())
				.block();
		permissions.deletePermission(CREDENTIAL_NAME, Actor.client("client1"))
				.onErrorResume(e -> Mono.empty())
				.block();
	}
}
