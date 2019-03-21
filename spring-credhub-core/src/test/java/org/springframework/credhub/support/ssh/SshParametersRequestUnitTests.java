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

package org.springframework.credhub.support.ssh;

import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.KeyLength;
import org.springframework.credhub.support.SimpleCredentialName;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasNoJsonPath;

public class SshParametersRequestUnitTests extends CredHubRequestUnitTestsBase {
	@Before
	public void setUp() {
		requestBuilder = SshParametersRequest.builder();
	}
	@Test
	public void serializeWithParameters() throws Exception {
		requestBuilder = SshParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.parameters(new SshParameters(KeyLength.LENGTH_2048, "ssh comment"));

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "ssh");
		assertThat(jsonValue,
				allOf(hasJsonPath("$.parameters.key_length", equalTo(2048)),
						hasJsonPath("$.parameters.ssh_comment", equalTo("ssh comment"))));
	}

	@Test
	public void serializeWithLengthParameter() throws Exception {
		requestBuilder = SshParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.parameters(new SshParameters(KeyLength.LENGTH_2048));

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "ssh");
		assertThat(jsonValue,
				allOf(hasJsonPath("$.parameters.key_length", equalTo(2048)),
						hasNoJsonPath("$.parameters.ssh_comment")));
	}

	@Test
	public void serializeWithCommentParameter() throws Exception {
		requestBuilder = SshParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.parameters(new SshParameters("ssh comment"));

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "ssh");
		assertThat(jsonValue,
				allOf(hasNoJsonPath("$.parameters.key_length"),
						hasJsonPath("$.parameters.ssh_comment", equalTo("ssh comment"))));
	}

	@Test
	public void serializeWithNoParameters() throws Exception {
		requestBuilder = SshParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true);

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "ssh");
		assertThat(jsonValue,
				allOf(hasNoJsonPath("$.parameters.key_length"),
						hasNoJsonPath("$.parameters.ssh_comment")));
	}
}