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

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.credhub.core.credential.CredHubCredentialOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteMode;
import org.springframework.credhub.support.password.PasswordParameters;
import org.springframework.credhub.support.user.UserCredential;
import org.springframework.credhub.support.user.UserParametersRequest;
import org.springframework.credhub.support.value.ValueCredential;
import org.springframework.credhub.support.value.ValueCredentialRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class CredentialIntegrationTests extends CredHubIntegrationTests {

	private static final SimpleCredentialName CREDENTIAL_NAME = new SimpleCredentialName("spring-credhub",
			"integration-test", "test-credential");

	private static final String CREDENTIAL_VALUE = "test-value";

	private CredHubCredentialOperations credentials;

	private PasswordParameters.PasswordParametersBuilder passwordParameters;

	@BeforeEach
	public void setUp() {
		this.credentials = this.operations.credentials();

		this.passwordParameters = PasswordParameters.builder().length(12).excludeLower(false).excludeUpper(false)
				.excludeNumber(false).includeSpecial(true);

		deleteCredentialIfExists(CREDENTIAL_NAME);
	}

	@AfterEach
	public void tearDown() {
		deleteCredentialIfExists(CREDENTIAL_NAME);

		List<CredentialSummary> afterDelete = this.credentials.findByName(CREDENTIAL_NAME);
		assertThat(afterDelete).hasSize(0);
	}

	@Test
	public void writeCredential() {
		CredentialDetails<ValueCredential> written = this.credentials
				.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value(CREDENTIAL_VALUE).build());
		assertThat(written.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(written.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
		assertThat(written.getCredentialType()).isEqualTo(CredentialType.VALUE);
		assertThat(written.getId()).isNotNull();

		CredentialDetails<ValueCredential> byId = this.credentials.getById(written.getId(), ValueCredential.class);
		assertThat(byId.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(byId.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
		assertThat(byId.getCredentialType()).isEqualTo(CredentialType.VALUE);

		CredentialDetails<ValueCredential> byName = this.credentials.getByName(CREDENTIAL_NAME, ValueCredential.class);
		assertThat(byName.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(byName.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
		assertThat(byName.getCredentialType()).isEqualTo(CredentialType.VALUE);

		List<CredentialSummary> foundByName = this.credentials.findByName(new SimpleCredentialName("/test"));
		assertThat(foundByName).hasSize(1);
		assertThat(foundByName).extracting("name").extracting("name").containsExactly(CREDENTIAL_NAME.getName());

		List<CredentialSummary> foundByPath = this.credentials.findByPath("/spring-credhub/integration-test");
		assertThat(foundByPath).hasSize(1);
		assertThat(foundByPath).extracting("name").extracting("name").containsExactly(CREDENTIAL_NAME.getName());
	}

	@Test
	public void overwriteCredentialV2() {
		assumeTrue(serverApiIsV2());

		CredentialDetails<ValueCredential> written = this.credentials
				.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value(CREDENTIAL_VALUE).build());
		assertThat(written.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(written.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
		assertThat(written.getCredentialType()).isEqualTo(CredentialType.VALUE);
		assertThat(written.getId()).isNotNull();

		CredentialDetails<ValueCredential> overwritten = this.credentials
				.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value("new-value").build());
		assertThat(overwritten.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(overwritten.getValue().getValue()).isEqualTo("new-value");

		verifyHistory();
	}

	@Test
	public void overwriteCredentialV1() {
		assumeTrue(serverApiIsV1());

		CredentialDetails<ValueCredential> written = this.credentials
				.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value(CREDENTIAL_VALUE).build());
		assertThat(written.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(written.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
		assertThat(written.getCredentialType()).isEqualTo(CredentialType.VALUE);
		assertThat(written.getId()).isNotNull();

		CredentialDetails<ValueCredential> overwritten = this.credentials.write(ValueCredentialRequest.builder()
				.name(CREDENTIAL_NAME).value("new-value").mode(WriteMode.NO_OVERWRITE).build());
		assertThat(overwritten.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(overwritten.getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);

		overwritten = this.credentials.write(ValueCredentialRequest.builder().name(CREDENTIAL_NAME).value("new-value")
				.mode(WriteMode.OVERWRITE).build());
		assertThat(overwritten.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(overwritten.getValue().getValue()).isEqualTo("new-value");

		verifyHistory();
	}

	private void verifyHistory() {
		List<CredentialDetails<ValueCredential>> history = this.credentials.getByNameWithHistory(CREDENTIAL_NAME,
				ValueCredential.class);
		assertThat(history).hasSize(2);
		assertThat(history.get(0).getName()).isEqualTo(CREDENTIAL_NAME);
		assertThat(history.get(0).getValue().getValue()).isEqualTo("new-value");
		assertThat(history.get(1).getName()).isEqualTo(CREDENTIAL_NAME);
		assertThat(history.get(1).getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);

		history = this.credentials.getByNameWithHistory(CREDENTIAL_NAME, 2, ValueCredential.class);
		assertThat(history).hasSize(2);
		assertThat(history.get(0).getName()).isEqualTo(CREDENTIAL_NAME);
		assertThat(history.get(0).getValue().getValue()).isEqualTo("new-value");
		assertThat(history.get(1).getName()).isEqualTo(CREDENTIAL_NAME);
		assertThat(history.get(1).getValue().getValue()).isEqualTo(CREDENTIAL_VALUE);
	}

	@Test
	public void generateCredential() {
		CredentialDetails<UserCredential> generated = this.credentials.generate(UserParametersRequest.builder()
				.name(CREDENTIAL_NAME).username("test-user").parameters(this.passwordParameters.build()).build());
		assertThat(generated.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(generated.getCredentialType()).isEqualTo(CredentialType.USER);
		assertThat(generated.getValue().getUsername()).isEqualTo("test-user");
		assertThat(generated.getValue().getPassword()).matches("^[a-zA-Z0-9\\p{Punct}]{12}$");
		assertThat(generated.getValue().getPasswordHash()).isNotNull();

		CredentialDetails<UserCredential> retrieved = this.credentials.getById(generated.getId(), UserCredential.class);
		assertThat(retrieved.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());

		CredentialDetails<UserCredential> regenerated = this.credentials.regenerate(CREDENTIAL_NAME,
				UserCredential.class);
		assertThat(regenerated.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(regenerated.getValue().getUsername()).isEqualTo("test-user");
		assertThat(regenerated.getValue().getPassword()).matches("^[a-zA-Z0-9\\p{Punct}]{12}$");
		assertThat(regenerated.getValue().getPassword()).isNotEqualTo(generated.getValue().getPassword());
		assertThat(regenerated.getValue().getPasswordHash()).isNotEqualTo(generated.getValue().getPasswordHash());
	}

	@Test
	public void generateNoOverwriteCredential() {
		CredentialDetails<UserCredential> generated = this.credentials.generate(UserParametersRequest.builder()
				.name(CREDENTIAL_NAME).username("test-user").parameters(this.passwordParameters.build()).build());
		assertThat(generated.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(generated.getValue().getUsername()).isEqualTo("test-user");
		assertThat(generated.getValue().getPassword()).matches("^[a-zA-Z0-9\\p{Punct}]{12}$");
		assertThat(generated.getValue().getPasswordHash()).isNotNull();

		CredentialDetails<UserCredential> noOverwrite = this.credentials
				.generate(UserParametersRequest.builder().name(CREDENTIAL_NAME).mode(WriteMode.NO_OVERWRITE)
						.username("test-user").parameters(this.passwordParameters.build()).build());
		assertThat(noOverwrite.getValue().getUsername()).isEqualTo("test-user");
		assertThat(noOverwrite.getValue().getPassword()).isEqualTo(generated.getValue().getPassword());
		assertThat(noOverwrite.getValue().getPasswordHash()).isEqualTo(generated.getValue().getPasswordHash());
	}

	@Test
	public void generateOverwriteCredential() {
		CredentialDetails<UserCredential> generated = this.credentials.generate(UserParametersRequest.builder()
				.name(CREDENTIAL_NAME).username("test-user").parameters(this.passwordParameters.build()).build());
		assertThat(generated.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(generated.getValue().getUsername()).isEqualTo("test-user");
		assertThat(generated.getValue().getPassword()).matches("^[a-zA-Z0-9\\p{Punct}]{12}$");
		assertThat(generated.getValue().getPasswordHash()).isNotNull();

		CredentialDetails<UserCredential> overwrite = this.credentials
				.generate(UserParametersRequest.builder().name(CREDENTIAL_NAME).mode(WriteMode.OVERWRITE)
						.username("test-user").parameters(this.passwordParameters.build()).build());
		assertThat(overwrite.getValue().getUsername()).isEqualTo("test-user");
		assertThat(overwrite.getValue().getPassword()).isNotEqualTo(generated.getValue().getPassword());
		assertThat(overwrite.getValue().getPasswordHash()).isNotEqualTo(generated.getValue().getPasswordHash());
	}

	@Test
	public void generateConvergeCredential() {
		CredentialDetails<UserCredential> generated = this.credentials.generate(UserParametersRequest.builder()
				.name(CREDENTIAL_NAME).username("test-user").parameters(this.passwordParameters.build()).build());
		assertThat(generated.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(generated.getValue().getUsername()).isEqualTo("test-user");
		assertThat(generated.getValue().getPassword()).matches("^[a-zA-Z0-9\\p{Punct}]{12}$");
		assertThat(generated.getValue().getPasswordHash()).isNotNull();

		CredentialDetails<UserCredential> convergeWithoutChanges = this.credentials
				.generate(UserParametersRequest.builder().name(CREDENTIAL_NAME).mode(WriteMode.CONVERGE)
						.username("test-user").parameters(this.passwordParameters.build()).build());
		assertThat(convergeWithoutChanges.getValue().getUsername()).isEqualTo("test-user");
		assertThat(convergeWithoutChanges.getValue().getPassword()).isEqualTo(generated.getValue().getPassword());
		assertThat(convergeWithoutChanges.getValue().getPasswordHash())
				.isEqualTo(generated.getValue().getPasswordHash());

		this.passwordParameters.includeSpecial(false);

		CredentialDetails<UserCredential> convergeWithChanges = this.credentials
				.generate(UserParametersRequest.builder().name(CREDENTIAL_NAME).mode(WriteMode.CONVERGE)
						.username("test-user").parameters(this.passwordParameters.build()).build());
		assertThat(convergeWithChanges.getValue().getUsername()).isEqualTo("test-user");
		assertThat(convergeWithChanges.getValue().getPassword()).isNotEqualTo(generated.getValue().getPassword());
		assertThat(convergeWithChanges.getValue().getPasswordHash())
				.isNotEqualTo(generated.getValue().getPasswordHash());
	}

}
