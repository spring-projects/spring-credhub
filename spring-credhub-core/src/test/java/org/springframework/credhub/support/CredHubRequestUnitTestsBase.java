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

import org.springframework.credhub.support.CredHubRequest.CredHubRequestBuilder;

public abstract class CredHubRequestUnitTestsBase {

	protected CredHubRequestBuilder requestBuilder;

	protected DocumentContext toJsonPath(CredHubRequestBuilder requestBuilder) {
		return JsonTestUtils.toJsonPath(requestBuilder.build());
	}

	protected void assertCommonRequestFields(DocumentContext json, WriteMode writeMode, String name, String type) {
		JsonPathAssert.assertThat(json).hasPath("$.mode").isEqualTo(writeMode.getMode());
		JsonPathAssert.assertThat(json).hasPath("$.name").isEqualTo(name);
		JsonPathAssert.assertThat(json).hasPath("$.type").isEqualTo(type);
	}

	protected void assertNoPermissions(DocumentContext json) {
		JsonPathAssert.assertThat(json).hasNoPath("$.additional_permissions");
	}

}
