/*
 * Copyright 2016-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.credhub;

import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.password.PasswordCredential;
import org.springframework.credhub.support.password.PasswordParameters;
import org.springframework.credhub.support.password.PasswordParametersRequest;
import org.springframework.stereotype.Component;

@Component
public class CredHubService {

	private final CredHubOperations credHubOperations;

	private final SimpleCredentialName credentialName;

	public CredHubService(CredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;

		this.credentialName = new SimpleCredentialName("example", "password");
	}

	public String generatePassword() {
		PasswordParameters parameters = PasswordParameters.builder()
			.length(12)
			.excludeLower(false)
			.excludeUpper(false)
			.excludeNumber(false)
			.includeSpecial(true)
			.build();

		CredentialDetails<PasswordCredential> password = this.credHubOperations.credentials()
			.generate(PasswordParametersRequest.builder().name(this.credentialName).parameters(parameters).build());

		return password.getValue().getPassword();
	}

	public String getPassword() {
		CredentialDetails<PasswordCredential> password = this.credHubOperations.credentials()
			.getByName(this.credentialName, PasswordCredential.class);

		return password.getValue().getPassword();
	}

}
