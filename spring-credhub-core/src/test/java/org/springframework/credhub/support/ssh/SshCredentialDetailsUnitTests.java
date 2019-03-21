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

package org.springframework.credhub.support.ssh;

import org.junit.Test;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.JsonParsingUnitTestsBase;

import static org.assertj.core.api.Assertions.assertThat;

public class SshCredentialDetailsUnitTests extends JsonParsingUnitTestsBase {
	private static final String SSH_CREDENTIALS =
			"  \"type\": \"ssh\"," +
			"  \"value\": {" +
			"  \"private_key\": \"private-key\"," +
			"  \"public_key\": \"public-key\"," +
			"  \"public_key_fingerprint\": \"fingerprint\"" +
			"  }";

	@Test
	public void deserializeDetailsWithPublicAndPrivateKeys() {
		CredentialDetails<SshCredential> data = parseDetails(SSH_CREDENTIALS);

		assertDetails(data, "public-key", "private-key", "fingerprint");
	}

	@Test
	public void deserializeDetailsWithPublicKey() {
		final String credentials =
				"  \"type\": \"ssh\"," +
						"  \"value\": {" +
						"  \"public_key\": \"public-key\"" +
						"  }";
		CredentialDetails<SshCredential> data = parseDetails(credentials);

		assertDetails(data, "public-key", null, null);
	}

	@Test
	public void deserializeDetailsWithPrivateKey() {
		final String credentials =
				"  \"type\": \"ssh\"," +
						"  \"value\": {" +
						"  \"private_key\": \"private-key\"" +
						"  }";
		CredentialDetails<SshCredential> data = parseDetails(credentials);

		assertDetails(data, null, "private-key", null);
	}

	@Test
	public void deserializeDetailsData() {
		CredentialDetailsData<SshCredential> response = parseDetailsData(SSH_CREDENTIALS);

		assertThat(response.getData()).hasSize(1);

		CredentialDetails<SshCredential> data = response.getData().get(0);

		assertDetails(data, "public-key", "private-key", "fingerprint");
	}

	private void assertDetails(CredentialDetails<SshCredential> data,
							   String publicKey, String privateKey, String publicKeyFingerprint) {
		assertCommonDetails(data);
		
		assertThat(data.getCredentialType()).isEqualTo(CredentialType.SSH);
		assertThat(data.getValue().getPublicKey()).isEqualTo(publicKey);
		assertThat(data.getValue().getPrivateKey()).isEqualTo(privateKey);
		assertThat(data.getValue().getPublicKeyFingerprint()).isEqualTo(publicKeyFingerprint);
	}
}
