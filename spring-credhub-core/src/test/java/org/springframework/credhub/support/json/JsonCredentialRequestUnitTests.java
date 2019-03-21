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

package org.springframework.credhub.support.json;

import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.SimpleCredentialName;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;

public class JsonCredentialRequestUnitTests extends CredHubRequestUnitTestsBase {

	@Before
	public void setUp() {
		requestBuilder = JsonCredentialRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.value(new JsonCredential() {
					{
						put("data", "value");
						put("test", true);
					}
				});
	}

	@Test
	public void serializeWithJsonValue() throws Exception {
		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, false, "/example/credential", "json");
		assertThat(jsonValue,
				allOf(hasJsonPath("$.value.data", equalTo("value")),
						hasJsonPath("$.value.test", equalTo(true))));

		assertNoPermissions(jsonValue);
	}
}