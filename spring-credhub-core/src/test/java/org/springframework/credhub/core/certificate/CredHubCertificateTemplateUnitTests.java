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

package org.springframework.credhub.core.certificate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.credhub.support.certificate.CertificateSummaryData;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateCredentialDetails;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.BASE_URL_PATH;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.BULK_REGENERATE_URL_PATH;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.NAME_URL_QUERY;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.REGENERATED_CREDENTIALS_RESPONSE_FIELD;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.REGENERATE_URL_PATH;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.SIGNED_BY_REQUEST_FIELD;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.TRANSITIONAL_REQUEST_FIELD;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.UPDATE_TRANSITIONAL_URL_PATH;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.VERSION_REQUEST_FIELD;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class CredHubCertificateTemplateUnitTests {
	private static final SimpleCredentialName NAME = new SimpleCredentialName("example", "certificate");

	@Mock
	private RestTemplate restTemplate;

	private CredHubCertificateOperations credHubTemplate;

	@Before
	public void setUp() {
		credHubTemplate = new CredHubTemplate(restTemplate).certificates();
	}

	@Test
	public void getAll() {
		CertificateSummaryData expectedCertificates = new CertificateSummaryData(
				new CertificateSummary("id1", "name1"),
				new CertificateSummary("id2", "name2"),
				new CertificateSummary("id3", "name3")
		);

		when(restTemplate.getForEntity(BASE_URL_PATH, CertificateSummaryData.class))
				.thenReturn(new ResponseEntity<>(expectedCertificates, OK));

		List<CertificateSummary> response = credHubTemplate.getAll();

		assertThat(response).isNotNull();
		assertThat(response.size()).isEqualTo(expectedCertificates.getCertificates().size());
		assertThat(response).isEqualTo(expectedCertificates.getCertificates());
	}

	@Test
	public void getByName() {
		CertificateSummaryData expectedCertificates = new CertificateSummaryData(
				new CertificateSummary("id1", "name1")
		);

		when(restTemplate.getForEntity(NAME_URL_QUERY, CertificateSummaryData.class, NAME.getName()))
				.thenReturn(new ResponseEntity<>(expectedCertificates, OK));

		CertificateSummary response = credHubTemplate.getByName(NAME);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo("id1");
		assertThat(response.getName()).isEqualTo("name1");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void regenerate() {
		CertificateCredentialDetails expectedCertificate =
				new CertificateCredentialDetails("id", NAME, CredentialType.CERTIFICATE, true,
						new CertificateCredential("cert", "authority", "key"));

		Map<String, Boolean> request = new HashMap<>();
		request.put(TRANSITIONAL_REQUEST_FIELD, true);

		when(restTemplate.exchange(eq(REGENERATE_URL_PATH), eq(HttpMethod.POST),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class), eq("id")))
				.thenReturn(new ResponseEntity<>(expectedCertificate, OK));

		CertificateCredentialDetails response = credHubTemplate.regenerate("id", true);

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
		Map<String, List<CredentialName>> expectedResponse =
				Collections.singletonMap(REGENERATED_CREDENTIALS_RESPONSE_FIELD,
						Arrays.<CredentialName>asList(
								new SimpleCredentialName("example-certificate1"),
								new SimpleCredentialName("example-certificate2")));

		Map<String, Object> request = new HashMap<String, Object>() {{
			put(SIGNED_BY_REQUEST_FIELD, NAME.getName());
		}};

		when(restTemplate.exchange(eq(BULK_REGENERATE_URL_PATH), eq(POST),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class)))
				.thenReturn(new ResponseEntity<>(expectedResponse, OK));

		List<CredentialName> response = credHubTemplate.regenerate(NAME);

		assertThat(response).isNotNull();
		assertThat(response).isEqualTo(expectedResponse.get(REGENERATED_CREDENTIALS_RESPONSE_FIELD));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void updateTransitionalVersion() {
		List<CertificateCredentialDetails> expectedCertificates = Arrays.asList(
				new CertificateCredentialDetails("id1", NAME, CredentialType.CERTIFICATE, false,
						new CertificateCredential("cert1", "authority1", "key1")),
				new CertificateCredentialDetails("id2", NAME, CredentialType.CERTIFICATE, true,
						new CertificateCredential("cert2", "authority2", "key2"))
				);

		Map<String, String> request = new HashMap<>();
		request.put(VERSION_REQUEST_FIELD, "id2");

		when(restTemplate.exchange(eq(UPDATE_TRANSITIONAL_URL_PATH), eq(HttpMethod.PUT),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class), eq("id1")))
				.thenReturn(new ResponseEntity<>(expectedCertificates, OK));

		List<CertificateCredentialDetails> response =
				credHubTemplate.updateTransitionalVersion("id1", "id2");

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