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

package org.springframework.credhub.support;

import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.Test;

import org.springframework.credhub.support.CredHubRequest.CredHubRequestBuilder;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;

public abstract class CredHubRequestUnitTestsBase {

	protected CredHubRequestBuilder requestBuilder;

	@Test
	@SuppressWarnings("deprecation")
	public void serializationWithOnePermission() {
		this.requestBuilder.permission(Permission.builder().app("app-id").operation(Operation.READ).build());

		DocumentContext json = JsonTestUtils.toJsonPath(this.requestBuilder.build());

		JsonPathAssert.assertThat(json)
			.hasPath("$.additional_permissions[0].actor")
			.isEqualTo(Actor.app("app-id").getIdentity());
		JsonPathAssert.assertThat(json).hasPath("$.additional_permissions[0].operations[0]").isEqualTo("read");
	}

	@Test
	@SuppressWarnings({ "deprecation" })
	public void serializationWithThreePermissions() {
		this.requestBuilder
			.permission(Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE).build())
			.permission(Permission.builder()
				.user("zone1", "user-id")
				.operations(Operation.READ_ACL, Operation.WRITE_ACL)
				.build())
			.permission(Permission.builder()
				.client("client-id")
				.operations(Operation.READ, Operation.WRITE, Operation.READ_ACL, Operation.WRITE_ACL)
				.build());

		DocumentContext json = JsonTestUtils.toJsonPath(this.requestBuilder.build());

		JsonPathAssert.assertThat(json)
			.hasPath("$.additional_permissions[0].actor")
			.isEqualTo(Actor.app("app-id").getIdentity());
		JsonPathAssert.assertThat(json).hasPath("$.additional_permissions[0].operations[0]").isEqualTo("read");
		JsonPathAssert.assertThat(json).hasPath("$.additional_permissions[0].operations[1]").isEqualTo("write");

		JsonPathAssert.assertThat(json)
			.hasPath("$.additional_permissions[1].actor")
			.isEqualTo(Actor.user("zone1", "user-id").getIdentity());
		JsonPathAssert.assertThat(json).hasPath("$.additional_permissions[1].operations[0]").isEqualTo("read_acl");
		JsonPathAssert.assertThat(json).hasPath("$.additional_permissions[1].operations[1]").isEqualTo("write_acl");

		JsonPathAssert.assertThat(json)
			.hasPath("$.additional_permissions[2].actor")
			.isEqualTo(Actor.client("client-id").getIdentity());
		JsonPathAssert.assertThat(json).hasPath("$.additional_permissions[2].operations[0]").isEqualTo("read");
		JsonPathAssert.assertThat(json).hasPath("$.additional_permissions[2].operations[1]").isEqualTo("write");
		JsonPathAssert.assertThat(json).hasPath("$.additional_permissions[2].operations[2]").isEqualTo("read_acl");
		JsonPathAssert.assertThat(json).hasPath("$.additional_permissions[2].operations[3]").isEqualTo("write_acl");
	}

	protected DocumentContext toJsonPath(CredHubRequestBuilder requestBuilder) {
		return JsonTestUtils.toJsonPath(requestBuilder.build());
	}

	protected void assertCommonRequestFields(DocumentContext json, Boolean overwrite, WriteMode writeMode, String name,
			String type) {
		if (overwrite == null) {
			JsonPathAssert.assertThat(json).hasNoPath("$.overwrite");
		}
		else {
			JsonPathAssert.assertThat(json).hasPath("$.overwrite").isEqualTo(overwrite);
		}
		JsonPathAssert.assertThat(json).hasPath("$.mode").isEqualTo(writeMode.getMode());
		JsonPathAssert.assertThat(json).hasPath("$.name").isEqualTo(name);
		JsonPathAssert.assertThat(json).hasPath("$.type").isEqualTo(type);
	}

	protected void assertNoPermissions(DocumentContext json) {
		JsonPathAssert.assertThat(json).hasNoPath("$.additional_permissions");
	}

}
