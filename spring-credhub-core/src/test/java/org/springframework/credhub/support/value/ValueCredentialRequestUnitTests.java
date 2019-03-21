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

package org.springframework.credhub.support.value;

import com.jayway.jsonpath.DocumentContext;
import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.SimpleCredentialName;

import static org.springframework.credhub.support.JsonPathAssert.assertThat;

public class ValueCredentialRequestUnitTests extends CredHubRequestUnitTestsBase {

	@Before
	@SuppressWarnings("deprecation")
	public void setUp() {
		requestBuilder = ValueCredentialRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.value(new ValueCredential("somevalue"));
	}

	@Test
	public void serializeWithValue() {
		DocumentContext json = toJsonPath(requestBuilder);

		assertThat(json).hasPath("$.overwrite").isEqualTo(true);
		assertThat(json).hasPath("$.name").isEqualTo("/example/credential");
		assertThat(json).hasPath("$.type").isEqualTo("value");
		assertThat(json).hasPath("$.value").isEqualTo("somevalue");

		assertNoPermissions(json);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void serializeWithStringValue() {
		requestBuilder = ValueCredentialRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.value("somevalue");

		DocumentContext json = toJsonPath(requestBuilder);

		assertThat(json).hasPath("$.overwrite").isEqualTo(true);
		assertThat(json).hasPath("$.name").isEqualTo("/example/credential");
		assertThat(json).hasPath("$.type").isEqualTo("value");
		assertThat(json).hasPath("$.value").isEqualTo("somevalue");

		assertNoPermissions(json);
	}
}