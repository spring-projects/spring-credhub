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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.JsonPathAssert;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteMode;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class SshCredentialRequestUnitTests extends CredHubRequestUnitTestsBase {

	@BeforeEach
	public void setUp() {
		buildRequest(new SshCredential("public-key", "private-key"));
	}

	@Test
	public void serializeWithPublicAndPrivateKey() {
		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, WriteMode.OVERWRITE, "/example/credential", "ssh");
		JsonPathAssert.assertThat(json).hasPath("$.value.public_key").isEqualTo("public-key");
		JsonPathAssert.assertThat(json).hasPath("$.value.private_key").isEqualTo("private-key");

		assertNoPermissions(json);
	}

	@Test
	public void serializeWithPublicKey() {
		buildRequest(new SshCredential("public-key", null));

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, WriteMode.OVERWRITE, "/example/credential", "ssh");
		JsonPathAssert.assertThat(json).hasPath("$.value.public_key").isEqualTo("public-key");
		JsonPathAssert.assertThat(json).hasNoPath("$.value.private_key");

		assertNoPermissions(json);
	}

	@Test
	public void serializeWithPrivateKey() {
		buildRequest(new SshCredential(null, "private-key"));

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, WriteMode.OVERWRITE, "/example/credential", "ssh");
		JsonPathAssert.assertThat(json).hasNoPath("$.value.public_key");
		JsonPathAssert.assertThat(json).hasPath("$.value.private_key").isEqualTo("private-key");

		assertNoPermissions(json);
	}

	@Test
	public void serializeWithNeitherKey() {
		assertThatIllegalArgumentException().isThrownBy(() -> {
			buildRequest(new SshCredential(null, null));

			toJsonPath(this.requestBuilder);
		});
	}

	private void buildRequest(SshCredential value) {
		this.requestBuilder = SshCredentialRequest.builder()
			.name(new SimpleCredentialName("example", "credential"))
			.mode(WriteMode.OVERWRITE)
			.value(value);
	}

}
