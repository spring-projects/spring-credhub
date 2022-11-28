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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.ParametersRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;

@SuppressWarnings("unchecked")
public abstract class CredHubTemplateDetailUnitTestsBase<T, P> extends CredHubCredentialTemplateUnitTestsBase {

	private static final String CREDENTIAL_ID = "1111-1111-1111-1111";

	protected abstract Class<T> getType();

	protected abstract CredentialRequest<T> getWriteRequest();

	protected ParametersRequest<P> getGenerateRequest() {
		throw new IllegalStateException("Tests that verify credential generation must override this method");
	}

	static <T> Stream<? extends Arguments> buildDetailArguments(CredentialType type, T credential) {
		return Stream.of(
				Arguments.of(ResponseEntity.ok().body(new CredentialDetails<>(CREDENTIAL_ID, NAME, type, credential))),
				Arguments.of(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CredentialDetails<>())));
	}

	static <T> Stream<? extends Arguments> buildDataArguments(CredentialType type, T credential) {
		return Stream.of(
				Arguments.of(ResponseEntity.ok().body(
						new CredentialDetailsData<>(new CredentialDetails<>(CREDENTIAL_ID, NAME, type, credential)))),
				Arguments.of(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CredentialDetailsData<>())));
	}

	static <T> List<ResponseEntity<CredentialDetails<T>>> buildDetailResponses(CredentialType type, T credential) {
		return Arrays.asList(ResponseEntity.ok().body(new CredentialDetails<>(CREDENTIAL_ID, NAME, type, credential)),
				ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CredentialDetails<>()));
	}

	static <T> List<ResponseEntity<CredentialDetailsData<T>>> buildDataResponses(CredentialType type, T credential) {
		return Arrays.asList(
				ResponseEntity.ok().body(
						new CredentialDetailsData<>(new CredentialDetails<>(CREDENTIAL_ID, NAME, type, credential))),
				ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CredentialDetailsData<>()));
	}

	void verifyWrite(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		CredentialRequest<T> request = getWriteRequest();

		given(this.restTemplate.exchange(eq(CredHubCredentialTemplate.BASE_URL_PATH), eq(HttpMethod.PUT),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class))).willReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				this.credHubTemplate.write(request);
				fail("Exception should have been thrown");
			}
			catch (CredHubException ex) {
				assertThat(ex.getMessage()).contains(expectedResponse.getStatusCode().toString());
			}
		}
		else {
			CredentialDetails<T> response = this.credHubTemplate.write(request);

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyGenerate(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		ParametersRequest<P> request = getGenerateRequest();

		given(this.restTemplate.exchange(eq(CredHubCredentialTemplate.BASE_URL_PATH), eq(HttpMethod.POST),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class))).willReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				this.credHubTemplate.generate(request);
				fail("Exception should have been thrown");
			}
			catch (CredHubException ex) {
				assertThat(ex.getMessage()).contains(expectedResponse.getStatusCode().toString());
			}
		}
		else {
			CredentialDetails<T> response = this.credHubTemplate.generate(request);

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyRegenerate(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		Map<String, Object> request = new HashMap<>() {
			{
				put(CredHubCredentialTemplate.NAME_REQUEST_FIELD, NAME.getName());
			}
		};

		given(this.restTemplate.exchange(eq(CredHubCredentialTemplate.REGENERATE_URL_PATH), eq(HttpMethod.POST),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class))).willReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				this.credHubTemplate.regenerate(NAME, getType());
				fail("Exception should have been thrown");
			}
			catch (CredHubException ex) {
				assertThat(ex.getMessage()).contains(expectedResponse.getStatusCode().toString());
			}
		}
		else {
			CredentialDetails<T> response = this.credHubTemplate.regenerate(NAME, getType());

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyGetById(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		given(this.restTemplate.exchange(eq(CredHubCredentialTemplate.ID_URL_PATH), eq(HttpMethod.GET), isNull(),
				isA(ParameterizedTypeReference.class), eq(CREDENTIAL_ID))).willReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				this.credHubTemplate.getById(CREDENTIAL_ID, String.class);
				fail("Exception should have been thrown");
			}
			catch (CredHubException ex) {
				assertThat(ex.getMessage()).contains(expectedResponse.getStatusCode().toString());
			}
		}
		else {
			CredentialDetails<T> response = this.credHubTemplate.getById(CREDENTIAL_ID, getType());

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyGetByName(ResponseEntity<CredentialDetailsData<T>> expectedResponse) {
		given(this.restTemplate.exchange(eq(CredHubCredentialTemplate.NAME_URL_QUERY_CURRENT), eq(HttpMethod.GET),
				isNull(), isA(ParameterizedTypeReference.class), eq(NAME.getName()))).willReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				this.credHubTemplate.getByName(NAME, String.class);
				fail("Exception should have been thrown");
			}
			catch (CredHubException ex) {
				assertThat(ex.getMessage()).contains(expectedResponse.getStatusCode().toString());
			}
		}
		else {
			CredentialDetails<T> response = this.credHubTemplate.getByName(NAME, getType());

			assertDataResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyGetByNameWithHistory(ResponseEntity<CredentialDetailsData<T>> expectedResponse) {
		given(this.restTemplate.exchange(eq(CredHubCredentialTemplate.NAME_URL_QUERY), eq(HttpMethod.GET), isNull(),
				isA(ParameterizedTypeReference.class), eq(NAME.getName()))).willReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				this.credHubTemplate.getByNameWithHistory(NAME, String.class);
				fail("Exception should have been thrown");
			}
			catch (CredHubException ex) {
				assertThat(ex.getMessage()).contains(expectedResponse.getStatusCode().toString());
			}
		}
		else {
			List<CredentialDetails<T>> response = this.credHubTemplate.getByNameWithHistory(NAME, getType());

			assertDataResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	void verifyGetByNameWithVersions(ResponseEntity<CredentialDetailsData<T>> expectedResponse) {
		given(this.restTemplate.exchange(eq(CredHubCredentialTemplate.NAME_URL_QUERY_VERSIONS), eq(HttpMethod.GET),
				isNull(), isA(ParameterizedTypeReference.class), eq(NAME.getName()), eq(5)))
						.willReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				this.credHubTemplate.getByNameWithHistory(NAME, 5, String.class);
				fail("Exception should have been thrown");
			}
			catch (CredHubException ex) {
				assertThat(ex.getMessage()).contains(expectedResponse.getStatusCode().toString());
			}
		}
		else {
			List<CredentialDetails<T>> response = this.credHubTemplate.getByNameWithHistory(NAME, 5, getType());

			assertDataResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	private void assertDataResponseContainsExpectedCredentials(
			ResponseEntity<CredentialDetailsData<T>> expectedResponse, List<CredentialDetails<T>> response) {
		assertThat(response).isNotNull();
		assertThat(response).hasSize(expectedResponse.getBody().getData().size());
		assertThat(response).contains(expectedResponse.getBody().getData().get(0));
	}

	private void assertDataResponseContainsExpectedCredential(ResponseEntity<CredentialDetailsData<T>> expectedResponse,
			CredentialDetails<T> response) {
		assertThat(response).isNotNull();
		assertThat(response).isEqualTo(expectedResponse.getBody().getData().get(0));
	}

	private void assertDetailsResponseContainsExpectedCredential(ResponseEntity<CredentialDetails<T>> expectedResponse,
			CredentialDetails<T> response) {
		assertThat(response).isNotNull();
		assertThat(response).isEqualTo(expectedResponse.getBody());
	}

}
