/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.support.certificate;

import org.junit.Test;

import org.springframework.credhub.support.KeyLength;
import org.springframework.credhub.support.ParametersRequestUnitTestsBase;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateParametersRequest.CertificateParametersRequestBuilder;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasNoJsonPath;

public class CertificateParametersRequestUnitTests extends ParametersRequestUnitTestsBase {
	@Test
	public void serializeWithParameters() throws Exception {
		CertificateParametersRequestBuilder requestBuilder = CertificateParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.parameters(CertificateParameters.builder()
						.keyLength(KeyLength.LENGTH_2048)
						.commonName("common")
						.alternateNames("alt1", "alt2")
						.organization("org")
						.organizationUnit("dev")
						.locality("city")
						.state("state")
						.country("country")
						.duration(1234)
						.credential("credential")
						.certificateAuthority(true)
						.selfSign(false)
						.build());

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "certificate");
		assertThat(jsonValue,
				allOf(hasJsonPath("$.parameters.key_length", equalTo(2048)),
						hasJsonPath("$.parameters.common_name", equalTo("common")),
						hasJsonPath("$.parameters.alternative_names[0]", equalTo("alt1")),
						hasJsonPath("$.parameters.alternative_names[1]", equalTo("alt2")),
						hasJsonPath("$.parameters.organization", equalTo("org")),
						hasJsonPath("$.parameters.organization_unit", equalTo("dev")),
						hasJsonPath("$.parameters.locality", equalTo("city")),
						hasJsonPath("$.parameters.state", equalTo("state")),
						hasJsonPath("$.parameters.country", equalTo("country")),
						hasJsonPath("$.parameters.duration", equalTo(1234)),
						hasJsonPath("$.parameters.credential", equalTo("credential")),
						hasJsonPath("$.parameters.is_ca", equalTo(true)),
						hasJsonPath("$.parameters.self_sign", equalTo(false))));
	}

	@Test
	public void serializeWithMinimalParameters() throws Exception {
		CertificateParametersRequestBuilder requestBuilder = CertificateParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.parameters(CertificateParameters.builder()
						.commonName("common")
						.credential("credential")
						.build());

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "certificate");
		assertThat(jsonValue,
				allOf(hasNoJsonPath("$.parameters.key_length"),
						hasJsonPath("$.parameters.common_name", equalTo("common")),
						hasJsonPath("$.parameters.credential", equalTo("credential")),
						hasNoJsonPath("$.parameters.alternative_names"),
						hasNoJsonPath("$.parameters.organization"),
						hasNoJsonPath("$.parameters.organization_unit"),
						hasNoJsonPath("$.parameters.locality"),
						hasNoJsonPath("$.parameters.state"),
						hasNoJsonPath("$.parameters.country"),
						hasNoJsonPath("$.parameters.duration"),
						hasNoJsonPath("$.parameters.is_ca"),
						hasNoJsonPath("$.parameters.self_sign")));
	}

	@Test
	public void serializeWithNoParameters() throws Exception {
		CertificateParametersRequestBuilder requestBuilder = CertificateParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true);

		String jsonValue = serializeToJson(requestBuilder);

		assertCommonRequestFields(jsonValue, true, "/example/credential", "certificate");
		assertParametersNotSet(jsonValue);
	}

	@Test(expected = IllegalArgumentException.class)
	public void serializeWithEmptyParameters() throws Exception {
		CertificateParametersRequestBuilder requestBuilder = CertificateParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.parameters(CertificateParameters.builder()
						.keyLength(KeyLength.LENGTH_2048)
						.build());

		String jsonValue = serializeToJson(requestBuilder);
	}

	private void assertParametersNotSet(String jsonValue) {
		assertThat(jsonValue,
				allOf(hasNoJsonPath("$.parameters.key_length"),
						hasNoJsonPath("$.parameters.exclude_lower"),
						hasNoJsonPath("$.parameters.exclude_upper"),
						hasNoJsonPath("$.parameters.exclude_number"),
						hasNoJsonPath("$.parameters.include_special")));
	}
}