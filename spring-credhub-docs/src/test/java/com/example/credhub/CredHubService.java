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

		credentialName = new SimpleCredentialName("example", "password");
	}

	public String generatePassword() {
		PasswordParameters parameters = PasswordParameters.builder()
				.length(12)
				.excludeLower(false)
				.excludeUpper(false)
				.excludeNumber(false)
				.includeSpecial(true)
				.build();
		
		CredentialDetails<PasswordCredential> password = credHubOperations.credentials()
				.generate(PasswordParametersRequest.builder()
						.name(credentialName)
						.parameters(parameters)
						.build());

		return password.getValue().getPassword();
	}

	public String getPassword() {
		CredentialDetails<PasswordCredential> password = credHubOperations.credentials()
				.getByName(credentialName, PasswordCredential.class);

		return password.getValue().getPassword();
	}
}
