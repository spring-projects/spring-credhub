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

public class PasswordCredentialRequestUnitTests extends CredHubRequestUnitTestsBase {

	@BeforeEach
	public void setUp() {
		this.requestBuilder = PasswordCredentialRequest.builder()
			.name(new SimpleCredentialName("example", "credential"))
			.mode(WriteMode.OVERWRITE)
			.value(new PasswordCredential("secret"));
	}

	@Test
	public void serializeWithPasswordValue() {
		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, WriteMode.OVERWRITE, "/example/credential", "password");
		JsonPathAssert.assertThat(json).hasPath("$.value").isEqualTo("secret");

		assertNoPermissions(json);
	}

	@Test
	public void serializeWithStringValue() {
		this.requestBuilder = PasswordCredentialRequest.builder()
			.name(new SimpleCredentialName("example", "credential"))
			.mode(WriteMode.OVERWRITE)
			.value("secret");

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, WriteMode.OVERWRITE, "/example/credential", "password");
		JsonPathAssert.assertThat(json).hasPath("$.value").isEqualTo("secret");

		assertNoPermissions(json);
	}

}
