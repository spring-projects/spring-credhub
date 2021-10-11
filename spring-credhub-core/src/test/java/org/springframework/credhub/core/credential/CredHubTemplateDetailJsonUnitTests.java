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
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.http.ResponseEntity;

public class CredHubTemplateDetailJsonUnitTests extends CredHubTemplateDetailUnitTestsBase<JsonCredential, Void> {

	private static final JsonCredential CREDENTIAL = new JsonCredential() {
		{
			put("data", "value");
			put("test", true);
		}
	};

	@Override
	public CredentialRequest<JsonCredential> getWriteRequest() {
		return JsonCredentialRequest.builder().name(NAME).value(CREDENTIAL).build();
	}

	@Override
	public Class<JsonCredential> getType() {
		return JsonCredential.class;
	}

	@ParameterizedTest
	@ArgumentsSource(DetailResponseArgumentsProvider.class)
	public void write(ResponseEntity<CredentialDetails<JsonCredential>> expectedResponse) {
		verifyWrite(expectedResponse);
	}

	@ParameterizedTest
	@ArgumentsSource(DetailResponseArgumentsProvider.class)
	public void getById(ResponseEntity<CredentialDetails<JsonCredential>> expectedResponse) {
		verifyGetById(expectedResponse);
	}

	@ParameterizedTest
	@ArgumentsSource(DataResponseArgumentsProvider.class)
	public void getByName(ResponseEntity<CredentialDetailsData<JsonCredential>> expectedResponse) {
		verifyGetByName(expectedResponse);
	}

	@ParameterizedTest
	@ArgumentsSource(DataResponseArgumentsProvider.class)
	public void getByNameWithHistory(ResponseEntity<CredentialDetailsData<JsonCredential>> expectedResponse) {
		verifyGetByNameWithHistory(expectedResponse);
	}

	@ParameterizedTest
	@ArgumentsSource(DataResponseArgumentsProvider.class)
	public void getByNameWithVersions(ResponseEntity<CredentialDetailsData<JsonCredential>> expectedResponse) {
		verifyGetByNameWithVersions(expectedResponse);
	}

	static class DetailResponseArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return buildDetailArguments(CredentialType.JSON, CREDENTIAL);
		}

	}

	static class DataResponseArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return buildDataArguments(CredentialType.JSON, CREDENTIAL);
		}

	}

}
