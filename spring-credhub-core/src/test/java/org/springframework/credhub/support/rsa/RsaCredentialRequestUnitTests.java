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

package org.springframework.credhub.support.rsa;

import com.jayway.jsonpath.DocumentContext;
import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteMode;

import static org.springframework.credhub.support.JsonPathAssert.assertThat;

public class RsaCredentialRequestUnitTests extends CredHubRequestUnitTestsBase {
	@Before
	public void setUp() {
		buildRequest(new RsaCredential("public-key", "private-key"));
	}

	@Test
	public void serializeWithPublicAndPrivateKey() {
		DocumentContext json = toJsonPath(requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "rsa");
		assertThat(json).hasPath("$.value.public_key").isEqualTo("public-key");
		assertThat(json).hasPath("$.value.private_key").isEqualTo("private-key");

		assertNoPermissions(json);
	}

	@Test
	public void serializeWithPublicKey() {
		buildRequest(new RsaCredential("public-key", null));

		DocumentContext json = toJsonPath(requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "rsa");

		assertThat(json).hasPath("$.value.public_key").isEqualTo("public-key");
		assertThat(json).hasNoPath("$.value.private_key");

		assertNoPermissions(json);
	}

	@Test
	public void serializeWithPrivateKey() {
		buildRequest(new RsaCredential(null, "private-key"));

		DocumentContext json = toJsonPath(requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "rsa");
		assertThat(json).hasNoPath("$.value.public_key");
		assertThat(json).hasPath("$.value.private_key").isEqualTo("private-key");

		assertNoPermissions(json);
	}

	@Test(expected = IllegalArgumentException.class)
	public void serializeWithNeitherKey() {
		buildRequest(new RsaCredential(null, null));

		toJsonPath(requestBuilder);
	}

	@SuppressWarnings("deprecation")
	private void buildRequest(RsaCredential value) {
		requestBuilder = RsaCredentialRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.mode(WriteMode.OVERWRITE)
				.value(value);
	}
}