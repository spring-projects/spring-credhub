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

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.CredentialSummaryData;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.credential.CredHubCredentialTemplate.NAME_LIKE_URL_QUERY;
import static org.springframework.credhub.core.credential.CredHubCredentialTemplate.PATH_URL_QUERY;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RunWith(Theories.class)
public class CredHubCredentialTemplateSummaryUnitTests extends CredHubCredentialTemplateUnitTestsBase {
	@DataPoint("responses")
	public static ResponseEntity<CredentialSummaryData> successfulResponse =
			new ResponseEntity<>(new CredentialSummaryData(new CredentialSummary(NAME)), OK);

	@DataPoint("responses")
	public static ResponseEntity<CredentialSummaryData> httpErrorResponse =
			new ResponseEntity<>(new CredentialSummaryData(), UNAUTHORIZED);

	@Theory
	public void findByName(@FromDataPoints("responses")
														 ResponseEntity<CredentialSummaryData> expectedResponse) {
		when(restTemplate.getForEntity(NAME_LIKE_URL_QUERY, CredentialSummaryData.class, NAME.getName()))
				.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(OK)) {
			try {
				credHubTemplate.findByName(NAME);
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage()).contains(expectedResponse.getStatusCode().toString());
			}
		}
		else {
			List<CredentialSummary> response = credHubTemplate.findByName(NAME);

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	@Theory
	public void findByPath(@FromDataPoints("responses")
									   ResponseEntity<CredentialSummaryData> expectedResponse) {
		when(restTemplate.getForEntity(PATH_URL_QUERY, CredentialSummaryData.class, NAME.getName()))
				.thenReturn(expectedResponse);

		if (!expectedResponse.getStatusCode().equals(OK)) {
			try {
				credHubTemplate.findByPath(NAME.getName());
				fail("Exception should have been thrown");
			}
			catch (CredHubException e) {
				assertThat(e.getMessage()).contains(expectedResponse.getStatusCode().toString());
			}
		}
		else {
			List<CredentialSummary> response = credHubTemplate.findByPath(NAME.getName());

			assertResponseContainsExpectedCredentials(expectedResponse, response);
		}
	}

	private void assertResponseContainsExpectedCredentials(
			ResponseEntity<CredentialSummaryData> expectedResponse,
			List<CredentialSummary> response) {
		assertThat(response).isNotNull();
		assertThat(response.size()).isEqualTo(expectedResponse.getBody().getCredentials().size());
		assertThat(response).contains(expectedResponse.getBody().getCredentials().get(0));
	}
}