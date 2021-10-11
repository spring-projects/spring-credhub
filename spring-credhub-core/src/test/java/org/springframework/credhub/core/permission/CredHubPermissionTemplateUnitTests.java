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

package org.springframework.credhub.core.permission;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.CredentialPermissions;
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
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class CredHubPermissionTemplateUnitTests {

	private static final SimpleCredentialName NAME = new SimpleCredentialName("example", "credential");

	@Mock
	private RestTemplate restTemplate;

	private CredHubPermissionOperations credHubTemplate;

	@BeforeEach
	public void setUp() {
		this.credHubTemplate = new CredHubTemplate(this.restTemplate).permissions();
	}

	@Test
	public void getPermissions() {
		CredentialPermissions expectedResponse = new CredentialPermissions(NAME,
				Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE).build(),
				Permission.builder().user("zone1", "user-id").operations(Operation.READ_ACL)
						.operations(Operation.WRITE_ACL).operation(Operation.DELETE).build());

		given(this.restTemplate.getForEntity(CredHubPermissionTemplate.PERMISSIONS_URL_QUERY,
				CredentialPermissions.class, NAME.getName()))
						.willReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

		List<Permission> response = this.credHubTemplate.getPermissions(NAME);

		assertThat(response).isNotNull();
		assertThat(response).hasSize(expectedResponse.getPermissions().size());
		assertThat(response).isEqualTo(expectedResponse.getPermissions());
	}

	@Test
	public void addPermissions() {
		Permission permission1 = Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE)
				.build();

		Permission permission2 = Permission.builder().user("zone1", "user-id").operations(Operation.READ_ACL)
				.operations(Operation.WRITE_ACL).operation(Operation.DELETE).build();

		this.credHubTemplate.addPermissions(NAME, permission1, permission2);

		verify(this.restTemplate).exchange(CredHubPermissionTemplate.PERMISSIONS_URL_PATH, HttpMethod.POST,
				new HttpEntity<>(new CredentialPermissions(NAME, permission1, permission2)),
				CredentialPermissions.class);
	}

	@Test
	public void deletePermission() {
		this.credHubTemplate.deletePermission(NAME, Actor.app("appid1"));

		verify(this.restTemplate).delete(CredHubPermissionTemplate.PERMISSIONS_ACTOR_URL_QUERY, NAME.getName(),
				ActorType.APP + ":appid1");
	}

}
