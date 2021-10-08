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

package org.springframework.credhub.integration;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import reactor.test.StepVerifier;

import org.springframework.credhub.core.credential.ReactiveCredHubCredentialOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteMode;
import org.springframework.credhub.support.password.PasswordParameters;
import org.springframework.credhub.support.user.UserCredential;
import org.springframework.credhub.support.user.UserParametersRequest;
import org.springframework.credhub.support.value.ValueCredential;
import org.springframework.credhub.support.value.ValueCredentialRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;

public class ReactiveCredentialIntegrationTests extends ReactiveCredHubIntegrationTests {

	private static final SimpleCredentialName CREDENTIAL_NAME = new SimpleCredentialName("spring-credhub",
			"integration-test", "test-credential");

	private static final String CREDENTIAL_VALUE = "test-value";

	private ReactiveCredHubCredentialOperations credentials;

	private PasswordParameters.PasswordParametersBuilder passwordParameters;

	@Before
	public void setUp() {
		this.credentials = this.operations.credentials();

		this.passwordParameters = PasswordParameters.builder().length(12).excludeLower(false).excludeUpper(false)
				.excludeNumber(false).includeSpecial(true);

		deleteCredentialIfExists(CREDENTIAL_NAME);
	}

	@After
	public void tearDown() {
		deleteCredentialIfExists(CREDENTIAL_NAME);

		StepVerifier.create(this.credentials.findByName(CREDENTIAL_NAME)).expectComplete().verify();
	}

