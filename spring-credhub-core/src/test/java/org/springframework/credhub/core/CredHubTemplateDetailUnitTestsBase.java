/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.core;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.WriteRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.CredHubTemplate.BASE_URL_PATH;
import static org.springframework.credhub.core.CredHubTemplate.ID_URL_PATH;
import static org.springframework.credhub.core.CredHubTemplate.NAME_URL_QUERY;
import static org.springframework.credhub.core.TypeUtils.getDetailsReference;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

public abstract class CredHubTemplateDetailUnitTestsBase<T> extends CredHubTemplateUnitTestsBase {
	static final String CREDENTIAL_ID = "1111-1111-1111-1111";

	public abstract WriteRequest<T> getRequest();
	public abstract Class<T> getType();

	void verifyWrite(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		WriteRequest<T> request = getRequest();

		final ParameterizedTypeReference<CredentialDetails<T>> ref =
				getDetailsReference(getType());

		when(restTemplate.exchange(BASE_URL_PATH, PUT,
				new HttpEntity<WriteRequest<T>>(request), ref))
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

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	void verifyGetById(ResponseEntity<CredentialDetails<T>> expectedResponse) {
		final ParameterizedTypeReference<CredentialDetails<T>> ref =
				getDetailsReference(getType());

		when(restTemplate.exchange(ID_URL_PATH, GET, null, ref, CREDENTIAL_ID))
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

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	void verifyGetByNameWithString(ResponseEntity<CredentialDetailsData<T>> expectedResponse) {
		ParameterizedTypeReference<CredentialDetailsData<T>> ref = TypeUtils
				.getDetailsDataReference(getType());

		when(restTemplate.exchange(NAME_URL_QUERY, GET, null, ref, NAME.getName()))
				.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(OK)) {
			try {
				credHubTemplate.getByName(NAME.getName(), String.class);
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage(),
						containsString(expectedResponse.getStatusCode().toString()));
			}
		}
		else {
			List<CredentialDetails<T>> response = credHubTemplate
					.getByName(NAME.getName(), getType());

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}


	void verifyGetByNameWithCredentialName(ResponseEntity<CredentialDetailsData<T>> expectedResponse) {
		ParameterizedTypeReference<CredentialDetailsData<T>> ref = TypeUtils
				.getDetailsDataReference(getType());

		when(restTemplate.exchange(NAME_URL_QUERY, GET, null, ref, NAME.getName()))
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
			List<CredentialDetails<T>> response = credHubTemplate.getByName(NAME, getType());

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	private void assertResponseContainsExpectedCredentials(
			ResponseEntity<CredentialDetailsData<T>> expectedResponse,
			List<CredentialDetails<T>> response) {
		assertThat(response, notNullValue());
		assertThat(response.size(), equalTo(expectedResponse.getBody().getData().size()));
		assertThat(response.get(0), equalTo(expectedResponse.getBody().getData().get(0)));
	}

	private void assertResponseContainsExpectedCredentials(
			ResponseEntity<CredentialDetails<T>> expectedResponse,
			CredentialDetails<T> response) {
		assertThat(response, notNullValue());
		assertThat(response, equalTo(expectedResponse.getBody()));
	}
}