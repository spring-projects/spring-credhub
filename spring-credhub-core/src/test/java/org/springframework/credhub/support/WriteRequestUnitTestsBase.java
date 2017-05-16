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

package org.springframework.credhub.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.credhub.support.WriteRequest.WriteRequestBuilder;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.credhub.support.AdditionalPermission.Operation.READ;
import static org.springframework.credhub.support.AdditionalPermission.Operation.WRITE;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.isJson;

public abstract class WriteRequestUnitTestsBase {
	protected ObjectMapper mapper;
	protected WriteRequestBuilder requestBuilder;

	@Before
	public void setUpWriteRequestUnitTestsBase() {
		mapper = new ObjectMapper();
	}

	@Test
	public void typeIsSerializable() {
		assertTrue(mapper.canSerialize(WriteRequest.class));
	}

	@Test
	public void serializationWithOnePermission() throws Exception {
		requestBuilder
				.additionalPermission(AdditionalPermission.builder()
						.app("app-id")
						.operation(READ)
						.build());

		String jsonValue = serializeToJson(requestBuilder);

		assertThat(jsonValue,
				allOf(hasJsonPath("$.additional_permissions[0].actor",
						equalTo("mtls-app:app-id")),
						hasJsonPath("$.additional_permissions[0].operations[0]",
								equalTo("read"))));
	}

	@Test
	public void serializationWithTwoPermissions() throws Exception {
		requestBuilder
				.additionalPermission(AdditionalPermission.builder()
						.app("app1-id")
						.operation(READ).operation(WRITE)
						.build())
				.additionalPermission(AdditionalPermission.builder()
						.app("app2-id")
						.operation(WRITE).operation(READ)
						.build());

		String jsonValue = serializeToJson(requestBuilder);

		assertThat(jsonValue, allOf(
				hasJsonPath("$.additional_permissions[0].actor",
						equalTo("mtls-app:app1-id")),
				hasJsonPath("$.additional_permissions[0].operations[0]", equalTo("read")),
				hasJsonPath("$.additional_permissions[0].operations[1]",
						equalTo("write")),
				hasJsonPath("$.additional_permissions[1].actor",
						equalTo("mtls-app:app2-id")),
				hasJsonPath("$.additional_permissions[1].operations[0]",
						equalTo("write")),
				hasJsonPath("$.additional_permissions[1].operations[1]",
						equalTo("read"))));
	}

	protected String serializeToJson(WriteRequestBuilder requestBuilder)
			throws JsonProcessingException {
		String jsonValue = mapper.writeValueAsString(requestBuilder.build());
		assertThat(jsonValue, isJson());
		return jsonValue;
	}
}
