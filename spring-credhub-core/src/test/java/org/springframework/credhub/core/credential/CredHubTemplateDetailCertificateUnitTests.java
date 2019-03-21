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

package org.springframework.credhub.core.credential;

import java.util.List;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.ParametersRequest;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateCredentialRequest;
import org.springframework.credhub.support.certificate.CertificateParameters;
import org.springframework.credhub.support.certificate.CertificateParametersRequest;
import org.springframework.http.ResponseEntity;

@RunWith(Theories.class)
public class CredHubTemplateDetailCertificateUnitTests
		extends CredHubTemplateDetailUnitTestsBase<CertificateCredential, CertificateParameters> {
	private static final CertificateCredential CREDENTIAL =
			new CertificateCredential("certificate", "authority", "private-key");
	private static final CertificateParameters PARAMETERS = CertificateParameters.builder()
			.commonName("common")
			.certificateAuthorityCredential("credential")
			.build();

	@DataPoints("detail-responses")
	public static List<ResponseEntity<CredentialDetails<CertificateCredential>>> buildDetailResponses() {
		return buildDetailResponses(CredentialType.CERTIFICATE, CREDENTIAL);
	}

	@DataPoints("data-responses")
	public static List<ResponseEntity<CredentialDetailsData<CertificateCredential>>> buildDataResponses() {
		return buildDataResponses(CredentialType.CERTIFICATE, CREDENTIAL);
	}

	@Override
	public CredentialRequest<CertificateCredential> getWriteRequest() {
		return CertificateCredentialRequest.builder()
				.name(NAME)
				.value(CREDENTIAL)
				.build();
	}

	@Override
	public ParametersRequest<CertificateParameters> getGenerateRequest() {
		return CertificateParametersRequest.builder()
				.name(NAME)
				.parameters(PARAMETERS)
				.build();
	}

	@Override
	public Class<CertificateCredential> getType() {
		return CertificateCredential.class;
	}

	@Theory
	public void write(@FromDataPoints("detail-responses")
					  ResponseEntity<CredentialDetails<CertificateCredential>> expectedResponse) {
		verifyWrite(expectedResponse);
	}

	@Theory
	public void generate(@FromDataPoints("detail-responses")
					  ResponseEntity<CredentialDetails<CertificateCredential>> expectedResponse) {
		verifyGenerate(expectedResponse);
	}

	@Theory
	public void regenerate(@FromDataPoints("detail-responses")
					  ResponseEntity<CredentialDetails<CertificateCredential>> expectedResponse) {
		verifyRegenerate(expectedResponse);
	}

	@Theory
	public void getById(@FromDataPoints("detail-responses")
						ResponseEntity<CredentialDetails<CertificateCredential>> expectedResponse) {
		verifyGetById(expectedResponse);
	}

	@Theory
	public void getByName(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<CertificateCredential>> expectedResponse) {
		verifyGetByName(expectedResponse);
	}

	@Theory
	public void getByNameWithHistory(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<CertificateCredential>> expectedResponse) {
		verifyGetByNameWithHistory(expectedResponse);
	}

	@Theory
	public void getByNameWithVersions(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<CertificateCredential>> expectedResponse) {
		verifyGetByNameWithVersions(expectedResponse);
	}
}