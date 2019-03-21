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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.ParametersRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.CredHubTemplate.BASE_URL_PATH;
import static org.springframework.credhub.core.CredHubTemplate.ID_URL_PATH;
import static org.springframework.credhub.core.CredHubTemplate.NAME_URL_QUERY;
import static org.springframework.credhub.core.CredHubTemplate.NAME_URL_QUERY_CURRENT;
import static org.springframework.credhub.core.CredHubTemplate.REGENERATE_URL_PATH;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SuppressWarnings("unchecked")
public abstract class CredHubTemplateDetailUnitTestsBase<T, P> extends CredHubTemplateUnitTestsBase {
	private static final String CREDENTIAL_ID = "1111-1111-1111-1111";

	protected abstract Class<T> getType();
	protected abstract CredentialRequest<T> getWriteRequest();

	protected ParametersRequest<P> getGenerateRequest() {
		throw new IllegalStateException("Tests that verify credential generation must override this method");
	}

	static <T> List<ResponseEntity<CredentialDetails<T>>> buildDetailResponses(CredentialType type, T credential) {
		return Arrays.asList(
				new ResponseEntity<>(
						new CredentialDetails<>(CREDENTIAL_ID, NAME, type, credential),
						OK),
				new ResponseEntity<>(new CredentialDetails<T>(), UNAUTHORIZED)
		);
	}

	static <T> List<ResponseEntity<CredentialDetailsData<T>>> buildDataResponses(CredentialType type, T credential) {
		return Arrays.asList(
				new ResponseEntity<>(
						new CredentialDetailsData<>(
								new CredentialDetails<>(CREDENTIAL_ID, NAME,
										type, credential)),
						OK),
				new ResponseEntity<>(
						new CredentialDetailsData<T>(), UNAUTHORIZED)
		);
	}

	void verifyWrite(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		CredentialRequest<T> request = getWriteRequest();

		when(restTemplate.exchange(eq(BASE_URL_PATH), eq(PUT),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class)))
						.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				credHubTemplate.write(request);
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage(), containsString(expectedResponse.getStatusCode().toString()));
			}
		}
		else {
			CredentialDetails<T> response = credHubTemplate.write(request);

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyGenerate(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		ParametersRequest<P> request = getGenerateRequest();

		when(restTemplate.exchange(eq(BASE_URL_PATH), eq(POST),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class)))
						.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				credHubTemplate.generate(request);
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage(), containsString(expectedResponse.getStatusCode().toString()));
			}
		}
		else {
			CredentialDetails<T> response = credHubTemplate.generate(request);

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	void verifyRegenerate(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		Map<String, Object> request = new HashMap<String, Object>() {{
				put("name", NAME.getName());
		}};

		when(restTemplate.exchange(eq(REGENERATE_URL_PATH), eq(POST),
				eq(new HttpEntity<>(request)), isA(ParameterizedTypeReference.class)))
						.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				credHubTemplate.regenerate(NAME);
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage(), containsString(expectedResponse.getStatusCode().toString()));
			}
		}
		else {
			CredentialDetails<T> response = credHubTemplate.regenerate(NAME);

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	@SuppressWarnings("deprecation")
	void verifyGetById(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		when(restTemplate.exchange(eq(ID_URL_PATH), eq(GET), isNull(HttpEntity.class),
				isA(ParameterizedTypeReference.class), eq(CREDENTIAL_ID)))
				.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(HttpStatus.OK)) {
			try {
				credHubTemplate.getById(CREDENTIAL_ID, String.class);
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage(), containsString(expectedResponse.getStatusCode().toString()));
			}
		}
		else {
			CredentialDetails<T> response =
					credHubTemplate.getById(CREDENTIAL_ID, getType());

			assertDetailsResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	@SuppressWarnings("deprecation")
	void verifyGetByName(ResponseEntity<CredentialDetailsData<T>> expectedResponse) {
		when(restTemplate.exchange(eq(NAME_URL_QUERY_CURRENT), eq(GET), isNull(HttpEntity.class),
				isA(ParameterizedTypeReference.class), eq(NAME.getName())))
				.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(OK)) {
			try {
				credHubTemplate.getByName(NAME, String.class);
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage(),
						containsString(expectedResponse.getStatusCode().toString()));
			}
		}
		else {
			CredentialDetails<T> response = credHubTemplate.getByName(NAME, getType());

			assertDataResponseContainsExpectedCredential(expectedResponse, response);
		}
	}

	@SuppressWarnings("deprecation")
	void verifyGetByNameWithHistory(ResponseEntity<CredentialDetailsData<T>> expectedResponse) {
		when(restTemplate.exchange(eq(NAME_URL_QUERY), eq(GET), isNull(HttpEntity.class),
				isA(ParameterizedTypeReference.class), eq(NAME.getName())))
				.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(OK)) {
			try {
				credHubTemplate.getByNameWithHistory(NAME, String.class);
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage(),
						containsString(expectedResponse.getStatusCode().toString()));
			}
		}
		else {
			List<CredentialDetails<T>> response = credHubTemplate.getByNameWithHistory(NAME, getType());

			assertDataResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	private void assertDataResponseContainsExpectedCredentials(
			ResponseEntity<CredentialDetailsData<T>> expectedResponse,
			List<CredentialDetails<T>> response) {
		assertThat(response, notNullValue());
		assertThat(response.size(), equalTo(expectedResponse.getBody().getData().size()));
		assertThat(response.get(0), equalTo(expectedResponse.getBody().getData().get(0)));
	}

	private void assertDataResponseContainsExpectedCredential(
			ResponseEntity<CredentialDetailsData<T>> expectedResponse,
			CredentialDetails<T> response) {
		assertThat(response, notNullValue());
		assertThat(1, equalTo(expectedResponse.getBody().getData().size()));
		assertThat(response, equalTo(expectedResponse.getBody().getData().get(0)));
	}

	private void assertDetailsResponseContainsExpectedCredential(
			ResponseEntity<CredentialDetails<T>> expectedResponse,
			CredentialDetails<T> response) {
		assertThat(response, notNullValue());
		assertThat(response, equalTo(expectedResponse.getBody()));
	}
}