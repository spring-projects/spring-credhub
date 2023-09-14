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

package org.springframework.credhub.support.password;

import com.jayway.jsonpath.DocumentContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.JsonPathAssert;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteMode;

@SuppressWarnings("deprecation")
public class PasswordParametersRequestUnitTests extends CredHubRequestUnitTestsBase {

	@BeforeEach
	public void setUp() {
		this.requestBuilder = PasswordParametersRequest.builder();
	}

	@Test
	public void serializeWithParameters() {
		this.requestBuilder = PasswordParametersRequest.builder()
			.name(new SimpleCredentialName("example", "credential"))
			.overwrite(true)
			.mode(WriteMode.OVERWRITE)
			.parameters(PasswordParameters.builder()
				.length(20)
				.excludeLower(true)
				.excludeUpper(false)
				.excludeNumber(true)
				.includeSpecial(false)
				.build());

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "password");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.length").isEqualTo(20);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.exclude_lower").isEqualTo(true);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.exclude_upper").isEqualTo(false);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.exclude_number").isEqualTo(true);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.include_special").isEqualTo(false);
	}

	@Test
	public void serializeWithEmptyParameters() {
		this.requestBuilder = PasswordParametersRequest.builder()
			.name(new SimpleCredentialName("example", "credential"))
			.overwrite(true)
			.mode(WriteMode.CONVERGE)
			.parameters(new PasswordParameters());

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.CONVERGE, "/example/credential", "password");
		assertParametersNotSet(json);
	}

	@Test
	public void serializeWithNoParameters() {
		this.requestBuilder = PasswordParametersRequest.builder()
			.name(new SimpleCredentialName("example", "credential"))
			.overwrite(true)
			.mode(WriteMode.NO_OVERWRITE);

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.NO_OVERWRITE, "/example/credential", "password");
		assertParametersNotSet(json);
	}

	private void assertParametersNotSet(DocumentContext json) {
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.length");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.exclude_lower");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.exclude_upper");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.exclude_number");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.include_special");
	}

}
