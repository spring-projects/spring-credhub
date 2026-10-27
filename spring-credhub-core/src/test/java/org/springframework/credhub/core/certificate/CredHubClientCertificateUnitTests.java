/*
 * Copyright 2016-2026 the original author or authors.
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

package org.springframework.credhub.core.certificate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.credhub.core.CredHubClient;
import org.springframework.credhub.core.CredHubClientUnitTestsBase;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateCredentialDetails;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.credhub.support.certificate.CertificateSummaryData;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

/**
 * Unit tests of the certificate operations of {@link CredHubClient}, converted from the
 * tests for {@code CredHubCertificateTemplate}.
 */
class CredHubClientCertificateUnitTests extends CredHubClientUnitTestsBase {

	private static final SimpleCredentialName NAME = new SimpleCredentialName("example", "certificate");

	private CredHubCertificateOperations certificates;

	@BeforeEach
	void setUpCertificates() {
		this.certificates = this.client.certificates();
	}

	@Test
	void getAll() {
		CertificateSummaryData expectedCertificates = new CertificateSummaryData(new CertificateSummary("id1", "name1"),
				new CertificateSummary("id2", "name2"), new CertificateSummary("id3", "name3"));

		this.server.expect(requestTo(BASE_URL + "/api/v1/certificates"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(respondWith(expectedCertificates));

		List<CertificateSummary> response = this.certificates.getAll();

		assertThat(response).isNotNull();
		assertThat(response).hasSameSizeAs(expectedCertificates.getCertificates());
		assertThat(response).usingRecursiveComparison().isEqualTo(expectedCertificates.getCertificates());
	}

	@Test
	void getByName() {
		CertificateSummaryData expectedCertificates = new CertificateSummaryData(
				new CertificateSummary("id1", "name1"));

		this.server.expect(requestTo(BASE_URL + "/api/v1/certificates?name=%2Fexample%2Fcertificate"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(respondWith(expectedCertificates));

		CertificateSummary response = this.certificates.getByName(NAME);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo("id1");
		assertThat(response.getName()).isEqualTo("name1");
	}

	@Test
	void regenerate() {
		CertificateCredentialDetails expectedCertificate = new CertificateCredentialDetails("id", NAME,
				CredentialType.CERTIFICATE, true, new CertificateCredential("cert", "authority", "key"));

		Map<String, Boolean> request = new HashMap<>();
		request.put("set_as_transitional", true);

		this.server.expect(requestTo(BASE_URL + "/api/v1/certificates/id/regenerate"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(request))
			.andRespond(respondWith(expectedCertificate));

		CertificateCredentialDetails response = this.certificates.regenerate("id", true);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo("id");
		assertThat(response.getCredentialType()).isEqualTo(CredentialType.CERTIFICATE);
		assertThat(response.isTransitional()).isTrue();
		assertThat(response.getValue().getCertificate()).isEqualTo("cert");
		assertThat(response.getValue().getCertificateAuthority()).isEqualTo("authority");
		assertThat(response.getValue().getPrivateKey()).isEqualTo("key");
	}

	@Test
	void regenerateWithAllParameters() {
		CertificateCredentialDetails expectedCertificate = new CertificateCredentialDetails("id", NAME,
				CredentialType.CERTIFICATE, true, new CertificateCredential("cert", "authority", "key"));

		Map<String, Object> metadata = Map.of("key", "metadata-value");

		Map<String, Object> request = new HashMap<>();
		request.put("set_as_transitional", true);
		request.put("allow_transitional_parent_to_sign", true);
		request.put("key_length", 2048);
		request.put("duration", 365);
		request.put("metadata", metadata);

		this.server.expect(requestTo(BASE_URL + "/api/v1/certificates/id/regenerate"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(request))
			.andRespond(respondWith(expectedCertificate));

		CertificateCredentialDetails response = this.certificates.regenerate("id", true, true, 2048, 365, metadata);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo("id");
		assertThat(response.isTransitional()).isTrue();
	}

	@Test
	void bulkRegenerate() {
		Map<String, List<CredentialName>> expectedResponse = Collections.singletonMap("regenerated_credentials",
				Arrays.asList(new SimpleCredentialName("example-certificate1"),
						new SimpleCredentialName("example-certificate2")));

		Map<String, Object> request = new HashMap<>();
		request.put("signed_by", NAME.getName());

		this.server.expect(requestTo(BASE_URL + "/api/v1/bulk-regenerate"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(request))
			.andRespond(respondWith(expectedResponse));

		List<CredentialName> response = this.certificates.regenerate(NAME);

		assertThat(response).isNotNull();
		assertThat(response).extracting(CredentialName::getName)
			.containsExactly("example-certificate1", "example-certificate2");
	}

	@Test
	void getVersions() {
		List<CertificateCredentialDetails> expectedCertificates = Arrays.asList(
				new CertificateCredentialDetails("id1", NAME, CredentialType.CERTIFICATE, false,
						new CertificateCredential("cert1", "authority1", "key1")),
				new CertificateCredentialDetails("id2", NAME, CredentialType.CERTIFICATE, true,
						new CertificateCredential("cert2", "authority2", "key2")));

		this.server.expect(requestTo(BASE_URL + "/api/v1/certificates/id1/versions"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(respondWith(expectedCertificates));

		List<CertificateCredentialDetails> response = this.certificates.getVersions("id1");

		assertThat(response).hasSize(2);
		assertThat(response).extracting("id").contains("id1", "id2");
		assertThat(response).extracting("transitional").contains(false, true);
		assertThat(response).extracting("value.certificate").contains("cert1", "cert2");
	}

	@Test
	void addVersion() {
		CertificateCredential value = new CertificateCredential("cert1", "authority1", "key1");
		CertificateCredentialDetails expectedCertificate = new CertificateCredentialDetails("id1", NAME,
				CredentialType.CERTIFICATE, true, value);

		Map<String, Object> request = new HashMap<>();
		request.put("value", value);
		request.put("transitional", true);

		this.server.expect(requestTo(BASE_URL + "/api/v1/certificates/id1/versions"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(request))
			.andRespond(respondWith(expectedCertificate));

		CertificateCredentialDetails response = this.certificates.addVersion("id1", value, true);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo("id1");
		assertThat(response.isTransitional()).isTrue();
		assertThat(response.getValue().getCertificate()).isEqualTo("cert1");
	}

	@Test
	void deleteVersion() {
		CertificateCredentialDetails expectedCertificate = new CertificateCredentialDetails("id2", NAME,
				CredentialType.CERTIFICATE, false, new CertificateCredential("cert2", "authority2", "key2"));

		this.server.expect(requestTo(BASE_URL + "/api/v1/certificates/id1/versions/id2"))
			.andExpect(method(HttpMethod.DELETE))
			.andRespond(respondWith(expectedCertificate));

		CertificateCredentialDetails response = this.certificates.deleteVersion("id1", "id2");

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo("id2");
		assertThat(response.getValue().getCertificate()).isEqualTo("cert2");
	}

	@Test
	void updateTransitionalVersion() {
		List<CertificateCredentialDetails> expectedCertificates = Arrays.asList(
				new CertificateCredentialDetails("id1", NAME, CredentialType.CERTIFICATE, false,
						new CertificateCredential("cert1", "authority1", "key1")),
				new CertificateCredentialDetails("id2", NAME, CredentialType.CERTIFICATE, true,
						new CertificateCredential("cert2", "authority2", "key2")));

		Map<String, String> request = new HashMap<>();
		request.put("version", "id2");

		this.server.expect(requestTo(BASE_URL + "/api/v1/certificates/id1/update_transitional_version"))
			.andExpect(method(HttpMethod.PUT))
			.andExpect(jsonBody(request))
			.andRespond(respondWith(expectedCertificates));

		List<CertificateCredentialDetails> response = this.certificates.updateTransitionalVersion("id1", "id2");

		assertThat(response).hasSize(2);
		assertThat(response).extracting("id").contains("id1", "id2");
		assertThat(response).extracting("credentialType")
			.contains(CredentialType.CERTIFICATE, CredentialType.CERTIFICATE);
		assertThat(response).extracting("transitional").contains(false, true);
		assertThat(response).extracting("value.certificate").contains("cert1", "cert2");
		assertThat(response).extracting("value.certificateAuthority").contains("authority1", "authority2");
		assertThat(response).extracting("value.privateKey").contains("key1", "key2");
	}

}
