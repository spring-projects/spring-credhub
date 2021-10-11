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

package org.springframework.credhub.core.permissionV2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.ActorType;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CredHubPermissionV2TemplateUnitTests {

	private static final SimpleCredentialName PATH = new SimpleCredentialName("example", "credential", "*");

	@Mock
	private RestTemplate restTemplate;

	private CredHubPermissionV2Operations credHubTemplate;

	@BeforeEach
	public void setUp() {
		this.credHubTemplate = new CredHubTemplate(this.restTemplate).permissionsV2();
	}

	@Test
	public void getPermissions() {
		String permissionId = "uuid";

		CredentialPermission expectedResponse = new CredentialPermission(PATH,
				Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE).build());

		given(this.restTemplate.getForEntity(CredHubPermissionV2Template.PERMISSIONS_ID_URL_PATH,
				CredentialPermission.class, permissionId))
						.willReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

		CredentialPermission response = this.credHubTemplate.getPermissions(permissionId);

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(ActorType.APP);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	public void getPermissionsByPathAndActor() {
		String clientId = "client-id";

		CredentialPermission expectedResponse = new CredentialPermission(PATH,
				Permission.builder().operation(Operation.READ).operation(Operation.WRITE).client(clientId).build());

		String actor = ActorType.OAUTH_CLIENT + ":" + clientId;
		given(this.restTemplate.getForEntity(CredHubPermissionV2Template.PERMISSIONS_PATH_ACTOR_URL_QUERY,
				CredentialPermission.class, PATH.getName(), actor))
						.willReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

		CredentialPermission response = this.credHubTemplate.getPermissionsByPathAndActor(PATH, Actor.client(clientId));

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(ActorType.OAUTH_CLIENT);
		assertThat(response.getPermission().getActor().getPrimaryIdentifier()).isEqualTo(clientId);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	public void addPermissions() {
		Permission permission = Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE)
				.build();

		CredentialPermission expectedResponse = new CredentialPermission(PATH, permission);

		given(this.restTemplate.exchange(CredHubPermissionV2Template.PERMISSIONS_URL_PATH, HttpMethod.POST,
				new HttpEntity<>(expectedResponse), CredentialPermission.class))
						.willReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

		CredentialPermission response = this.credHubTemplate.addPermissions(PATH, permission);

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(ActorType.APP);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	public void updatePermissions() {
		String permissionId = "uuid";

		Permission permission = Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE)
				.build();

		CredentialPermission expectedResponse = new CredentialPermission(PATH, permission);

		given(this.restTemplate.exchange(CredHubPermissionV2Template.PERMISSIONS_ID_URL_PATH, HttpMethod.PUT,
				new HttpEntity<>(expectedResponse), CredentialPermission.class, permissionId))
						.willReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

		CredentialPermission response = this.credHubTemplate.updatePermissions(permissionId, PATH, permission);

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(ActorType.APP);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	public void deletePermission() {
		this.credHubTemplate.deletePermission("uuid");

		verify(this.restTemplate).delete(CredHubPermissionV2Template.PERMISSIONS_ID_URL_PATH, "uuid");
	}

}
