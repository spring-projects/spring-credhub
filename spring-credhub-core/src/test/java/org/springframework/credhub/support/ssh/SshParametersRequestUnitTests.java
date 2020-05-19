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

package org.springframework.credhub.support.ssh;

import com.jayway.jsonpath.DocumentContext;
import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.JsonPathAssert;
import org.springframework.credhub.support.KeyLength;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteMode;

@SuppressWarnings("deprecation")
public class SshParametersRequestUnitTests extends CredHubRequestUnitTestsBase {

	@Before
	public void setUp() {
		this.requestBuilder = SshParametersRequest.builder();
	}

	@Test
	public void serializeWithParameters() {
		this.requestBuilder = SshParametersRequest.builder().name(new SimpleCredentialName("example", "credential"))
				.overwrite(true).mode(WriteMode.OVERWRITE)
				.parameters(new SshParameters(KeyLength.LENGTH_2048, "ssh comment"));

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "ssh");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_length").isEqualTo(2048);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.ssh_comment").isEqualTo("ssh comment");
	}

	@Test
	public void serializeWithLengthParameter() {
		this.requestBuilder = SshParametersRequest.builder().name(new SimpleCredentialName("example", "credential"))
				.overwrite(true).mode(WriteMode.NO_OVERWRITE).parameters(new SshParameters(KeyLength.LENGTH_2048));

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.NO_OVERWRITE, "/example/credential", "ssh");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_length").isEqualTo(2048);
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.ssh_comment");
	}

	@Test
	public void serializeWithCommentParameter() {
		this.requestBuilder = SshParametersRequest.builder().name(new SimpleCredentialName("example", "credential"))
				.overwrite(true).mode(WriteMode.CONVERGE).parameters(new SshParameters("ssh comment"));

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.CONVERGE, "/example/credential", "ssh");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.key_length");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.ssh_comment").isEqualTo("ssh comment");
	}

	@Test
	public void serializeWithNoParameters() {
		this.requestBuilder = SshParametersRequest.builder().name(new SimpleCredentialName("example", "credential"))
				.overwrite(true).mode(WriteMode.OVERWRITE);

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "ssh");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.key_length");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.ssh_comment");
	}

}
