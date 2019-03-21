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
import org.springframework.credhub.support.password.PasswordCredential;
import org.springframework.credhub.support.password.PasswordCredentialRequest;
import org.springframework.credhub.support.password.PasswordParameters;
import org.springframework.credhub.support.password.PasswordParametersRequest;
import org.springframework.http.ResponseEntity;

@RunWith(Theories.class)
public class CredHubTemplateDetailPasswordUnitTests
		extends CredHubTemplateDetailUnitTestsBase<PasswordCredential, PasswordParameters> {
	private static final PasswordCredential CREDENTIAL = new PasswordCredential("secret");
	private static final PasswordParameters PARAMETERS = new PasswordParameters();

	@DataPoints("detail-responses")
	public static List<ResponseEntity<CredentialDetails<PasswordCredential>>> buildDetailResponses() {
		return buildDetailResponses(CredentialType.PASSWORD, CREDENTIAL);
	}

	@DataPoints("data-responses")
	public static List<ResponseEntity<CredentialDetailsData<PasswordCredential>>> buildDataResponses() {
		return buildDataResponses(CredentialType.PASSWORD, CREDENTIAL);
	}

	@Override
	public CredentialRequest<PasswordCredential> getWriteRequest() {
		return PasswordCredentialRequest.builder()
				.name(NAME)
				.value(CREDENTIAL)
				.build();
	}

	@Override
	protected ParametersRequest<PasswordParameters> getGenerateRequest() {
		return PasswordParametersRequest.builder()
				.name(NAME)
				.parameters(PARAMETERS)
				.build();
	}

	@Override
	public Class<PasswordCredential> getType() {
		return PasswordCredential.class;
	}

	@Theory
	public void write(@FromDataPoints("detail-responses")
					  ResponseEntity<CredentialDetails<PasswordCredential>> expectedResponse) {
		verifyWrite(expectedResponse);
	}

	@Theory
	public void generate(@FromDataPoints("detail-responses")
						 ResponseEntity<CredentialDetails<PasswordCredential>> expectedResponse) {
		verifyGenerate(expectedResponse);
	}

	@Theory
	public void regenerate(@FromDataPoints("detail-responses")
						 ResponseEntity<CredentialDetails<PasswordCredential>> expectedResponse) {
		verifyRegenerate(expectedResponse);
	}

	@Theory
	public void getById(@FromDataPoints("detail-responses")
						ResponseEntity<CredentialDetails<PasswordCredential>> expectedResponse) {
		verifyGetById(expectedResponse);
	}

	@Theory
	public void getByName(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<PasswordCredential>> expectedResponse) {
		verifyGetByName(expectedResponse);
	}

	@Theory
	public void getByNameWithHistory(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<PasswordCredential>> expectedResponse) {
		verifyGetByNameWithHistory(expectedResponse);
	}

	@Theory
	public void getByNameWithVersions(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<PasswordCredential>> expectedResponse) {
		verifyGetByNameWithVersions(expectedResponse);
	}
}