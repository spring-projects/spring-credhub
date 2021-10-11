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

package org.springframework.credhub.core.certificate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateCredentialDetails;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.credhub.support.certificate.CertificateSummaryData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CredHubCertificateTemplateUnitTests {

	private static final SimpleCredentialName NAME = new SimpleCredentialName("example", "certificate");

	@Mock
	private RestTemplate restTemplate;

	private CredHubCertificateOperations credHubTemplate;

	@BeforeEach
	public void setUp() {
		this.credHubTemplate = new CredHubTemplate(this.restTemplate).certificates();
	}

	@Test
	public void getAll() {
		CertificateSummaryData expectedCertificates = new CertificateSummaryData(new CertificateSummary("id1", "name1"),
				new CertificateSummary("id2", "name2"), new CertificateSummary("id3", "name3"));

		given(this.restTemplate.getForEntity(CredHubCertificateTemplate.BASE_URL_PATH, CertificateSummaryData.class))
				.willReturn(new ResponseEntity<>(expectedCertificates, HttpStatus.OK));

		List<CertificateSummary> response = this.credHubTemplate.getAll();

		assertThat(response).isNotNull();
		assertThat(response.size()).isEqualTo(expectedCertificates.getCertificates().size());
		assertThat(response).isEqualTo(expectedCertificates.getCertificates());
	}

	@Test
	public void getByName() {
		CertificateSummaryData expectedCertificates = new CertificateSummaryData(
				new CertificateSummary("id1", "name1"));

		given(this.restTemplate.getForEntity(CredHubCertificateTemplate.NAME_URL_QUERY, CertificateSummaryData.class,
				NAME.getName())).willReturn(new ResponseEntity<>(expectedCertificates, HttpStatus.OK));

		CertificateSummary response = this.credHubTemplate.getByName(NAME);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo("id1");
		assertThat(response.getName()).isEqualTo("name1");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void regenerate() {
		CertificateCredentialDetails expectedCertificate = new CertificateCredentialDetails("id", NAME,
				CredentialType.CERTIFICATE, true, new CertificateCredential("cert", "authority", "key"));

		Map<String, Boolean> request = new HashMap<>();
		request.put(CredHubCertificateTemplate.TRANSITIONAL_REQUEST_FIELD, true);

		given(this.restTemplate.exchange(eq(CredHubCertificateTemplate.REGENERATE_URL_PATH), eq(HttpMethod.POST),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class), eq("id")))
						.willReturn(new ResponseEntity<>(expectedCertificate, HttpStatus.OK));

		CertificateCredentialDetails response = this.credHubTemplate.regenerate("id", true);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo("id");
		assertThat(response.getCredentialType()).isEqualTo(CredentialType.CERTIFICATE);
		assertThat(response.isTransitional()).isTrue();
		assertThat(response.getValue().getCertificate()).isEqualTo("cert");
		assertThat(response.getValue().getCertificateAuthority()).isEqualTo("authority");
		assertThat(response.getValue().getPrivateKey()).isEqualTo("key");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void bulkRegenerate() {
		Map<String, List<CredentialName>> expectedResponse = Collections.singletonMap(
				CredHubCertificateTemplate.REGENERATED_CREDENTIALS_RESPONSE_FIELD,
				Arrays.asList(new SimpleCredentialName("example-certificate1"),
						new SimpleCredentialName("example-certificate2")));

		Map<String, Object> request = new HashMap<String, Object>() {
			{
				put(CredHubCertificateTemplate.SIGNED_BY_REQUEST_FIELD, NAME.getName());
			}
		};

		given(this.restTemplate.exchange(eq(CredHubCertificateTemplate.BULK_REGENERATE_URL_PATH), eq(HttpMethod.POST),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class)))
						.willReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

		List<CredentialName> response = this.credHubTemplate.regenerate(NAME);

		assertThat(response).isNotNull();
		assertThat(response)
				.isEqualTo(expectedResponse.get(CredHubCertificateTemplate.REGENERATED_CREDENTIALS_RESPONSE_FIELD));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void updateTransitionalVersion() {
		List<CertificateCredentialDetails> expectedCertificates = Arrays.asList(
				new CertificateCredentialDetails("id1", NAME, CredentialType.CERTIFICATE, false,
						new CertificateCredential("cert1", "authority1", "key1")),
				new CertificateCredentialDetails("id2", NAME, CredentialType.CERTIFICATE, true,
						new CertificateCredential("cert2", "authority2", "key2")));

		Map<String, String> request = new HashMap<>();
		request.put(CredHubCertificateTemplate.VERSION_REQUEST_FIELD, "id2");

		given(this.restTemplate.exchange(eq(CredHubCertificateTemplate.UPDATE_TRANSITIONAL_URL_PATH),
				eq(HttpMethod.PUT), eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class), eq("id1")))
						.willReturn(new ResponseEntity<>(expectedCertificates, HttpStatus.OK));

		List<CertificateCredentialDetails> response = this.credHubTemplate.updateTransitionalVersion("id1", "id2");

		assertThat(response).hasSize(2);
		assertThat(response).extracting("id").contains("id1", "id2");
		assertThat(response).extracting("credentialType").contains(CredentialType.CERTIFICATE,
				CredentialType.CERTIFICATE);
		assertThat(response).extracting("transitional").contains(false, true);
		assertThat(response).extracting("value.certificate").contains("cert1", "cert2");
		assertThat(response).extracting("value.certificateAuthority").contains("authority1", "authority2");
		assertThat(response).extracting("value.privateKey").contains("key1", "key2");
	}

}
