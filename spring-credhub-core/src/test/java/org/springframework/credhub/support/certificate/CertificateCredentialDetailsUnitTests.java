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

import org.junit.Test;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.JsonParsingUnitTestsBase;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class CertificateCredentialDetailsUnitTests extends JsonParsingUnitTestsBase {
	private static final String CERT_CREDENTIALS =
			"  \"type\": \"certificate\"," +
			"  \"value\": {" +
			"  \"certificate\": \"cert\"," +
			"  \"ca\": \"authority\"," +
			"  \"private_key\": \"private-key\"" +
			"  }";

	@Test
	public void deserializeDetailsWithAllValues() throws Exception {
		CredentialDetails<CertificateCredential> data = parseDetails(CERT_CREDENTIALS);

		assertDetails(data, "cert", "authority", "private-key");
	}

	@Test
	public void deserializeDetailsCertOnly() throws Exception {
		final String credentials =
				"  \"type\": \"certificate\"," +
				"  \"value\": {" +
				"  \"certificate\": \"cert\"" +
				"  }";
		CredentialDetails<CertificateCredential> data = parseDetails(credentials);

		assertDetails(data, "cert", null, null);
	}

	@Test
	public void deserializeDetailsWithNoCert() throws Exception {
		final String credentials =
				"  \"type\": \"certificate\"," +
				"  \"value\": {" +
				"  \"ca\": \"authority\"," +
				"  \"private_key\": \"private-key\"" +
				"  }";
		CredentialDetails<CertificateCredential> data = parseDetails(credentials);

		assertDetails(data, null, "authority", "private-key");
	}

	@Test
	public void deserializeDetailsData() throws Exception {
		CredentialDetailsData<CertificateCredential> response = parseDetailsData(CERT_CREDENTIALS);

		assertThat(response.getData().size(), equalTo(1));

		CredentialDetails<CertificateCredential> data = response.getData().get(0);

		assertDetails(data, "cert", "authority", "private-key");
	}

	private void assertDetails(CredentialDetails<CertificateCredential> data,
							   String certificate, String ca, String privateKey) {
		assertCommonDetails(data);
		
		assertThat(data.getCredentialType(), equalTo(CredentialType.CERTIFICATE));
		assertThat(data.getValue().getCertificate(), equalTo(certificate));
		assertThat(data.getValue().getCertificateAuthority(), equalTo(ca));
		assertThat(data.getValue().getPrivateKey(), equalTo(privateKey));
	}
}
