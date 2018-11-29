package com.example.credhub;

import org.springframework.credhub.core.ReactiveCredHubOperations;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.password.PasswordCredential;
import org.springframework.credhub.support.password.PasswordParameters;
import org.springframework.credhub.support.password.PasswordParametersRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactiveCredHubService {
	private final ReactiveCredHubOperations credHubOperations;
	private final SimpleCredentialName credentialName;

	public ReactiveCredHubService(ReactiveCredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;

		credentialName = new SimpleCredentialName("example", "password");
	}

	public Mono<String> generatePassword() {
		PasswordParameters parameters = PasswordParameters.builder()
				.length(12)
				.excludeLower(false)
				.excludeUpper(false)
				.excludeNumber(false)
				.includeSpecial(true)
				.build();

		return credHubOperations.credentials()
				.generate(PasswordParametersRequest.builder()
								.name(credentialName)
								.parameters(parameters)
								.build(),
						PasswordCredential.class)
				.map(password -> password.getValue().getPassword());
	}

	public Mono<String> getPassword() {
		return credHubOperations.credentials()
				.getByName(credentialName, PasswordCredential.class)
				.map(password -> password.getValue().getPassword());
	}
}
