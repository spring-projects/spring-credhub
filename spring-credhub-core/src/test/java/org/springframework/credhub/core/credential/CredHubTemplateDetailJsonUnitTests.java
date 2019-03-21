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

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.http.ResponseEntity;

@RunWith(Theories.class)
public class CredHubTemplateDetailJsonUnitTests
		extends CredHubTemplateDetailUnitTestsBase<JsonCredential, Void> {
	private static final JsonCredential CREDENTIAL = new JsonCredential() {
		{
			put("data", "value");
			put("test", true);
		}
	};

	@DataPoints("detail-responses")
	public static List<ResponseEntity<CredentialDetails<JsonCredential>>> buildDetailResponses() {
		return buildDetailResponses(CredentialType.JSON, CREDENTIAL);
	}

	@DataPoints("data-responses")
	public static List<ResponseEntity<CredentialDetailsData<JsonCredential>>> buildDataResponses() {
		return buildDataResponses(CredentialType.JSON, CREDENTIAL);
	}

	@Override
	public CredentialRequest<JsonCredential> getWriteRequest() {
		return JsonCredentialRequest.builder()
				.name(NAME)
				.value(CREDENTIAL)
				.build();
	}

	@Override
	public Class<JsonCredential> getType() {
		return JsonCredential.class;
	}

	@Theory
	public void write(@FromDataPoints("detail-responses")
					  ResponseEntity<CredentialDetails<JsonCredential>> expectedResponse) {
		verifyWrite(expectedResponse);
	}

	@Theory
	public void getById(@FromDataPoints("detail-responses")
						ResponseEntity<CredentialDetails<JsonCredential>> expectedResponse) {
		verifyGetById(expectedResponse);
	}

	@Theory
	public void getByName(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<JsonCredential>> expectedResponse) {
		verifyGetByName(expectedResponse);
	}

	@Theory
	public void getByNameWithHistory(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<JsonCredential>> expectedResponse) {
		verifyGetByNameWithHistory(expectedResponse);
	}

	@Theory
	public void getByNameWithVersions(@FromDataPoints("data-responses")
						ResponseEntity<CredentialDetailsData<JsonCredential>> expectedResponse) {
		verifyGetByNameWithVersions(expectedResponse);
	}
}