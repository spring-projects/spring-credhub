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

package org.springframework.credhub.core;

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
import org.springframework.credhub.support.KeyLength;
import org.springframework.credhub.support.ParametersRequest;
import org.springframework.credhub.support.ssh.SshCredential;
import org.springframework.credhub.support.ssh.SshCredentialRequest;
import org.springframework.credhub.support.ssh.SshParameters;
import org.springframework.credhub.support.ssh.SshParametersRequest;
import org.springframework.http.ResponseEntity;

@RunWith(Theories.class)
public class CredHubTemplateDetailSshUnitTests
		extends CredHubTemplateDetailUnitTestsBase<SshCredential, SshParameters> {
	private static final SshCredential CREDENTIAL = new SshCredential("public-key", "private-key");
	private static final SshParameters PARAMETERS = new SshParameters(KeyLength.LENGTH_2048, "comment");

	@DataPoints("detail-responses")
	public static List<ResponseEntity<CredentialDetails<SshCredential>>> buildDetailResponses() {
		return buildDetailResponses(CredentialType.RSA, CREDENTIAL);
	}

	@DataPoints("data-responses")
	public static List<ResponseEntity<CredentialDetailsData<SshCredential>>> buildDataResponses() {
		return buildDataResponses(CredentialType.RSA, CREDENTIAL);
	}

	@Override
	public CredentialRequest<SshCredential> getWriteRequest() {
		return SshCredentialRequest.builder()
				.name(NAME)
				.value(CREDENTIAL)
				.build();
	}

	@Override
	public ParametersRequest<SshParameters> getGenerateRequest() {
		return SshParametersRequest.builder()
				.name(NAME)
				.parameters(PARAMETERS)
				.build();
	}

	@Override
	public Class<SshCredential> getType() {
		return SshCredential.class;
	}

	@Theory
	public void write(@FromDataPoints("detail-responses")
					  ResponseEntity<CredentialDetails<SshCredential>> expectedResponse) {
		verifyWrite(expectedResponse);
	}

	@Theory
	public void generate(@FromDataPoints("detail-responses")
						 ResponseEntity<CredentialDetails<SshCredential>> expectedResponse) {
		verifyGenerate(expectedResponse);
	}

	@Theory
	public void regenerate(@FromDataPoints("detail-responses")
						 ResponseEntity<CredentialDetails<SshCredential>> expectedResponse) {
		verifyRegenerate(expectedResponse);
	}

	@Theory
	public void getById(@FromDataPoints("detail-responses")
						ResponseEntity<CredentialDetails<SshCredential>> expectedResponse) {
		verifyGetById(expectedResponse);
	}

	@Theory
	public void getByName(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<SshCredential>> expectedResponse) {
		verifyGetByName(expectedResponse);
	}

	@Theory
	public void getByNameWithHistory(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<SshCredential>> expectedResponse) {
		verifyGetByNameWithHistory(expectedResponse);
	}
}