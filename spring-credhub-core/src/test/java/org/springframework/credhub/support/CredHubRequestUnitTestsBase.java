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

package org.springframework.credhub.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredHubRequest.CredHubRequestBuilder;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.CredentialPermission;
import org.springframework.credhub.support.utils.JsonUtils;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.credhub.support.permissions.Operation.READ;
import static org.springframework.credhub.support.permissions.Operation.READ_ACL;
import static org.springframework.credhub.support.permissions.Operation.WRITE;
import static org.springframework.credhub.support.permissions.Operation.WRITE_ACL;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasNoJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.isJson;

public abstract class CredHubRequestUnitTestsBase {
	private ObjectMapper mapper;
	protected CredHubRequestBuilder requestBuilder;

	@Before
	public void setUpCredentialRequestUnitTests() {
		mapper = JsonUtils.buildObjectMapper();
	}

	@Test
	public void serializationWithOnePermission() throws Exception {
		requestBuilder
				.permission(CredentialPermission.builder()
						.app("app-id")
						.operation(READ)
						.build());

		String jsonValue = serializeToJson(requestBuilder);

		assertThat(jsonValue,
				allOf(hasJsonPath("$.additional_permissions[0].actor",
						equalTo(Actor.app("app-id").getIdentity())),
						hasJsonPath("$.additional_permissions[0].operations[0]",
								equalTo("read"))));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void serializationWithThreePermissions() throws Exception {
		requestBuilder
				.permission(CredentialPermission.builder()
						.app("app-id")
						.operation(READ).operation(WRITE)
						.build())
				.permission(CredentialPermission.builder()
						.user("zone1", "user-id")
						.operations(READ_ACL, WRITE_ACL)
						.build())
				.permission(CredentialPermission.builder()
						.client("client-id")
						.operations(READ, WRITE, READ_ACL, WRITE_ACL)
						.build());

		String jsonValue = serializeToJson(requestBuilder);

		assertThat(jsonValue, allOf(
				hasJsonPath("$.additional_permissions[0].actor",
						equalTo(Actor.app("app-id").getIdentity())),
				hasJsonPath("$.additional_permissions[0].operations[0]", equalTo("read")),
				hasJsonPath("$.additional_permissions[0].operations[1]", equalTo("write")),

				hasJsonPath("$.additional_permissions[1].actor",
						equalTo(Actor.user("zone1", "user-id").getIdentity())),
				hasJsonPath("$.additional_permissions[1].operations[0]", equalTo("read_acl")),
				hasJsonPath("$.additional_permissions[1].operations[1]", equalTo("write_acl")),

				hasJsonPath("$.additional_permissions[2].actor",
						equalTo(Actor.client("client-id").getIdentity())),
				hasJsonPath("$.additional_permissions[2].operations[0]", equalTo("read")),
				hasJsonPath("$.additional_permissions[2].operations[1]", equalTo("write")),
				hasJsonPath("$.additional_permissions[2].operations[2]", equalTo("read_acl")),
				hasJsonPath("$.additional_permissions[2].operations[3]", equalTo("write_acl"))
				)
		);
	}

	protected <T extends CredHubRequestBuilder> String serializeToJson(T requestBuilder)
			throws JsonProcessingException {
		String jsonValue = mapper.writeValueAsString(requestBuilder.build());
		assertThat(jsonValue, isJson());
		return jsonValue;
	}

	protected void assertCommonRequestFields(String jsonValue, boolean overwrite, String name, String type) {
		assertThat(jsonValue,
				allOf(hasJsonPath("$.overwrite", equalTo(overwrite)),
						hasJsonPath("$.name", equalTo(name)),
						hasJsonPath("$.type", equalTo(type))));
	}

	protected void assertNoPermissions(String jsonValue) {
		assertThat(jsonValue, hasNoJsonPath("$.additional_permissions"));
	}
}
