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

package org.springframework.credhub.support.certificate;

import com.jayway.jsonpath.DocumentContext;
import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredHubRequestUnitTestsBase;
import org.springframework.credhub.support.JsonPathAssert;
import org.springframework.credhub.support.KeyLength;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteMode;

@SuppressWarnings("deprecation")
public class CertificateParametersRequestUnitTests extends CredHubRequestUnitTestsBase {

	@Before
	public void setUp() {
		this.requestBuilder = CertificateParametersRequest.builder();
	}

	@Test
	public void serializeWithParameters() {
		this.requestBuilder = CertificateParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential")).overwrite(true).mode(WriteMode.OVERWRITE)
				.parameters(CertificateParameters.builder().keyLength(KeyLength.LENGTH_2048).commonName("common")
						.alternateNames("alt1", "alt2").organization("org").organizationUnit("dev").locality("city")
						.state("state").country("country").duration(1234).certificateAuthorityCredential("credential")
						.certificateAuthority(true).selfSign(false)
						.keyUsage(KeyUsage.CRL_SIGN, KeyUsage.DATA_ENCIPHERMENT, KeyUsage.DECIPHER_ONLY,
								KeyUsage.DIGITAL_SIGNATURE, KeyUsage.ENCIPHER_ONLY, KeyUsage.KEY_AGREEMENT,
								KeyUsage.KEY_CERT_SIGN, KeyUsage.KEY_ENCIPHERMENT, KeyUsage.NON_REPUDIATION)
						.extendedKeyUsage(ExtendedKeyUsage.CLIENT_AUTH, ExtendedKeyUsage.CODE_SIGNING,
								ExtendedKeyUsage.EMAIL_PROTECTION, ExtendedKeyUsage.SERVER_AUTH,
								ExtendedKeyUsage.TIMESTAMPING)
						.build());

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.OVERWRITE, "/example/credential", "certificate");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_length").isEqualTo(2048);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.common_name").isEqualTo("common");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.alternative_names[0]").isEqualTo("alt1");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.alternative_names[1]").isEqualTo("alt2");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.organization").isEqualTo("org");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.organization_unit").isEqualTo("dev");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.locality").isEqualTo("city");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.state").isEqualTo("state");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.country").isEqualTo("country");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.duration").isEqualTo(1234);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.ca").isEqualTo("credential");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.is_ca").isEqualTo(true);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.self_sign").isEqualTo(false);
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_usage[0]").isEqualTo(KeyUsage.CRL_SIGN.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_usage[1]")
				.isEqualTo(KeyUsage.DATA_ENCIPHERMENT.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_usage[2]")
				.isEqualTo(KeyUsage.DECIPHER_ONLY.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_usage[3]")
				.isEqualTo(KeyUsage.DIGITAL_SIGNATURE.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_usage[4]")
				.isEqualTo(KeyUsage.ENCIPHER_ONLY.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_usage[5]")
				.isEqualTo(KeyUsage.KEY_AGREEMENT.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_usage[6]")
				.isEqualTo(KeyUsage.KEY_CERT_SIGN.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_usage[7]")
				.isEqualTo(KeyUsage.KEY_ENCIPHERMENT.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.key_usage[8]")
				.isEqualTo(KeyUsage.NON_REPUDIATION.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.extended_key_usage[0]")
				.isEqualTo(ExtendedKeyUsage.CLIENT_AUTH.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.extended_key_usage[1]")
				.isEqualTo(ExtendedKeyUsage.CODE_SIGNING.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.extended_key_usage[2]")
				.isEqualTo(ExtendedKeyUsage.EMAIL_PROTECTION.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.extended_key_usage[3]")
				.isEqualTo(ExtendedKeyUsage.SERVER_AUTH.getValue());
		JsonPathAssert.assertThat(json).hasPath("$.parameters.extended_key_usage[4]")
				.isEqualTo(ExtendedKeyUsage.TIMESTAMPING.getValue());
	}

	@Test
	public void serializeWithMinimalParameters() {
		this.requestBuilder = CertificateParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential")).overwrite(true).mode(WriteMode.NO_OVERWRITE)
				.parameters(CertificateParameters.builder().commonName("common")
						.certificateAuthorityCredential("credential").build());

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.NO_OVERWRITE, "/example/credential", "certificate");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.key_length");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.common_name").isEqualTo("common");
		JsonPathAssert.assertThat(json).hasPath("$.parameters.ca").isEqualTo("credential");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.alternative_names");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.organization");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.organization_unit");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.locality");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.state");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.country");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.duration");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.is_ca");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.self_sign");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.key_usage");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.extended_key_usage");
	}

	@Test
	public void serializeWithNoParameters() {
		this.requestBuilder = CertificateParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential")).overwrite(true).mode(WriteMode.CONVERGE);

		DocumentContext json = toJsonPath(this.requestBuilder);

		assertCommonRequestFields(json, true, WriteMode.CONVERGE, "/example/credential", "certificate");
		assertParametersNotSet(json);
	}

	@Test(expected = IllegalArgumentException.class)
	public void serializeWithEmptyParameters() {
		this.requestBuilder = CertificateParametersRequest.builder()
				.name(new SimpleCredentialName("example", "credential")).overwrite(true)
				.parameters(CertificateParameters.builder().keyLength(KeyLength.LENGTH_2048).build());

		toJsonPath(this.requestBuilder);
	}

	private void assertParametersNotSet(DocumentContext json) {
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.key_length");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.exclude_lower");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.exclude_upper");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.exclude_number");
		JsonPathAssert.assertThat(json).hasNoPath("$.parameters.include_special");
	}

}
