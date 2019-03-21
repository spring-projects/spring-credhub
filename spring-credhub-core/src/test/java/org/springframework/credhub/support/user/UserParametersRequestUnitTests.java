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

package org.springframework.credhub.support.user;

import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.password.PasswordParameters;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasNoJsonPath;

public class UserParametersRequestUnitTests extends CredHubRequestUnitTestsBase {
	@Before
	public void setUp() {
		requestBuilder = UserParametersRequest.builder();
	}

	@Test
	public void serializeWithParameters() throws Exception {
		requestBuilder = UserParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.username("user")
				.parameters(PasswordParameters.builder()
						.length(20)
						.excludeLower(true)
						.excludeUpper(false)
						.excludeNumber(true)
						.includeSpecial(false)
						.build());

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "user");
		assertThat(jsonValue,
				allOf(hasJsonPath("$.value.username", equalTo("user")),
						hasJsonPath("$.parameters.length", equalTo(20)),
						hasJsonPath("$.parameters.exclude_lower", equalTo(true)),
						hasJsonPath("$.parameters.exclude_upper", equalTo(false)),
						hasJsonPath("$.parameters.exclude_number", equalTo(true)),
						hasJsonPath("$.parameters.include_special", equalTo(false))));
	}

	@Test
	public void serializeWithEmptyParameters() throws Exception {
		requestBuilder = UserParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.parameters(new PasswordParameters());

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "user");
		assertParametersNotSet(jsonValue);
	}

	@Test
	public void serializeWithNoParameters() throws Exception {
		requestBuilder = UserParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true);

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "user");
		assertParametersNotSet(jsonValue);
	}

	private void assertParametersNotSet(String jsonValue) {
		assertThat(jsonValue,
				allOf(hasNoJsonPath("$.value.username"),
						hasNoJsonPath("$.parameters.length"),
						hasNoJsonPath("$.parameters.exclude_lower"),
						hasNoJsonPath("$.parameters.exclude_upper"),
						hasNoJsonPath("$.parameters.exclude_number"),
						hasNoJsonPath("$.parameters.include_special")));
	}
}