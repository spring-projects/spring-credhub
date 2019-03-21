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

package org.springframework.credhub.support.password;

import org.junit.Test;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.JsonParsingUnitTestsBase;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PasswordCredentialDetailsUnitTests extends JsonParsingUnitTestsBase {
	private static final String PASSWORD_CREDENTIALS =
			"  \"type\": \"password\"," +
			"  \"value\": \"secret\"";

	@Test
	public void deserializeDetails() throws Exception {
		CredentialDetails<PasswordCredential> data =
				parseDetails(PASSWORD_CREDENTIALS);

		assertDetails(data);
	}

	@Test
	public void deserializeDetailsData() throws Exception {
		CredentialDetailsData<PasswordCredential> response =
				parseDetailsData(PASSWORD_CREDENTIALS);

		assertThat(response.getData().size(), equalTo(1));

		CredentialDetails<PasswordCredential> data = response.getData().get(0);

		assertDetails(data);
	}

	private void assertDetails(CredentialDetails<PasswordCredential> data) {
		assertCommonDetails(data);
		
		assertThat(data.getCredentialType(), equalTo(CredentialType.PASSWORD));
		assertThat(data.getValue().getPassword(), equalTo("secret"));
	}
}
