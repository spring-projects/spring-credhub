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

package org.springframework.credhub.core.permission;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.credhub.core.CredHubClient;
import org.springframework.credhub.core.CredHubClientUnitTestsBase;
import org.springframework.credhub.support.CredentialPermissions;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.ActorType;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit tests of the v1 permission operations of {@link CredHubClient}, converted from the
 * tests for {@code CredHubPermissionTemplate}.
 */
class CredHubClientPermissionUnitTests extends CredHubClientUnitTestsBase {

	private static final SimpleCredentialName NAME = new SimpleCredentialName("example", "credential");

	private static final String PERMISSIONS_URL = BASE_URL + "/api/v1/permissions";

	private CredHubPermissionOperations permissions;

	@BeforeEach
	void setUpPermissions() {
		this.permissions = this.client.permissions();
	}

	private static String encode(String value) {
		return UriUtils.encode(value, StandardCharsets.UTF_8);
	}

	@Test
	void getPermissions() {
		CredentialPermissions expectedResponse = new CredentialPermissions(NAME,
				Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE).build(),
				Permission.builder()
					.user("zone1", "user-id")
					.operations(Operation.READ_ACL)
					.operations(Operation.WRITE_ACL)
					.operation(Operation.DELETE)
					.build());

		this.server.expect(requestTo(PERMISSIONS_URL + "?credential_name=" + encode(NAME.getName())))
			.andExpect(method(HttpMethod.GET))
			.andRespond(respondWith(expectedResponse));

		List<Permission> response = this.permissions.getPermissions(NAME);

		assertThat(response).isNotNull();
		assertThat(response).hasSize(expectedResponse.getPermissions().size());
		assertThat(response).isEqualTo(expectedResponse.getPermissions());
	}

	@Test
	void addPermissions() {
		Permission permission1 = Permission.builder()
			.app("app-id")
			.operation(Operation.READ)
			.operation(Operation.WRITE)
			.build();

		Permission permission2 = Permission.builder()
			.user("zone1", "user-id")
			.operations(Operation.READ_ACL)
			.operations(Operation.WRITE_ACL)
			.operation(Operation.DELETE)
			.build();

		this.server.expect(requestTo(PERMISSIONS_URL))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(new CredentialPermissions(NAME, permission1, permission2)))
			.andRespond(withSuccess());

		this.permissions.addPermissions(NAME, permission1, permission2);
	}

	@Test
	void deletePermission() {
		this.server
			.expect(requestTo(PERMISSIONS_URL + "?credential_name=" + encode(NAME.getName()) + "&actor="
					+ encode(ActorType.APP + ":appid1")))
			.andExpect(method(HttpMethod.DELETE))
			.andRespond(withNoContent());

		this.permissions.deletePermission(NAME, Actor.app("appid1"));
	}

}
