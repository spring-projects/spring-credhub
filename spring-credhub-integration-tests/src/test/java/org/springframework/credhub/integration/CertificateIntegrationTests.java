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

package org.springframework.credhub.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.credhub.core.certificate.CredHubCertificateOperations;
import org.springframework.credhub.core.credential.CredHubCredentialOperations;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateCredentialDetails;
import org.springframework.credhub.support.certificate.CertificateParameters;
import org.springframework.credhub.support.certificate.CertificateParametersRequest;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

public class CertificateIntegrationTests extends CredHubIntegrationTests {
	private static final SimpleCredentialName TEST_CERT_NAME =
			new SimpleCredentialName("spring-credhub", "integration-test", "test-certificate");
	private static final SimpleCredentialName ROOT_CERT_NAME =
			new SimpleCredentialName("spring-credhub", "integration-test", "root-certificate");

	private CredHubCredentialOperations credentials;
	private CredHubCertificateOperations certificates;

	@Before
	public void setUp() {
		credentials = operations.credentials();
		certificates = operations.certificates();

		deleteCredentialIfExists(TEST_CERT_NAME);
		deleteCredentialIfExists(ROOT_CERT_NAME);
	}

	@After
	public void tearDown() {
		deleteCredentialIfExists(TEST_CERT_NAME);
		deleteCredentialIfExists(ROOT_CERT_NAME);
	}

	@Test
	public void generateCertificate() {
		assumeTrue(serverApiIsV2());

		CredentialDetails<CertificateCredential> certificate =
				credentials.generate(CertificateParametersRequest.builder()
						.name(TEST_CERT_NAME)
						.parameters(CertificateParameters.builder()
								.commonName("example.com")
								.selfSign(true)
								.build())
						.build());
		assertThat(certificate.getName().getName()).isEqualTo(TEST_CERT_NAME.getName());
		assertThat(certificate.getCredentialType()).isEqualTo(CredentialType.CERTIFICATE);
		assertThat(certificate.getId()).isNotNull();
		assertThat(certificate.getValue().getCertificate()).isNotNull();
		assertThat(certificate.getValue().getCertificateAuthority()).isNotNull();
		assertThat(certificate.getValue().getPrivateKey()).isNotNull();

		CertificateSummary byName = certificates.getByName(TEST_CERT_NAME);
		assertThat(byName.getName()).isEqualTo(TEST_CERT_NAME.getName());
		assertThat(byName.getId()).isNotNull();

		List<CertificateSummary> allCertificates = certificates.getAll();
		assertThat(allCertificates.size()).isGreaterThan(0);
		assertThat(allCertificates).extracting("name").contains(TEST_CERT_NAME.getName());
	}

	@Test
	public void regenerateCertificate() {
		assumeTrue(serverApiIsV2());

		CredentialDetails<CertificateCredential> certificate =
				credentials.generate(CertificateParametersRequest.builder()
						.name(TEST_CERT_NAME)
						.parameters(CertificateParameters.builder()
								.commonName("example.com")
								.selfSign(true)
								.build())
						.build());
		assertThat(certificate.getName().getName()).isEqualTo(TEST_CERT_NAME.getName());

		CertificateSummary byName = certificates.getByName(TEST_CERT_NAME);

		CertificateCredentialDetails regenerated = certificates.regenerate(byName.getId(), true);
		assertThat(regenerated.getName().getName()).isEqualTo(TEST_CERT_NAME.getName());
		assertThat(regenerated.isTransitional()).isTrue();
		assertThat(regenerated.getValue().getCertificate())
				.isNotEqualTo(certificate.getValue().getCertificate());
		assertThat(regenerated.getValue().getCertificateAuthority())
				.isNotEqualTo(certificate.getValue().getCertificateAuthority());
		assertThat(regenerated.getValue().getPrivateKey())
				.isNotEqualTo(certificate.getValue().getPrivateKey());
	}

