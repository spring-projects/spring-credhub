/*
 *
 * Copyright 2013-2017 the original author or authors.
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
 *
 */

package org.springframework.credhub.support.permissions;

import java.util.List;

import com.jayway.jsonpath.DocumentContext;
import org.junit.Test;

import org.springframework.credhub.support.CredentialPermissions;
import org.springframework.credhub.support.JsonParsingUnitTestsBase;
import org.springframework.credhub.support.JsonTestUtils;
import org.springframework.credhub.support.SimpleCredentialName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.credhub.support.JsonPathAssert.assertThat;
import static org.springframework.credhub.support.permissions.ActorType.APP;
import static org.springframework.credhub.support.permissions.ActorType.USER;

public class CredentialPermissionsUnitTests extends JsonParsingUnitTestsBase {
	@Test
	public void deserializePermissions() {
		String json = "{\"credential_name\": \"/c/example\"," +
				"\"permissions\": [" +
				"    {" +
				"      \"actor\": \"mtls-app:appid1\"," +
				"      \"operations\": [" +
				"        \"read\"," +
				"        \"write\"," +
				"        \"delete\"," +
				"        \"read_acl\"," +
				"        \"write_acl\"" +
				"      ]" +
				"    }," +
				"    {" +
				"      \"actor\": \"uaa-user:zone1/userid\"," +
				"      \"operations\": [" +
				"        \"read\"" +
				"      ]" +
				"    }" +
				"  ]}";

		CredentialPermissions permissions = parsePermissions(json);

		assertThat(permissions.getCredentialName()).isEqualTo("/c/example");
		assertThat(permissions.getPermissions().size()).isEqualTo(2);

		Permission permission = permissions.getPermissions().get(0);
		assertThat(permission.getActor().getAuthType()).isEqualTo(APP);
		assertThat(permission.getActor().getPrimaryIdentifier()).isEqualTo("appid1");

		List<Operation> operations = permission.getOperations();
		assertThat(operations.size()).isEqualTo(5);
		assertThat(operations).contains(Operation.READ, Operation.WRITE, Operation.DELETE,
				Operation.READ_ACL, Operation.WRITE_ACL);

		permission = permissions.getPermissions().get(1);
		assertThat(permission.getActor().getAuthType()).isEqualTo(USER);
		assertThat(permission.getActor().getPrimaryIdentifier()).isEqualTo("zone1/userid");

		operations = permission.getOperations();
		assertThat(operations.size()).isEqualTo(1);
		assertThat(operations).contains(Operation.READ);
	}

	@Test
	public void deserializeWithNoPermissions() {
		String json = "{\"permissions\": []}";

		CredentialPermissions permissions = parsePermissions(json);

		assertThat(permissions.getPermissions().size()).isEqualTo(0);
	}

	@Test
	public void serialize() {
		CredentialPermissions permissions =
				new CredentialPermissions(new SimpleCredentialName("example", "credentialName"),
						Permission.builder()
								.app("appid1")
								.operations(Operation.READ, Operation.WRITE)
								.build());

		DocumentContext json = JsonTestUtils.toJsonPath(permissions);

		assertThat(json).hasPath("$.credential_name").isEqualTo("/example/credentialName");
		assertThat(json).hasPath("$.permissions[0].actor").isEqualTo(Actor.app("appid1").getIdentity());
		assertThat(json).hasPath("$.permissions[0].operations[0]").isEqualTo("read");
		assertThat(json).hasPath("$.permissions[0].operations[1]").isEqualTo("write");
	}

	private CredentialPermissions parsePermissions(String json) {
		return JsonTestUtils.fromJson(json, CredentialPermissions.class);
	}
}
