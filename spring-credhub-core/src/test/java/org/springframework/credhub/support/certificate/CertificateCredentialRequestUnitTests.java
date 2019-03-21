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

package org.springframework.credhub.support.certificate;

import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.SimpleCredentialName;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasNoJsonPath;

public class CertificateCredentialRequestUnitTests extends CredHubRequestUnitTestsBase {
	@Before
	public void setUp() {
		buildRequest(new CertificateCredential("cert", "ca", "private-key"));
	}

	@Test
	public void serializeWithAllValues() throws Exception {
		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "certificate");
		assertThat(jsonValue,
				allOf(hasJsonPath("$.value.certificate", equalTo("cert")),
						hasJsonPath("$.value.ca", equalTo("ca")),
						hasJsonPath("$.value.private_key", equalTo("private-key"))));

		assertNoPermissions(jsonValue);
	}

	@Test
	public void serializeWithCertOnly() throws Exception {
		buildRequest(new CertificateCredential("cert", null, null));

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "certificate");
		assertThat(jsonValue,
				allOf(hasJsonPath("$.value.certificate", equalTo("cert")),
						hasNoJsonPath("$.value.ca"),
						hasNoJsonPath("$.value.private_key")));

		assertNoPermissions(jsonValue);
	}

	@Test
	public void serializeWithNoCert() throws Exception {
		buildRequest(new CertificateCredential(null, "ca", "private-key"));

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "certificate");
		assertThat(jsonValue,
				allOf(hasNoJsonPath("$.value.certificate"),
						hasJsonPath("$.value.ca", equalTo("ca")),
						hasJsonPath("$.value.private_key", equalTo("private-key"))));

		assertNoPermissions(jsonValue);
	}

	@Test(expected = IllegalArgumentException.class)
	public void serializeWithNoValues() throws Exception {
		buildRequest(new CertificateCredential(null, null, null));

		serializeToJson(requestBuilder);
	}

	private void buildRequest(CertificateCredential value) {
		requestBuilder = CertificateCredentialRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.value(value);
	}

}