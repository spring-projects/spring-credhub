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
import org.springframework.credhub.support.user.UserCredentialRequest.UserCredentialRequestBuilder;

import static org.springframework.credhub.support.JsonPathAssert.assertThat;

@SuppressWarnings("deprecation")
public class UserCredentialRequestUnitTests extends CredHubRequestUnitTestsBase {
	@Before
	public void setUp() {
		requestBuilder = UserCredentialRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.mode(WriteMode.OVERWRITE)
				.value(new UserCredential("myname", "secret"));
	}

	@Test
	public void serializeWithUsernameAndPassword() {
		DocumentContext json = toJsonPath(requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "user");
		assertThat(json).hasPath("$.value.username").isEqualTo("myname");
		assertThat(json).hasPath("$.value.password").isEqualTo("secret");
		assertThat(json).hasNoPath("$.value.password_hash");

		assertNoPermissions(json);
	}

	@Test
	public void serializeWithPassword() {
		UserCredentialRequestBuilder builder = UserCredentialRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.mode(WriteMode.OVERWRITE)
				.value(new UserCredential("secret"));

		DocumentContext json = toJsonPath(builder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "user");
		assertThat(json).hasNoPath("$.value.username");
		assertThat(json).hasPath("$.value.password").isEqualTo("secret");
		assertThat(json).hasNoPath("$.value.password_hash");

		assertNoPermissions(json);
	}
}