	@Test
	public void writeCredential() {
		AtomicReference<CredentialDetails<ValueCredential>> written = new AtomicReference<>();

		StepVerifier
				.create(this.credentials
						.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value(CREDENTIAL_VALUE).build()))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.VALUE);
					assertThat(response.getId()).isNotNull();

					written.set(response);
				}).verifyComplete();

		StepVerifier.create(this.credentials.getById(written.get().getId(), ValueCredential.class))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.VALUE);
				}).verifyComplete();

		StepVerifier.create(this.credentials.getByName(CREDENTIAL_NAME, ValueCredential.class))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.VALUE);
				}).verifyComplete();

		StepVerifier.create(this.credentials.findByName(new SimpleCredentialName("/test")))
				.assertNext((response) -> assertThat(response).extracting("name").extracting("name").asString()
						.contains(CREDENTIAL_NAME.getName()))
				.verifyComplete();

		StepVerifier.create(this.credentials.findByPath("/spring-credhub/integration-test"))
				.assertNext((response) -> assertThat(response).extracting("name").extracting("name").asString()
						.contains(CREDENTIAL_NAME.getName()))
				.verifyComplete();
	}

	@Test
	public void overwriteCredentialV2() {
		assumeTrue(serverApiIsV2());

		StepVerifier
				.create(this.credentials
						.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value(CREDENTIAL_VALUE).build()))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.VALUE);
					assertThat(response.getId()).isNotNull();
				}).verifyComplete();

		StepVerifier
				.create(this.credentials
						.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value("new-value").build()))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo("new-value");
				}).verifyComplete();

		verifyHistory();
	}

	@Test
	public void overwriteCredentialV1() {
		assumeTrue(serverApiIsV1());

		StepVerifier
				.create(this.credentials
						.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value(CREDENTIAL_VALUE).build()))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.VALUE);
					assertThat(response.getId()).isNotNull();
				}).verifyComplete();

		StepVerifier.create(this.credentials.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME)
				.value("new-value").mode(WriteMode.NO_OVERWRITE).build())).assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
				}).verifyComplete();

		StepVerifier.create(this.credentials.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME)
				.value("new-value").mode(WriteMode.OVERWRITE).build())).assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo("new-value");
				}).verifyComplete();

		verifyHistory();
	}

	private void verifyHistory() {
		StepVerifier.create(this.credentials.getByNameWithHistory(CREDENTIAL_NAME, ValueCredential.class))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo("new-value");
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.VALUE);
				}).assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.VALUE);
				}).verifyComplete();

		StepVerifier.create(this.credentials.getByNameWithHistory(CREDENTIAL_NAME, 2, ValueCredential.class))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo("new-value");
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.VALUE);
				}).assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.VALUE);
				}).verifyComplete();
	}

	@Test
	public void generateCredential() {
		AtomicReference<CredentialDetails<UserCredential>> generated = new AtomicReference<>();

		StepVerifier.create(this.credentials.generate(UserParametersRequest.builder().name(CREDENTIAL_NAME)
				.username("test-user").parameters(this.passwordParameters.build()).build(), UserCredential.class))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getCredentialType()).isEqualTo(CredentialType.USER);
					assertThat(response.getValue().getUsername()).isEqualTo("test-user");
					assertThat(response.getValue().getPassword()).matches("^[a-zA-Z0-9\\p{Punct}]{12}$");
					assertThat(response.getValue().getPasswordHash()).isNotNull();

					generated.set(response);
				}).verifyComplete();

		StepVerifier.create(this.credentials.getById(generated.get().getId(), UserCredential.class))
				.assertNext((response) -> assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName()))
				.verifyComplete();

		StepVerifier.create(this.credentials.regenerate(CREDENTIAL_NAME, UserCredential.class))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getUsername()).isEqualTo("test-user");
					assertThat(response.getValue().getPassword()).matches("^[a-zA-Z0-9\\p{Punct}]{12}$");
					assertThat(response.getValue().getPassword())
							.isNotEqualTo(generated.get().getValue().getPassword());
					assertThat(response.getValue().getPasswordHash())
							.isNotEqualTo(generated.get().getValue().getPasswordHash());
				}).verifyComplete();
	}

	@Test
	public void generateNoOverwriteCredential() {
		AtomicReference<CredentialDetails<UserCredential>> generated = new AtomicReference<>();

		StepVerifier.create(this.credentials.generate(UserParametersRequest.builder().name(CREDENTIAL_NAME)
				.username("test-user").parameters(this.passwordParameters.build()).build(), UserCredential.class))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getUsername()).isEqualTo("test-user");
					assertThat(response.getValue().getPassword()).matches("^[a-zA-Z0-9\\p{Punct}]{12}$");
					assertThat(response.getValue().getPasswordHash()).isNotNull();

					generated.set(response);
				}).verifyComplete();

		StepVerifier
				.create(this.credentials.generate(
						UserParametersRequest.builder().name(CREDENTIAL_NAME).mode(WriteMode.NO_OVERWRITE)
								.username("test-user").parameters(this.passwordParameters.build()).build(),
						UserCredential.class))
				.assertNext((response) -> {
					assertThat(response.getValue().getUsername()).isEqualTo("test-user");
					assertThat(response.getValue().getPassword()).isEqualTo(generated.get().getValue().getPassword());
					assertThat(response.getValue().getPasswordHash())
							.isEqualTo(generated.get().getValue().getPasswordHash());
				}).verifyComplete();
	}

	@Test
	public void generateOverwriteCredential() {
		AtomicReference<CredentialDetails<UserCredential>> generated = new AtomicReference<>();

		StepVerifier.create(this.credentials.generate(UserParametersRequest.builder().name(CREDENTIAL_NAME)
				.username("test-user").parameters(this.passwordParameters.build()).build(), UserCredential.class))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getUsername()).isEqualTo("test-user");
					assertThat(response.getValue().getPassword()).matches("^[a-zA-Z0-9\\p{Punct}]{12}$");
					assertThat(response.getValue().getPasswordHash()).isNotNull();

					generated.set(response);
				}).verifyComplete();

		StepVerifier
				.create(this.credentials.generate(
						UserParametersRequest.builder().name(CREDENTIAL_NAME).mode(WriteMode.OVERWRITE)
								.username("test-user").parameters(this.passwordParameters.build()).build(),
						UserCredential.class))
				.assertNext((response) -> {
					assertThat(response.getValue().getUsername()).isEqualTo("test-user");
					assertThat(response.getValue().getPassword())
							.isNotEqualTo(generated.get().getValue().getPassword());
					assertThat(response.getValue().getPasswordHash())
							.isNotEqualTo(generated.get().getValue().getPasswordHash());
				}).verifyComplete();
	}

	@Test
	public void generateConvergeCredential() {
		AtomicReference<CredentialDetails<UserCredential>> generated = new AtomicReference<>();

		StepVerifier.create(this.credentials.generate(UserParametersRequest.builder().name(CREDENTIAL_NAME)
				.username("test-user").parameters(this.passwordParameters.build()).build(), UserCredential.class))
				.assertNext((response) -> {
					assertThat(response.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
					assertThat(response.getValue().getUsername()).isEqualTo("test-user");
					assertThat(response.getValue().getPassword()).matches("^[a-zA-Z0-9\\p{Punct}]{12}$");
					assertThat(response.getValue().getPasswordHash()).isNotNull();

					generated.set(response);
				}).verifyComplete();

		StepVerifier
				.create(this.credentials.generate(
						UserParametersRequest.builder().name(CREDENTIAL_NAME).mode(WriteMode.CONVERGE)
								.username("test-user").parameters(this.passwordParameters.build()).build(),
						UserCredential.class))
				.assertNext((response) -> {
					assertThat(response.getValue().getUsername()).isEqualTo("test-user");
					assertThat(response.getValue().getPassword()).isEqualTo(generated.get().getValue().getPassword());
					assertThat(response.getValue().getPasswordHash())
							.isEqualTo(generated.get().getValue().getPasswordHash());
				}).verifyComplete();

		this.passwordParameters.includeSpecial(false);

		StepVerifier
				.create(this.credentials.generate(
						UserParametersRequest.builder().name(CREDENTIAL_NAME).mode(WriteMode.CONVERGE)
								.username("test-user").parameters(this.passwordParameters.build()).build(),
						UserCredential.class))
				.assertNext((response) -> {
					assertThat(response.getValue().getUsername()).isEqualTo("test-user");
					assertThat(response.getValue().getPassword())
							.isNotEqualTo(generated.get().getValue().getPassword());
					assertThat(response.getValue().getPasswordHash())
							.isNotEqualTo(generated.get().getValue().getPasswordHash());
				}).verifyComplete();
	}

}
