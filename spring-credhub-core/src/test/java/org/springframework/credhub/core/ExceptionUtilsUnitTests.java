/*
 * Copyright 2016-2026 the original author or authors.
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

package org.springframework.credhub.core;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ExceptionUtilsUnitTests {

	@Test
	void throwExceptionOnErrorPreservesStatusCode() {
		ResponseEntity<String> response = ResponseEntity.status(HttpStatus.FORBIDDEN).body("access denied");

		assertThatExceptionOfType(CredHubException.class)
			.isThrownBy(() -> ExceptionUtils.throwExceptionOnError(response))
			.satisfies((ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
	}

	@Test
	void throwExceptionOnErrorDoesNothingForOk() {
		ResponseEntity<String> response = ResponseEntity.ok("success");

		ExceptionUtils.throwExceptionOnError(response);
	}

	@Test
	void buildErrorPreservesStatusCodeAndResponseBody() {
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.FORBIDDEN)
			.body("{\"error\":\"access denied\"}")
			.build();

		Mono<Throwable> error = ExceptionUtils.buildError(clientResponse);

		StepVerifier.create(error).assertNext((ex) -> {
			assertThat(ex).isInstanceOf(CredHubException.class);
			CredHubException credHubException = (CredHubException) ex;
			assertThat(credHubException.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
			assertThat(credHubException.getResponseBodyAsString()).isEqualTo("{\"error\":\"access denied\"}");
		}).verifyComplete();
	}

	@Test
	void buildErrorHandlesEmptyBody() {
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build();

		Mono<Throwable> error = ExceptionUtils.buildError(clientResponse);

		StepVerifier.create(error).assertNext((ex) -> {
			assertThat(ex).isInstanceOf(CredHubException.class);
			CredHubException credHubException = (CredHubException) ex;
			assertThat(credHubException.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
			assertThat(credHubException.getResponseBodyAsString()).isEmpty();
		}).verifyComplete();
	}

}
