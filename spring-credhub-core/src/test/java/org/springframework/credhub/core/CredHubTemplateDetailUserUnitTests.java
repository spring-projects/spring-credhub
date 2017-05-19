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

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.UserCredential;
import org.springframework.credhub.support.UserWriteRequest;
import org.springframework.credhub.support.ValueType;
import org.springframework.credhub.support.WriteRequest;
import org.springframework.http.ResponseEntity;

@RunWith(Theories.class)
public class CredHubTemplateDetailUserUnitTests
		extends CredHubTemplateDetailUnitTestsBase<UserCredential> {
	private static final UserCredential CREDENTIAL = new UserCredential("myname", "secret");

	@DataPoints("detail-responses")
	public static List<ResponseEntity<CredentialDetails<UserCredential>>> buildDetailResponses() {
		return buildDetailResponses(ValueType.USER, CREDENTIAL);
	}

	@DataPoints("data-responses")
	public static List<ResponseEntity<CredentialDetailsData<UserCredential>>> buildDataResponses() {
		return buildDataResponses(ValueType.USER, CREDENTIAL);
	}

	@Override
	public WriteRequest<UserCredential> getRequest() {
		return UserWriteRequest.builder()
				.name(NAME)
				.value(CREDENTIAL)
				.build();
	}

	@Override
	public Class<UserCredential> getType() {
		return UserCredential.class;
	}

	@Theory
	public void write(@FromDataPoints("detail-responses")
					  ResponseEntity<CredentialDetails<UserCredential>> expectedResponse) {
		verifyWrite(expectedResponse);
	}

	@Theory
	public void getById(@FromDataPoints("detail-responses")
						ResponseEntity<CredentialDetails<UserCredential>> expectedResponse) {
		verifyGetById(expectedResponse);
	}

	@Theory
	public void getByNameWithString(@FromDataPoints("data-responses")
									ResponseEntity<CredentialDetailsData<UserCredential>> expectedResponse) {
		verifyGetByNameWithString(expectedResponse);
	}

	@Theory
	public void getByNameWithCredentialName(@FromDataPoints("data-responses")
											ResponseEntity<CredentialDetailsData<UserCredential>> expectedResponse) {
		verifyGetByNameWithCredentialName(expectedResponse);
	}
}