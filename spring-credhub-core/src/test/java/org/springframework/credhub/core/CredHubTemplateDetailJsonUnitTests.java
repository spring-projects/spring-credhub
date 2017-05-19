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

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.JsonCredential;
import org.springframework.credhub.support.JsonWriteRequest;
import org.springframework.credhub.support.ValueType;
import org.springframework.credhub.support.WriteRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RunWith(Theories.class)
public class CredHubTemplateDetailJsonUnitTests
		extends CredHubTemplateDetailUnitTestsBase<JsonCredential> {
	private static final JsonCredential CREDENTIAL = new JsonCredential() {
		{
			put("data", "value");
			put("test", true);
		}
	};

	@DataPoints("detail-responses")
	public static List<ResponseEntity<CredentialDetails<JsonCredential>>> buildDetailResponses() {
		return buildDetailResponses(ValueType.JSON, CREDENTIAL);
	}

	@DataPoints("data-responses")
	public static List<ResponseEntity<CredentialDetailsData<JsonCredential>>> buildDataResponses() {
		return buildDataResponses(ValueType.JSON, CREDENTIAL);
	}

	@Override
	public WriteRequest<JsonCredential> getRequest() {
		return JsonWriteRequest.builder()
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
	public void getByNameWithString(@FromDataPoints("data-responses")
									ResponseEntity<CredentialDetailsData<JsonCredential>> expectedResponse) {
		verifyGetByNameWithString(expectedResponse);
	}

	@Theory
	public void getByNameWithCredentialName(@FromDataPoints("data-responses")
											ResponseEntity<CredentialDetailsData<JsonCredential>> expectedResponse) {
		verifyGetByNameWithCredentialName(expectedResponse);
	}
}