	@Test
	public void rotateCertificate() {
		assumeTrue(serverApiIsV2());

		CredentialDetails<CertificateCredential> certificate =
				credentials.generate(CertificateParametersRequest.builder()
						.name(TEST_CERT_NAME)
						.parameters(CertificateParameters.builder()
								.commonName("example.com")
								.selfSign(true)
								.build())
						.build());
		assertThat(certificate.getName().getName()).isEqualTo(TEST_CERT_NAME.getName());

		String credentialVersion0Id = certificate.getId();

		List<CredentialDetails<CertificateCredential>> allVersions =
				credentials.getByNameWithHistory(TEST_CERT_NAME, CertificateCredential.class);
		assertThat(allVersions).hasSize(1);
		assertThat(allVersions.get(0).getId()).isEqualTo(credentialVersion0Id);

		CertificateSummary byName = certificates.getByName(TEST_CERT_NAME);
		String certificateId = byName.getId();

		CertificateCredentialDetails regenerated = certificates.regenerate(certificateId, true);
		assertThat(regenerated.getName().getName()).isEqualTo(TEST_CERT_NAME.getName());
		assertThat(regenerated.getValue().getCertificate())
				.isNotEqualTo(certificate.getValue().getCertificate());
		assertThat(regenerated.getValue().getCertificateAuthority())
				.isNotEqualTo(certificate.getValue().getCertificateAuthority());
		assertThat(regenerated.getValue().getPrivateKey())
				.isNotEqualTo(certificate.getValue().getPrivateKey());

		String credentialVersion1Id = regenerated.getId();

		allVersions = credentials.getByNameWithHistory(TEST_CERT_NAME, CertificateCredential.class);
		assertThat(allVersions).hasSize(2);
		assertThat(allVersions).extracting("id").contains(credentialVersion1Id, credentialVersion0Id);

		List<CertificateCredentialDetails> updatedCertificate =
				certificates.updateTransitionalVersion(certificateId, credentialVersion0Id);
		assertThat(updatedCertificate).hasSize(2);
		assertThat(updatedCertificate).extracting("id").contains(credentialVersion1Id, credentialVersion0Id);
		assertThat(updatedCertificate).extracting("transitional").contains(false, true);

		updatedCertificate = certificates.updateTransitionalVersion(certificateId, null);
		assertThat(updatedCertificate).hasSize(1);
		assertThat(updatedCertificate).extracting("id").contains(credentialVersion1Id);
		assertThat(updatedCertificate).extracting("transitional").contains(false);
	}

	@Test
	public void bulkRegenerateCertificates() {
		CredentialDetails<CertificateCredential> rootCertificate =
				credentials.generate(CertificateParametersRequest.builder()
						.name(ROOT_CERT_NAME)
						.parameters(CertificateParameters.builder()
								.commonName("example.com")
								.certificateAuthority(true)
								.selfSign(true)
								.build())
						.build());

		CredentialDetails<CertificateCredential> signedCertificate =
				credentials.generate(CertificateParametersRequest.builder()
						.name(TEST_CERT_NAME)
						.parameters(CertificateParameters.builder()
								.commonName("example.com")
								.certificateAuthorityCredential(ROOT_CERT_NAME)
								.build())
						.build());
		assertThat(signedCertificate.getValue().getCertificateAuthority())
				.isEqualTo(rootCertificate.getValue().getCertificate());

		List<CredentialDetails<CertificateCredential>> allVersions =
				credentials.getByNameWithHistory(TEST_CERT_NAME, CertificateCredential.class);
		assertThat(allVersions).hasSize(1);
		assertThat(allVersions.get(0).getId()).isEqualTo(signedCertificate.getId());

		List<CredentialName> regeneratedNames = certificates.regenerate(ROOT_CERT_NAME);
		assertThat(regeneratedNames).hasSize(1);
		assertThat(regeneratedNames).contains(TEST_CERT_NAME);
	}
}
