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

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.CredHubTemplate.NAME_URL_QUERY;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RunWith(Theories.class)
public class CredHubTemplateDetailsResponseUnitTests extends CredHubTemplateUnitTestsBase {
	private static final String CREDENTIAL_ID = "1111-1111-1111-1111";
	private static final String CREDENTIAL_VALUE = "secret";

	@DataPoint("responses")
	public static ResponseEntity<CredentialDetailsData> successfulResponse =
			new ResponseEntity<CredentialDetailsData>(
					CredentialDetailsData.builder()
							.datum(CredentialDetails.detailsBuilder()
									.name(NAME)
									.id(CREDENTIAL_ID)
									.passwordValue(CREDENTIAL_VALUE)
									.build())
							.build(),
					OK);

	@DataPoint("responses")
	public static ResponseEntity<CredentialDetailsData> httpErrorResponse =
			new ResponseEntity<CredentialDetailsData>(
					CredentialDetailsData.builder()
							.build(),
					UNAUTHORIZED);

	@Theory
	public void getByNameWithString(@FromDataPoints("responses") ResponseEntity<CredentialDetailsData> expectedResponse) {
		when(restTemplate.getForEntity(NAME_URL_QUERY, CredentialDetailsData.class, NAME.getName()))
				.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(OK)) {
			try {
				credHubTemplate.getByName(NAME.getName());
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage(), containsString(expectedResponse.getStatusCode().toString()));
			}
		}
		else {
			List<CredentialDetails> response = credHubTemplate.getByName(NAME.getName());

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	@Theory
	public void getByNameWithCredentialName(@FromDataPoints("responses") ResponseEntity<CredentialDetailsData> expectedResponse) {
		when(restTemplate.getForEntity(NAME_URL_QUERY, CredentialDetailsData.class, NAME.getName()))
				.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(OK)) {
			try {
				credHubTemplate.getByName(NAME);
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage(), containsString(expectedResponse.getStatusCode().toString()));
			}
		}
		else {
			List<CredentialDetails> response = credHubTemplate.getByName(NAME);

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	private void assertResponseContainsExpectedCredentials(
			ResponseEntity<CredentialDetailsData> expectedResponse,
			List<CredentialDetails> response) {
		assertThat(response, notNullValue());
		assertThat(response.size(), equalTo(expectedResponse.getBody().getData().size()));
		assertThat(response.get(0), equalTo(expectedResponse.getBody().getData().get(0)));
	}
}