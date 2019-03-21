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

import com.jayway.jsonpath.DocumentContext;
import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteMode;
import org.springframework.credhub.support.password.PasswordParameters;

import static org.springframework.credhub.support.JsonPathAssert.assertThat;

@SuppressWarnings("deprecation")
public class UserParametersRequestUnitTests extends CredHubRequestUnitTestsBase {
	@Before
	public void setUp() {
		requestBuilder = UserParametersRequest.builder();
	}

	@Test
	public void serializeWithParameters() {
		requestBuilder = UserParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.mode(WriteMode.OVERWRITE)
				.username("user")
				.parameters(PasswordParameters.builder()
						.length(20)
						.excludeLower(true)
						.excludeUpper(false)
						.excludeNumber(true)
						.includeSpecial(false)
						.build());

		DocumentContext json = toJsonPath(requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "user");
		assertThat(json).hasPath("$.value.username").isEqualTo("user");
		assertThat(json).hasPath("$.parameters.length").isEqualTo(20);
		assertThat(json).hasPath("$.parameters.exclude_lower").isEqualTo(true);
		assertThat(json).hasPath("$.parameters.exclude_upper").isEqualTo(false);
		assertThat(json).hasPath("$.parameters.exclude_number").isEqualTo(true);
		assertThat(json).hasPath("$.parameters.include_special").isEqualTo(false);
	}

	@Test
	public void serializeWithEmptyParameters() {
		requestBuilder = UserParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.mode(WriteMode.NO_OVERWRITE)
				.parameters(new PasswordParameters());

		DocumentContext json = toJsonPath(requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.NO_OVERWRITE, "/example/credential", "user");
		assertParametersNotSet(json);
	}

	@Test
	public void serializeWithNoParameters() {
		requestBuilder = UserParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.mode(WriteMode.CONVERGE);

		DocumentContext json = toJsonPath(requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.CONVERGE, "/example/credential", "user");
		assertParametersNotSet(json);
	}

	private void assertParametersNotSet(DocumentContext json) {
		assertThat(json).hasNoPath("$.value.username");
		assertThat(json).hasNoPath("$.parameters.length");
		assertThat(json).hasNoPath("$.parameters.exclude_lower");
		assertThat(json).hasNoPath("$.parameters.exclude_upper");
		assertThat(json).hasNoPath("$.parameters.exclude_number");
		assertThat(json).hasNoPath("$.parameters.include_special");
	}
}