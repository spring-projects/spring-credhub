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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import org.springframework.credhub.support.CredentialPermissions;
import org.springframework.credhub.support.JsonParsingUnitTestsBase;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.utils.JsonUtils;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.credhub.support.permissions.ActorType.APP;
import static org.springframework.credhub.support.permissions.ActorType.USER;
import static org.springframework.credhub.support.permissions.Operation.DELETE;
import static org.springframework.credhub.support.permissions.Operation.READ;
import static org.springframework.credhub.support.permissions.Operation.READ_ACL;
import static org.springframework.credhub.support.permissions.Operation.WRITE;
import static org.springframework.credhub.support.permissions.Operation.WRITE_ACL;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.isJson;

public class CredentialPermissionsUnitTests extends JsonParsingUnitTestsBase {
	@Test
	public void deserializePermissions() throws Exception {
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

		assertThat(permissions.getCredentialName(), equalTo("/c/example"));
		assertThat(permissions.getPermissions().size(), equalTo(2));

		CredentialPermission permission = permissions.getPermissions().get(0);
		assertThat(permission.getActor().getAuthType(), equalTo(APP));
		assertThat(permission.getActor().getPrimaryIdentifier(), equalTo("appid1"));

		List<Operation> operations = permission.getOperations();
		assertThat(operations.size(), equalTo(5));
		assertThat(operations, contains(READ, WRITE, DELETE, READ_ACL, WRITE_ACL));

		permission = permissions.getPermissions().get(1);
		assertThat(permission.getActor().getAuthType(), equalTo(USER));
		assertThat(permission.getActor().getPrimaryIdentifier(), equalTo("zone1/userid"));

		operations = permission.getOperations();
		assertThat(operations.size(), equalTo(1));
		assertThat(operations, contains(READ));
	}

	@Test
	public void deserializeWithNoPermissions() throws Exception {
		String json = "{\"permissions\": []}";

		CredentialPermissions permissions = parsePermissions(json);

		assertThat(permissions.getPermissions().size(), equalTo(0));
	}

	@Test
	public void serialize() throws Exception {
		CredentialPermissions permissions = new CredentialPermissions(new SimpleCredentialName("example", "credentialName"),
				CredentialPermission.builder()
						.app("appid1")
						.operations(READ, WRITE)
						.build());

		String jsonValue = serializeToJson(permissions);

		assertThat(jsonValue,
				allOf(hasJsonPath("$.credential_name", equalTo("/example/credentialName")),
						hasJsonPath("$.permissions[0].actor", equalTo(Actor.app("appid1").getIdentity())),
						hasJsonPath("$.permissions[0].operations[0]", equalTo("read")),
						hasJsonPath("$.permissions[0].operations[1]", equalTo("write"))));
	}

	protected String serializeToJson(Object obj) throws JsonProcessingException {
		String jsonValue = JsonUtils.buildObjectMapper().writeValueAsString(obj);
		assertThat(jsonValue, isJson());
		return jsonValue;
	}

	@SuppressWarnings("unchecked")
	private CredentialPermissions parsePermissions(String json) throws java.io.IOException {
		return objectMapper.readValue(json, CredentialPermissions.class);
	}
}
