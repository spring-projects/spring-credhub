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
import org.springframework.credhub.core.certificate.ReactiveCredHubCertificateOperations;
import org.springframework.credhub.core.credential.ReactiveCredHubCredentialOperations;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateParameters;
import org.springframework.credhub.support.certificate.CertificateParametersRequest;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

public class ReactiveCertificateIntegrationTests extends ReactiveCredHubIntegrationTests {
	private static final SimpleCredentialName TEST_CERT_NAME =
			new SimpleCredentialName("spring-credhub", "integration-test", "test-certificate");
	private static final SimpleCredentialName ROOT_CERT_NAME =
			new SimpleCredentialName("spring-credhub", "integration-test", "root-certificate");

	private ReactiveCredHubCredentialOperations credentials;
	private ReactiveCredHubCertificateOperations certificates;

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

		StepVerifier.create(credentials.generate(CertificateParametersRequest.builder()
						.name(TEST_CERT_NAME)
						.parameters(CertificateParameters.builder()
								.commonName("example.com")
								.selfSign(true)
								.build())
						.build(),
				CertificateCredential.class))
				.assertNext(response -> {
					assertThat(response.getName().getName()).isEqualTo(TEST_CERT_NAME.getName());
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.CERTIFICATE);
					assertThat(response.getId()).isNotNull();
					assertThat(response.getValue().getCertificate()).isNotNull();
					assertThat(response.getValue().getCertificateAuthority()).isNotNull();
					assertThat(response.getValue().getPrivateKey()).isNotNull();
				})
				.verifyComplete();

		StepVerifier.create(certificates.getByName(TEST_CERT_NAME))
				.assertNext(response -> {
					assertThat(response.getName()).isEqualTo(TEST_CERT_NAME.getName());
					assertThat(response.getId()).isNotNull();
				})
				.verifyComplete();

		StepVerifier.create(certificates.getAll())
				.assertNext(response -> assertThat(response.getName()).isEqualTo(TEST_CERT_NAME.getName()))
				.verifyComplete();
	}

	@Test
	public void regenerateCertificate() {
		assumeTrue(serverApiIsV2());

		AtomicReference<CertificateCredential> certificate = new AtomicReference<>();
		AtomicReference<String> certificateId = new AtomicReference<>();

		StepVerifier.create(credentials.generate(CertificateParametersRequest.builder()
						.name(TEST_CERT_NAME)
						.parameters(CertificateParameters.builder()
								.commonName("example.com")
								.selfSign(true)
								.build())
						.build(),
				CertificateCredential.class))
				.assertNext(response -> {
					assertThat(response.getName().getName()).isEqualTo(TEST_CERT_NAME.getName());

					certificate.set(response.getValue());
				})
				.verifyComplete();

		StepVerifier.create(certificates.getByName(TEST_CERT_NAME))
				.assertNext(response -> {
					assertThat(response.getName()).isEqualTo(TEST_CERT_NAME.getName());
					assertThat(response.getId()).isNotNull();

					certificateId.set(response.getId());
				})
				.verifyComplete();

		StepVerifier.create(certificates.regenerate(certificateId.get(), true))
				.assertNext(response -> {
					assertThat(response.getName().getName()).isEqualTo(TEST_CERT_NAME.getName());
					assertThat(response.isTransitional()).isTrue();
					assertThat(response.getValue().getCertificate())
							.isNotEqualTo(certificate.get().getCertificate());
					assertThat(response.getValue().getCertificateAuthority())
							.isNotEqualTo(certificate.get().getCertificateAuthority());
					assertThat(response.getValue().getPrivateKey())
							.isNotEqualTo(certificate.get().getPrivateKey());
				})
				.verifyComplete();
	}

	@Test
	public void rotateCertificate() {
		assumeTrue(serverApiIsV2());

		AtomicReference<CertificateCredential> certificate = new AtomicReference<>();
		AtomicReference<String> credentialVersion0Id = new AtomicReference<>();
		AtomicReference<String> credentialVersion1Id = new AtomicReference<>();
		AtomicReference<String> certificateId = new AtomicReference<>();

		StepVerifier.create(credentials.generate(CertificateParametersRequest.builder()
				.name(TEST_CERT_NAME)
				.parameters(CertificateParameters.builder()
						.commonName("example.com")
						.selfSign(true)
						.build())
				.build(), CertificateCredential.class))
				.assertNext(response -> {
					assertThat(response.getName().getName()).isEqualTo(TEST_CERT_NAME.getName());

					certificate.set(response.getValue());
					credentialVersion0Id.set(response.getId());
				})
				.verifyComplete();

		StepVerifier.create(credentials.getByNameWithHistory(TEST_CERT_NAME, CertificateCredential.class))
				.assertNext(response -> assertThat(response.getId()).isEqualTo(credentialVersion0Id.get()))
				.verifyComplete();

		StepVerifier.create(certificates.getByName(TEST_CERT_NAME))
				.assertNext(response -> {
					assertThat(response.getName()).isEqualTo(TEST_CERT_NAME.getName());
					assertThat(response.getId()).isNotNull();

					certificateId.set(response.getId());
				})
				.verifyComplete();

		StepVerifier.create(certificates.regenerate(certificateId.get(), true))
				.assertNext(response -> {
					assertThat(response.getName().getName()).isEqualTo(TEST_CERT_NAME.getName());
					assertThat(response.getValue().getCertificate())
							.isNotEqualTo(certificate.get().getCertificate());
					assertThat(response.getValue().getCertificateAuthority())
							.isNotEqualTo(certificate.get().getCertificateAuthority());
					assertThat(response.getValue().getPrivateKey())
							.isNotEqualTo(certificate.get().getPrivateKey());

					credentialVersion1Id.set(response.getId());
				})
				.verifyComplete();

		StepVerifier.create(credentials.getByNameWithHistory(TEST_CERT_NAME, CertificateCredential.class))
				.assertNext(response -> assertThat(response.getId()).isEqualTo(credentialVersion1Id.get()))
				.assertNext(response -> assertThat(response.getId()).isEqualTo(credentialVersion0Id.get()))
				.verifyComplete();

		StepVerifier.create(certificates.updateTransitionalVersion(certificateId.get(), credentialVersion0Id.get()))
				.assertNext(response -> {
					assertThat(response.getId()).isEqualTo(credentialVersion1Id.get());
					assertThat(response.isTransitional()).isFalse();
				})
				.assertNext(response -> {
					assertThat(response.getId()).isEqualTo(credentialVersion0Id.get());
					assertThat(response.isTransitional()).isTrue();
				})
				.verifyComplete();

		StepVerifier.create(certificates.updateTransitionalVersion(certificateId.get(), null))
				.assertNext(response -> {
					assertThat(response.getId()).isEqualTo(credentialVersion1Id.get());
					assertThat(response.isTransitional()).isFalse();
				})
				.verifyComplete();
	}

	@Test
	public void bulkRegenerateCertificates() {
		AtomicReference<CertificateCredential> rootCertificate = new AtomicReference<>();
		AtomicReference<String> signedCertificateId = new AtomicReference<>();

		StepVerifier.create(credentials.generate(CertificateParametersRequest.builder()
						.name(ROOT_CERT_NAME)
						.parameters(CertificateParameters.builder()
								.commonName("example.com")
								.certificateAuthority(true)
								.selfSign(true)
								.build())
						.build(),
				CertificateCredential.class))
				.assertNext(response -> {
					assertThat(response.getName().getName()).isEqualTo(ROOT_CERT_NAME.getName());

					rootCertificate.set(response.getValue());
				})
				.verifyComplete();

		StepVerifier.create(credentials.generate(CertificateParametersRequest.builder()
						.name(TEST_CERT_NAME)
						.parameters(CertificateParameters.builder()
								.commonName("example.com")
								.certificateAuthorityCredential(ROOT_CERT_NAME)
								.build())
						.build(),
				CertificateCredential.class))
				.assertNext(response -> {
					assertThat(response.getValue().getCertificateAuthority())
							.isEqualTo(rootCertificate.get().getCertificate());

					signedCertificateId.set(response.getId());
				})
				.verifyComplete();

		StepVerifier.create(credentials.getByNameWithHistory(TEST_CERT_NAME, CertificateCredential.class))
				.assertNext(response -> assertThat(response.getId()).isEqualTo(signedCertificateId.get()))
				.verifyComplete();

		StepVerifier.create(certificates.regenerate(ROOT_CERT_NAME))
				.assertNext(response -> assertThat(response).isEqualTo(TEST_CERT_NAME))
				.verifyComplete();
	}
}
