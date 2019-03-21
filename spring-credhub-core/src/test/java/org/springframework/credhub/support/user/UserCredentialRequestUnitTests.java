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
import org.springframework.credhub.support.user.UserCredentialRequest.UserCredentialRequestBuilder;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasNoJsonPath;

public class UserCredentialRequestUnitTests extends CredHubRequestUnitTestsBase {
	@Before
	public void setUp() {
		requestBuilder = UserCredentialRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.value(new UserCredential("myname", "secret"));
	}

	@Test
	public void serializeWithUsernameAndPassword() throws Exception {
		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "user");
		assertThat(jsonValue,
				allOf(hasJsonPath("$.value.username", equalTo("myname")),
						hasJsonPath("$.value.password", equalTo("secret")),
						hasNoJsonPath("$.value.password_hash")));

		assertNoPermissions(jsonValue);
	}

	@Test
	public void serializeWithPassword() throws Exception {
		UserCredentialRequestBuilder builder = UserCredentialRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.value(new UserCredential("secret"));

		String jsonValue = serializeToJson(builder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "user");
		assertThat(jsonValue,
				allOf(hasNoJsonPath("$.value.username"),
						hasJsonPath("$.value.password", equalTo("secret")),
						hasNoJsonPath("$.value.password_hash")));

		assertNoPermissions(jsonValue);
	}
}