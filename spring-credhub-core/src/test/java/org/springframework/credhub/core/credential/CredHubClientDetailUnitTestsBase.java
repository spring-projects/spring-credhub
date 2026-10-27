/*
 * Copyright 2016-2026 the original author or authors.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.ParametersRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

abstract class CredHubClientDetailUnitTestsBase<T, P> extends CredHubClientCredentialUnitTestsBase {

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
				Arguments.of(ResponseEntity.ok()
					.body(new CredentialDetailsData<>(new CredentialDetails<>(CREDENTIAL_ID, NAME, type, credential)))),
				Arguments.of(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CredentialDetailsData<>())));
	}

	void verifyWrite(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		CredentialRequest<T> request = getWriteRequest();

		this.server.expect(requestTo(BASE_URL + "/api/v1/data"))
			.andExpect(method(HttpMethod.PUT))
			.andExpect(jsonBody(request))
			.andRespond(respond(expectedResponse));

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			assertThatExceptionOfType(CredHubException.class).isThrownBy(() -> this.credentials.write(request))
				.withMessageContaining(statusLine(expectedResponse.getStatusCode()));
		}
		else {
			CredentialDetails<T> response = this.credentials.write(request);

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyGenerate(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		ParametersRequest<P> request = getGenerateRequest();

		this.server.expect(requestTo(BASE_URL + "/api/v1/data"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(request))
			.andRespond(respond(expectedResponse));

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			assertThatExceptionOfType(CredHubException.class).isThrownBy(() -> this.credentials.generate(request))
				.withMessageContaining(statusLine(expectedResponse.getStatusCode()));
		}
		else {
			CredentialDetails<T> response = this.credentials.generate(request);

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyRegenerate(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		Map<String, Object> request = new HashMap<>() {
			{
				put("name", NAME.getName());
			}
		};

		this.server.expect(requestTo(BASE_URL + "/api/v1/regenerate"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(request))
			.andRespond(respond(expectedResponse));

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			assertThatExceptionOfType(CredHubException.class)
				.isThrownBy(() -> this.credentials.regenerate(NAME, getType()))
				.withMessageContaining(statusLine(expectedResponse.getStatusCode()));
		}
		else {
			CredentialDetails<T> response = this.credentials.regenerate(NAME, getType());

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyRegenerateWithMetadata(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		Map<String, Object> metadata = Map.of("key", "metadata-value");

		Map<String, Object> request = new HashMap<>() {
			{
				put("name", NAME.getName());
				put("metadata", metadata);
			}
		};

		this.server.expect(requestTo(BASE_URL + "/api/v1/regenerate"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(request))
			.andRespond(respond(expectedResponse));

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			assertThatExceptionOfType(CredHubException.class)
				.isThrownBy(() -> this.credentials.regenerate(NAME, getType(), metadata))
				.withMessageContaining(statusLine(expectedResponse.getStatusCode()));
		}
		else {
			CredentialDetails<T> response = this.credentials.regenerate(NAME, getType(), metadata);

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyGetById(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		this.server.expect(requestTo(BASE_URL + "/api/v1/data/" + CREDENTIAL_ID))
			.andExpect(method(HttpMethod.GET))
			.andRespond(respond(expectedResponse));

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			assertThatExceptionOfType(CredHubException.class)
				.isThrownBy(() -> this.credentials.getById(CREDENTIAL_ID, String.class))
				.withMessageContaining(statusLine(expectedResponse.getStatusCode()));
		}
		else {
			CredentialDetails<T> response = this.credentials.getById(CREDENTIAL_ID, getType());

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyGetByName(ResponseEntity<CredentialDetailsData<T>> expectedResponse) {
		this.server.expect(requestTo(BASE_URL + "/api/v1/data?name=" + encodedName() + "&current=true"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(respond(expectedResponse));

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			assertThatExceptionOfType(CredHubException.class)
				.isThrownBy(() -> this.credentials.getByName(NAME, String.class))
				.withMessageContaining(statusLine(expectedResponse.getStatusCode()));
		}
		else {
			CredentialDetails<T> response = this.credentials.getByName(NAME, getType());

			assertDataResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyGetByNameWithHistory(ResponseEntity<CredentialDetailsData<T>> expectedResponse) {
		this.server.expect(requestTo(BASE_URL + "/api/v1/data?name=" + encodedName()))
			.andExpect(method(HttpMethod.GET))
			.andRespond(respond(expectedResponse));

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			assertThatExceptionOfType(CredHubException.class)
				.isThrownBy(() -> this.credentials.getByNameWithHistory(NAME, String.class))
				.withMessageContaining(statusLine(expectedResponse.getStatusCode()));
		}
		else {
			List<CredentialDetails<T>> response = this.credentials.getByNameWithHistory(NAME, getType());

			assertDataResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	void verifyGetByNameWithVersions(ResponseEntity<CredentialDetailsData<T>> expectedResponse) {
		this.server.expect(requestTo(BASE_URL + "/api/v1/data?name=" + encodedName() + "&versions=5"))
			.andExpect(method(HttpMethod.GET))
			.andRespond(respond(expectedResponse));

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			assertThatExceptionOfType(CredHubException.class)
				.isThrownBy(() -> this.credentials.getByNameWithHistory(NAME, 5, String.class))
				.withMessageContaining(statusLine(expectedResponse.getStatusCode()));
		}
		else {
			List<CredentialDetails<T>> response = this.credentials.getByNameWithHistory(NAME, 5, getType());

			assertDataResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	private void assertDataResponseContainsExpectedCredentials(
			ResponseEntity<CredentialDetailsData<T>> expectedResponse, List<CredentialDetails<T>> response) {
		List<CredentialDetails<T>> expected = expectedResponse.getBody().getData();
		assertThat(response).isNotNull();
		assertThat(response).hasSize(expected.size());
		for (int i = 0; i < expected.size(); i++) {
			assertSameCredential(response.get(i), expected.get(i));
		}
	}

	private void assertDataResponseContainsExpectedCredential(ResponseEntity<CredentialDetailsData<T>> expectedResponse,
			CredentialDetails<T> response) {
		assertThat(response).isNotNull();
		assertSameCredential(response, expectedResponse.getBody().getData().get(0));
	}

	private void assertDetailsResponseContainsExpectedCredential(ResponseEntity<CredentialDetails<T>> expectedResponse,
			CredentialDetails<T> response) {
		assertThat(response).isNotNull();
		assertSameCredential(response, expectedResponse.getBody());
	}

	/**
	 * The old tests compared with {@code equals()}, which held because a mocked
	 * {@code RestTemplate} returned the very same objects. A response read from the wire
	 * is a different instance of each type, and several credential types do not define
	 * value-based {@code equals()}, so compare field by field instead. The name is
	 * deserialized as a {@code CredentialName} rather than the
	 * {@code SimpleCredentialName} used to build the fixture, so it is compared by its
	 * string form.
	 */
	private void assertSameCredential(CredentialDetails<T> actual, CredentialDetails<T> expected) {
		assertThat(actual).usingRecursiveComparison().ignoringFields("name").isEqualTo(expected);
		assertThat(actual.getName().getName()).isEqualTo(expected.getName().getName());
	}

}
