/*
 * Copyright 2016-2026 the original author or authors.
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

package org.springframework.credhub.core.permissionV2;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.credhub.core.CredHubClient;
import org.springframework.credhub.core.CredHubClientUnitTestsBase;
import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.ActorType;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * Unit tests of the v2 permission operations of {@link CredHubClient}, converted from the
 * tests for {@code CredHubPermissionV2Template}.
 */
class CredHubClientPermissionV2UnitTests extends CredHubClientUnitTestsBase {

	private static final SimpleCredentialName PATH = new SimpleCredentialName("example", "credential", "*");

	private static final String PERMISSIONS_URL = BASE_URL + "/api/v2/permissions";

	private CredHubPermissionV2Operations permissions;

	@BeforeEach
	void setUpPermissions() {
		this.permissions = this.client.permissionsV2();
	}

	private static String encode(String value) {
		return UriUtils.encode(value, StandardCharsets.UTF_8);
	}

	@Test
	void getPermissions() {
		String permissionId = "uuid";

		CredentialPermission expectedResponse = new CredentialPermission(PATH,
				Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE).build());

		this.server.expect(requestTo(PERMISSIONS_URL + "/" + permissionId))
			.andExpect(method(HttpMethod.GET))
			.andRespond(respondWith(expectedResponse));

		CredentialPermission response = this.permissions.getPermissions(permissionId);

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(ActorType.APP);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	void getPermissionsHandlesErrorStatus() {
		String permissionId = "uuid";

		this.server.expect(requestTo(PERMISSIONS_URL + "/" + permissionId))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withStatus(HttpStatus.NOT_FOUND));

		assertThatExceptionOfType(CredHubException.class)
			.isThrownBy(() -> this.permissions.getPermissions(permissionId))
			.withMessageContaining(statusLine(HttpStatus.NOT_FOUND));
	}

	@Test
	void getPermissionsByPathAndActor() {
		String clientId = "client-id";

		CredentialPermission expectedResponse = new CredentialPermission(PATH,
				Permission.builder().operation(Operation.READ).operation(Operation.WRITE).client(clientId).build());

		String actor = ActorType.OAUTH_CLIENT + ":" + clientId;
		this.server.expect(requestTo(PERMISSIONS_URL + "?path=" + encode(PATH.getName()) + "&actor=" + encode(actor)))
			.andExpect(method(HttpMethod.GET))
			.andRespond(respondWith(expectedResponse));

		CredentialPermission response = this.permissions.getPermissionsByPathAndActor(PATH, Actor.client(clientId));

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(ActorType.OAUTH_CLIENT);
		assertThat(response.getPermission().getActor().getPrimaryIdentifier()).isEqualTo(clientId);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	void getPermissionsByPathAndActorHandlesErrorStatus() {
		String clientId = "client-id";

		String actor = ActorType.OAUTH_CLIENT + ":" + clientId;
		this.server.expect(requestTo(PERMISSIONS_URL + "?path=" + encode(PATH.getName()) + "&actor=" + encode(actor)))
			.andExpect(method(HttpMethod.GET))
			.andRespond(withStatus(HttpStatus.NOT_FOUND));

		assertThatExceptionOfType(CredHubException.class)
			.isThrownBy(() -> this.permissions.getPermissionsByPathAndActor(PATH, Actor.client(clientId)))
			.withMessageContaining(statusLine(HttpStatus.NOT_FOUND));
	}

	@Test
	void addPermissions() {
		Permission permission = Permission.builder()
			.app("app-id")
			.operation(Operation.READ)
			.operation(Operation.WRITE)
			.build();

		CredentialPermission expectedResponse = new CredentialPermission(PATH, permission);

		this.server.expect(requestTo(PERMISSIONS_URL))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(expectedResponse))
			.andRespond(respondWith(expectedResponse));

		CredentialPermission response = this.permissions.addPermissions(PATH, permission);

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(ActorType.APP);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	void addPermissionsHandlesErrorStatus() {
		Permission permission = Permission.builder()
			.app("app-id")
			.operation(Operation.READ)
			.operation(Operation.WRITE)
			.build();

		CredentialPermission expectedRequest = new CredentialPermission(PATH, permission);

		this.server.expect(requestTo(PERMISSIONS_URL))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(expectedRequest))
			.andRespond(withStatus(HttpStatus.NOT_FOUND));

		assertThatExceptionOfType(CredHubException.class)
			.isThrownBy(() -> this.permissions.addPermissions(PATH, permission))
			.withMessageContaining(statusLine(HttpStatus.NOT_FOUND));
	}

	@Test
	void updatePermissions() {
		String permissionId = "uuid";

		Permission permission = Permission.builder()
			.app("app-id")
			.operation(Operation.READ)
			.operation(Operation.WRITE)
			.build();

		CredentialPermission expectedResponse = new CredentialPermission(PATH, permission);

		this.server.expect(requestTo(PERMISSIONS_URL + "/" + permissionId))
			.andExpect(method(HttpMethod.PUT))
			.andExpect(jsonBody(expectedResponse))
			.andRespond(respondWith(expectedResponse));

		CredentialPermission response = this.permissions.updatePermissions(permissionId, PATH, permission);

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(ActorType.APP);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	void updatePermissionsHandlesErrorStatus() {
		String permissionId = "uuid";

		Permission permission = Permission.builder()
			.app("app-id")
			.operation(Operation.READ)
			.operation(Operation.WRITE)
			.build();

		CredentialPermission expectedRequest = new CredentialPermission(PATH, permission);

		this.server.expect(requestTo(PERMISSIONS_URL + "/" + permissionId))
			.andExpect(method(HttpMethod.PUT))
			.andExpect(jsonBody(expectedRequest))
			.andRespond(withStatus(HttpStatus.NOT_FOUND));

		assertThatExceptionOfType(CredHubException.class)
			.isThrownBy(() -> this.permissions.updatePermissions(permissionId, PATH, permission))
			.withMessageContaining(statusLine(HttpStatus.NOT_FOUND));
	}

	@Test
	void patchPermissions() {
		String permissionId = "uuid";
		List<Operation> operations = Arrays.asList(Operation.READ, Operation.WRITE);

		CredentialPermission expectedResponse = new CredentialPermission(PATH,
				Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE).build());

		Map<String, List<Operation>> request = new HashMap<>();
		request.put("operations", operations);

		this.server.expect(requestTo(PERMISSIONS_URL + "/" + permissionId))
			.andExpect(method(HttpMethod.PATCH))
			.andExpect(jsonBody(request))
			.andRespond(respondWith(expectedResponse));

		CredentialPermission response = this.permissions.patchPermissions(permissionId, operations);

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	void patchPermissionsHandlesErrorStatus() {
		String permissionId = "uuid";
		List<Operation> operations = Arrays.asList(Operation.READ, Operation.WRITE);

		Map<String, List<Operation>> request = new HashMap<>();
		request.put("operations", operations);

		this.server.expect(requestTo(PERMISSIONS_URL + "/" + permissionId))
			.andExpect(method(HttpMethod.PATCH))
			.andExpect(jsonBody(request))
			.andRespond(withStatus(HttpStatus.NOT_FOUND));

		assertThatExceptionOfType(CredHubException.class)
			.isThrownBy(() -> this.permissions.patchPermissions(permissionId, operations))
			.withMessageContaining(statusLine(HttpStatus.NOT_FOUND));
	}

	@Test
	void deletePermission() {
		this.server.expect(requestTo(PERMISSIONS_URL + "/uuid"))
			.andExpect(method(HttpMethod.DELETE))
			.andRespond(withNoContent());

		this.permissions.deletePermission("uuid");
	}

}
