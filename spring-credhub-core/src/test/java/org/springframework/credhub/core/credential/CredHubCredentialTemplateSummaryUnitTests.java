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

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.ParameterDeclarations;

import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.CredentialSummaryData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

class CredHubCredentialTemplateSummaryUnitTests extends CredHubCredentialTemplateUnitTestsBase {

	@ParameterizedTest
	@ArgumentsSource(ResponseArgumentsProvider.class)
	void findByName(ResponseEntity<CredentialSummaryData> expectedResponse) {
		given(this.restTemplate.getForEntity(CredHubCredentialTemplate.NAME_LIKE_URL_QUERY, CredentialSummaryData.class,
				NAME.getName()))
			.willReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			assertThatExceptionOfType(CredHubException.class).isThrownBy(() -> this.credHubTemplate.findByName(NAME))
				.withMessageContaining(expectedResponse.getStatusCode().toString());
		}
		else {
			List<CredentialSummary> response = this.credHubTemplate.findByName(NAME);

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	@ParameterizedTest
	@ArgumentsSource(ResponseArgumentsProvider.class)
	void findByPath(ResponseEntity<CredentialSummaryData> expectedResponse) {
		given(this.restTemplate.getForEntity(CredHubCredentialTemplate.PATH_URL_QUERY, CredentialSummaryData.class,
				NAME.getName()))
			.willReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			assertThatExceptionOfType(CredHubException.class)
				.isThrownBy(() -> this.credHubTemplate.findByPath(NAME.getName()))
				.withMessageContaining(expectedResponse.getStatusCode().toString());
		}
		else {
			List<CredentialSummary> response = this.credHubTemplate.findByPath(NAME.getName());

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	private void assertResponseContainsExpectedCredentials(ResponseEntity<CredentialSummaryData> expectedResponse,
			List<CredentialSummary> response) {
		assertThat(response).isNotNull();
		assertThat(response).hasSameSizeAs(expectedResponse.getBody().getCredentials());
		assertThat(response).contains(expectedResponse.getBody().getCredentials().get(0));
	}

	static class ResponseArgumentsProvider implements ArgumentsProvider {

		@Override
		public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters,
				ExtensionContext context) {
			return Stream.of(
					Arguments.of(ResponseEntity.ok().body(new CredentialSummaryData(new CredentialSummary(NAME)))),
					Arguments.of(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CredentialSummaryData())));
		}

	}

}
