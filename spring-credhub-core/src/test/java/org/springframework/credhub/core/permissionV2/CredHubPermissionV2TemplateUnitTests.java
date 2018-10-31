/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.core.permissionV2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.permissionV2.CredHubPermissionV2Template.PERMISSIONS_ID_URL_PATH;
import static org.springframework.credhub.core.permissionV2.CredHubPermissionV2Template.PERMISSIONS_URL_PATH;
import static org.springframework.credhub.support.permissions.ActorType.APP;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class CredHubPermissionV2TemplateUnitTests {
	private static final SimpleCredentialName PATH =
			new SimpleCredentialName("example", "credential", "*");

	@Mock
	private RestTemplate restTemplate;

	private CredHubPermissionV2Operations credHubTemplate;

	@Before
	public void setUp() {
		credHubTemplate = new CredHubTemplate(restTemplate).permissionsV2();
	}

	@Test
	public void getPermissions() {
		String permissionId = "uuid";

		CredentialPermission expectedResponse = new CredentialPermission(
				PATH,
				Permission.builder()
						.app("app-id")
						.operation(Operation.READ)
						.operation(Operation.WRITE)
				.build());

		when(restTemplate.getForEntity(PERMISSIONS_ID_URL_PATH, CredentialPermission.class, permissionId))
				.thenReturn(new ResponseEntity<>(expectedResponse, OK));

		CredentialPermission response = credHubTemplate.getPermissions(permissionId);

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(APP);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	public void addPermissions() {
		Permission permission = Permission.builder()
				.app("app-id")
				.operation(Operation.READ)
				.operation(Operation.WRITE)
				.build();

		CredentialPermission expectedResponse = new CredentialPermission(PATH, permission);

		when(restTemplate.exchange(PERMISSIONS_URL_PATH, POST, new HttpEntity<>(expectedResponse),
				CredentialPermission.class))
				.thenReturn(new ResponseEntity<>(expectedResponse, OK));

		CredentialPermission response = credHubTemplate.addPermissions(PATH, permission);

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(APP);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	public void updatePermissions() {
		String permissionId = "uuid";

		Permission permission = Permission.builder()
				.app("app-id")
				.operation(Operation.READ)
				.operation(Operation.WRITE)
				.build();

		CredentialPermission expectedResponse = new CredentialPermission(PATH, permission);

		when(restTemplate.exchange(PERMISSIONS_ID_URL_PATH, PUT, new HttpEntity<>(expectedResponse),
				CredentialPermission.class, permissionId))
				.thenReturn(new ResponseEntity<>(expectedResponse, OK));

		CredentialPermission response = credHubTemplate.updatePermissions(permissionId, PATH, permission);

		assertThat(response).isNotNull();
		assertThat(response.getPath()).isEqualTo(PATH.getName());
		assertThat(response.getPermission().getActor().getAuthType()).isEqualTo(APP);
		assertThat(response.getPermission().getOperations()).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	public void deletePermission() {
		credHubTemplate.deletePermission("uuid");

		verify(restTemplate).delete(PERMISSIONS_ID_URL_PATH, "uuid");
	}
}