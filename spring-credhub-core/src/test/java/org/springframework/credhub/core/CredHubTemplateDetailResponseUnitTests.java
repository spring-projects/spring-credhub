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

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.PasswordWriteRequest;
import org.springframework.credhub.support.ValueType;
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
import static org.springframework.credhub.core.TypeUtils.getDetailsReference;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RunWith(Theories.class)
public class CredHubTemplateDetailResponseUnitTests extends CredHubTemplateUnitTestsBase {
	private static final String CREDENTIAL_ID = "1111-1111-1111-1111";
	private static final String CREDENTIAL_VALUE = "secret";

	@DataPoint("responses")
	public static ResponseEntity<CredentialDetails<String>> successfulResponse =
			new ResponseEntity<CredentialDetails<String>>(
					new CredentialDetails<String>(CREDENTIAL_ID, NAME, ValueType.PASSWORD, CREDENTIAL_VALUE),
					OK);

	@DataPoint("responses")
	public static ResponseEntity<CredentialDetails<String>> httpErrorResponse =
			new ResponseEntity<CredentialDetails<String>>(new CredentialDetails<String>(), UNAUTHORIZED);

	@Theory
	public void write(@FromDataPoints("responses") ResponseEntity<CredentialDetails<String>> expectedResponse) {
		PasswordWriteRequest request = PasswordWriteRequest.builder()
				.name(NAME)
				.value("secret")
				.build();

		final ParameterizedTypeReference<CredentialDetails<String>> ref = getDetailsReference(String.class);

		when(restTemplate.exchange(BASE_URL_PATH, PUT, new HttpEntity<WriteRequest<String>>(request), ref))
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
			CredentialDetails<String> response = credHubTemplate.write(request);

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	@Theory
	public void getById(@FromDataPoints("responses") ResponseEntity<CredentialDetails<String>> expectedResponse) {
		final ParameterizedTypeReference<CredentialDetails<String>> ref = getDetailsReference(String.class);

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
			CredentialDetails<String> response = credHubTemplate.getById(CREDENTIAL_ID, String.class);

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	private void assertResponseContainsExpectedCredentials(
			ResponseEntity<CredentialDetails<String>> expectedResponse, CredentialDetails<String> response) {
		assertThat(response, notNullValue());
		assertThat(response, equalTo(expectedResponse.getBody()));
	}
}