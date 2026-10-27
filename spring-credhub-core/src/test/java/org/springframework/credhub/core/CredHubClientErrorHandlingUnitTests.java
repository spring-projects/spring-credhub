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

import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.value.ValueCredential;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * Tests the translation of error responses to {@link CredHubException}. These pin down
 * the behavior of the {@code RestTemplate}-based client: the status text is the reason
 * phrase from the response, and any non-2xx response is an error, not only 4xx and 5xx.
 */
class CredHubClientErrorHandlingUnitTests extends CredHubClientUnitTestsBase {

	private static final SimpleCredentialName NAME = new SimpleCredentialName("example", "credential");

	@Test
	void clientErrorKeepsTheReasonPhrase() {
		this.server.expect(anything())
			.andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
				.body("{\"error\":\"missing\"}"));

		assertThatExceptionOfType(CredHubException.class)
			.isThrownBy(() -> this.client.credentials().getByName(NAME, ValueCredential.class))
			.satisfies((ex) -> {
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
				assertThat(ex.getStatusText()).isEqualTo("Not Found");
				assertThat(ex.getMessage()).startsWith("404 Not Found");
				assertThat(ex.getResponseBodyAsString()).isEqualTo("{\"error\":\"missing\"}");
			});
	}

	@Test
	void serverErrorKeepsTheReasonPhrase() {
		this.server.expect(anything()).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		assertThatExceptionOfType(CredHubException.class).isThrownBy(() -> this.client.info().version())
			.satisfies((ex) -> {
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
				assertThat(ex.getStatusText()).isEqualTo("Internal Server Error");
				assertThat(ex.getMessage()).startsWith("500 Internal Server Error");
			});
	}

	@Test
	void headersAndBodyArePreserved() {
		this.server.expect(anything())
			.andRespond(withStatus(HttpStatus.FORBIDDEN).header("X-Test", "value").body("denied"));

		assertThatExceptionOfType(CredHubException.class).isThrownBy(() -> this.client.info().version())
			.satisfies((ex) -> {
				HttpHeaders headers = ex.getResponseHeaders();
				assertThat(headers).isNotNull();
				assertThat(headers.getFirst("X-Test")).isEqualTo("value");
				assertThat(ex.getResponseBodyAsString()).isEqualTo("denied");
			});
	}

	@Test
	void unknownErrorStatusCodeIsTranslated() {
		this.server.expect(anything()).andRespond(withStatus(HttpStatusCode.valueOf(599)));

		assertThatExceptionOfType(CredHubException.class).isThrownBy(() -> this.client.info().version())
			.satisfies((ex) -> assertThat(ex.getStatusCode().value()).isEqualTo(599));
	}

	@Test
	void redirectWithAnEmptyBodyIsAnError() {
		this.server.expect(anything())
			.andRespond(withStatus(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "https://login.example.com/"));

		assertThatExceptionOfType(CredHubException.class)
			.isThrownBy(() -> this.client.credentials().getByName(NAME, ValueCredential.class))
			.satisfies((ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FOUND));
	}

	@Test
	void redirectToAnHtmlPageIsAnError() {
		this.server.expect(anything())
			.andRespond(withStatus(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "https://login.example.com/")
				.contentType(MediaType.TEXT_HTML)
				.body("<html>login</html>"));

		assertThatExceptionOfType(CredHubException.class)
			.isThrownBy(() -> this.client.credentials().getByName(NAME, ValueCredential.class))
			.satisfies((ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FOUND));
	}

	@Test
	void redirectIsAnErrorForOperationsWithoutAResponseBody() {
		this.server.expect(anything())
			.andRespond(withStatus(HttpStatus.TEMPORARY_REDIRECT).header(HttpHeaders.LOCATION,
					"https://login.example.com/"));

		assertThatExceptionOfType(CredHubException.class).isThrownBy(() -> this.client.credentials().deleteByName(NAME))
			.satisfies((ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.TEMPORARY_REDIRECT));
	}

}
