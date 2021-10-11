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

package org.springframework.credhub.core.credential;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

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

public class CredHubTemplateDetailPasswordUnitTests
		extends CredHubTemplateDetailUnitTestsBase<PasswordCredential, PasswordParameters> {

	private static final PasswordCredential CREDENTIAL = new PasswordCredential("secret");

	private static final PasswordParameters PARAMETERS = new PasswordParameters();

	@Override
	public CredentialRequest<PasswordCredential> getWriteRequest() {
		return PasswordCredentialRequest.builder().name(NAME).value(CREDENTIAL).build();
	}

	@Override
	protected ParametersRequest<PasswordParameters> getGenerateRequest() {
		return PasswordParametersRequest.builder().name(NAME).parameters(PARAMETERS).build();
	}

	@Override
	public Class<PasswordCredential> getType() {
		return PasswordCredential.class;
	}

	@ParameterizedTest
	@ArgumentsSource(DetailResponseArgumentsProvider.class)
	public void write(ResponseEntity<CredentialDetails<PasswordCredential>> expectedResponse) {
		verifyWrite(expectedResponse);
	}

	@ParameterizedTest
	@ArgumentsSource(DetailResponseArgumentsProvider.class)
	public void generate(ResponseEntity<CredentialDetails<PasswordCredential>> expectedResponse) {
		verifyGenerate(expectedResponse);
	}

	@ParameterizedTest
	@ArgumentsSource(DetailResponseArgumentsProvider.class)
	public void regenerate(ResponseEntity<CredentialDetails<PasswordCredential>> expectedResponse) {
		verifyRegenerate(expectedResponse);
	}

	@ParameterizedTest
	@ArgumentsSource(DetailResponseArgumentsProvider.class)
	public void getById(ResponseEntity<CredentialDetails<PasswordCredential>> expectedResponse) {
		verifyGetById(expectedResponse);
	}

	@ParameterizedTest
	@ArgumentsSource(DataResponseArgumentsProvider.class)
	public void getByName(ResponseEntity<CredentialDetailsData<PasswordCredential>> expectedResponse) {
		verifyGetByName(expectedResponse);
	}

	@ParameterizedTest
	@ArgumentsSource(DataResponseArgumentsProvider.class)
	public void getByNameWithHistory(ResponseEntity<CredentialDetailsData<PasswordCredential>> expectedResponse) {
		verifyGetByNameWithHistory(expectedResponse);
	}

	@ParameterizedTest
	@ArgumentsSource(DataResponseArgumentsProvider.class)
	public void getByNameWithVersions(ResponseEntity<CredentialDetailsData<PasswordCredential>> expectedResponse) {
		verifyGetByNameWithVersions(expectedResponse);
	}

	static class DetailResponseArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return buildDetailArguments(CredentialType.PASSWORD, CREDENTIAL);
		}

	}

	static class DataResponseArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return buildDataArguments(CredentialType.PASSWORD, CREDENTIAL);
		}

	}

}
