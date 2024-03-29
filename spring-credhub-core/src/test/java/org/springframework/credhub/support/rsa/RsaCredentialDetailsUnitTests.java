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

package org.springframework.credhub.support.rsa;

import org.junit.jupiter.api.Test;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.JsonParsingUnitTestsBase;

import static org.assertj.core.api.Assertions.assertThat;

public class RsaCredentialDetailsUnitTests extends JsonParsingUnitTestsBase {

	private static final String RSA_CREDENTIALS = """
			"type": "rsa",
			"value": {
				"private_key": "private-key",
				"public_key": "public-key"
			}
			""";

	@Test
	public void deserializeDetailsWithPublicAndPrivateKeys() {
		CredentialDetails<RsaCredential> data = parseDetails(RSA_CREDENTIALS);

		assertDetails(data, "public-key", "private-key");
	}

	@Test
	public void deserializeDetailsWithPublicKey() {
		final String credentials = """
				"type": "rsa",
				"value": {
					"public_key": "public-key"
				}
				""";
		CredentialDetails<RsaCredential> data = parseDetails(credentials);

		assertDetails(data, "public-key", null);
	}

	@Test
	public void deserializeDetailsWithPrivateKey() {
		final String credentials = """
				"type": "rsa",
				"value": {
					"private_key": "private-key"
				}
				""";
		CredentialDetails<RsaCredential> data = parseDetails(credentials);

		assertDetails(data, null, "private-key");
	}

	@Test
	public void deserializeDetailsData() {
		CredentialDetailsData<RsaCredential> response = parseDetailsData(RSA_CREDENTIALS);

		assertThat(response.getData()).hasSize(1);

		CredentialDetails<RsaCredential> data = response.getData().get(0);

		assertDetails(data, "public-key", "private-key");
	}

	private void assertDetails(CredentialDetails<RsaCredential> data, String publicKey, String privateKey) {
		assertCommonDetails(data);

		assertThat(data.getCredentialType()).isEqualTo(CredentialType.RSA);
		assertThat(data.getValue().getPublicKey()).isEqualTo(publicKey);
		assertThat(data.getValue().getPrivateKey()).isEqualTo(privateKey);
	}

}
