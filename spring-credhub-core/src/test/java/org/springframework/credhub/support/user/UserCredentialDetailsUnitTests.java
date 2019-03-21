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

package org.springframework.credhub.support.user;

import org.junit.Test;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.JsonParsingUnitTestsBase;

import static org.assertj.core.api.Assertions.assertThat;

public class UserCredentialDetailsUnitTests extends JsonParsingUnitTestsBase {
	private static final String USER_CREDENTIALS =
			"  \"type\": \"user\"," +
			"  \"value\": {" +
			"  \"username\": \"myname\"," +
			"  \"password\": \"secret\"," +
			"  \"password_hash\": \"secret-hash\"" +
			"  }";

	@Test
	public void deserializeDetails() {
		CredentialDetails<UserCredential> data = parseDetails(USER_CREDENTIALS);

		assertDetails(data);
	}

	@Test
	public void deserializeDetailsData() {
		CredentialDetailsData<UserCredential> response = parseDetailsData(USER_CREDENTIALS);

		assertThat(response.getData()).hasSize(1);

		CredentialDetails<UserCredential> data = response.getData().get(0);

		assertDetails(data);
	}

	private void assertDetails(CredentialDetails<UserCredential> data) {
		assertCommonDetails(data);
		
		assertThat(data.getCredentialType()).isEqualTo(CredentialType.USER);
		assertThat(data.getValue().getUsername()).isEqualTo("myname");
		assertThat(data.getValue().getPassword()).isEqualTo("secret");
		assertThat(data.getValue().getPasswordHash()).isEqualTo("secret-hash");
	}
}
