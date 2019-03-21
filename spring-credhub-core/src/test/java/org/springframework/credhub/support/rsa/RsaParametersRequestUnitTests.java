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
import org.springframework.credhub.support.KeyLength;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteMode;

import static org.springframework.credhub.support.JsonPathAssert.assertThat;

@SuppressWarnings("deprecation")
public class RsaParametersRequestUnitTests extends CredHubRequestUnitTestsBase {
	@Before
	public void setUp() {
		requestBuilder = RsaParametersRequest.builder();
	}

	@Test
	public void serializeWithParameters() {
		requestBuilder = RsaParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.mode(WriteMode.OVERWRITE)
				.parameters(new RsaParameters(KeyLength.LENGTH_4096));

		DocumentContext json = toJsonPath(requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "rsa");
		assertThat(json).hasPath("$.parameters.key_length").isEqualTo(4096);
	}

	@Test
	public void serializeWithNoParameters() {
		requestBuilder = RsaParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.mode(WriteMode.OVERWRITE);

		DocumentContext json = toJsonPath(requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "rsa");
		assertThat(json).hasNoPath("$.parameters.key_length");
	}
}