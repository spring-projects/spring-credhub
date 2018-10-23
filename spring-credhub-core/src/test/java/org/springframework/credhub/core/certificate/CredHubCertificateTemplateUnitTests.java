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

package org.springframework.credhub.core.certificate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.CertificateSummary;
import org.springframework.credhub.support.CertificateSummaryData;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.BASE_URL_PATH;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.NAME_URL_QUERY;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.REGENERATE_URL_PATH;
import static org.springframework.credhub.core.certificate.CredHubCertificateTemplate.TRANSITIONAL_REQUEST_FIELD;
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
	public void regenerate() {
		CredentialDetails<CertificateCredential> expectedCertificates =
				new CredentialDetails<>("id", NAME, CredentialType.CERTIFICATE,
						new CertificateCredential("cert", "authority", "key"));

		Map<String, Boolean> request = new HashMap<>();
		request.put(TRANSITIONAL_REQUEST_FIELD, true);

		when(restTemplate.exchange(eq(REGENERATE_URL_PATH), eq(HttpMethod.POST),
				eq(new HttpEntity<Object>(request)), isA(ParameterizedTypeReference.class), eq("id")))
				.thenReturn(new ResponseEntity<>(expectedCertificates, OK));

		CredentialDetails<CertificateCredential> response = credHubTemplate.regenerate("id", true);

		assertThat(response).isNotNull();
		assertThat(response.getId()).isEqualTo("id");
		assertThat(response.getCredentialType()).isEqualTo(CredentialType.CERTIFICATE);
		assertThat(response.getValue().getCertificate()).isEqualTo("cert");
		assertThat(response.getValue().getCertificateAuthority()).isEqualTo("authority");
		assertThat(response.getValue().getPrivateKey()).isEqualTo("key");
	}
}