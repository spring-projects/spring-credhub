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

import com.jayway.jsonpath.DocumentContext;
import org.junit.Test;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.JsonParsingUnitTestsBase;
import org.springframework.credhub.support.JsonTestUtils;
import org.springframework.credhub.support.SimpleCredentialName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.credhub.support.JsonPathAssert.assertThat;

public class CredentialPermissionUnitTests extends JsonParsingUnitTestsBase {
	@Test
	public void deserializePermission() {
		String json = "{" +
				"\"uuid\": \"uuid\",\n" +
				"\"path\": \"/example-directory/example-specific-credential\",\n" +
				"\"actor\": \"uaa-user:106f52e2-5d01-4675-8d7a-c05ff9a2c081\",\n" +
				"\"operations\": [\"read\",\"write\"]" +
				"}";

		CredentialPermission credentialPermission = parsePermission(json);

		assertThat(credentialPermission.getId()).isEqualTo("uuid");
		assertThat(credentialPermission.getPath()).isEqualTo("/example-directory/example-specific-credential");

		Permission permission = credentialPermission.getPermission();

		Actor actor = permission.getActor();
		assertThat(actor.getAuthType()).isEqualTo(ActorType.USER);
		assertThat(actor.getPrimaryIdentifier()).isEqualTo("106f52e2-5d01-4675-8d7a-c05ff9a2c081");

		List<Operation> operations = permission.getOperations();
		assertThat(operations.size()).isEqualTo(2);
		assertThat(operations).contains(Operation.READ, Operation.WRITE);
	}

	@Test
	public void serializePermission() {
		CredentialPermission permission =
				new CredentialPermission(new SimpleCredentialName("example", "credential", "*"),
						Permission.builder()
								.app("appid1")
								.operations(Operation.READ, Operation.WRITE)
								.build());

		DocumentContext json = JsonTestUtils.toJsonPath(permission);

		assertThat(json).hasPath("$.path").isEqualTo("/example/credential/*");
		assertThat(json).hasPath("$.actor").isEqualTo(Actor.app("appid1").getIdentity());
		assertThat(json).hasPath("$.operations[0]").isEqualTo("read");
		assertThat(json).hasPath("$.operations[1]").isEqualTo("write");
	}

	private CredentialPermission parsePermission(String json) {
		return JsonTestUtils.fromJson(json, CredentialPermission.class);
	}
}
