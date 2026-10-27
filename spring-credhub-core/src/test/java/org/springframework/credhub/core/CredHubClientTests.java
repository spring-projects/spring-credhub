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

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CredHubClientTests {

	private static final String BASE_URL = "https://credhub.example.com:8844";

	private final AtomicReference<MockRestServiceServer> server = new AtomicReference<>();

	private CredHubClient.Builder builder() {
		return CredHubClient.builder()
			.baseUrl(BASE_URL)
			.restClientCustomizer((builder) -> this.server.set(MockRestServiceServer.bindTo(builder).build()));
	}

	@Test
	void baseUrlIsRequired() {
		assertThatIllegalArgumentException().isThrownBy(() -> CredHubClient.builder().build())
			.withMessage("baseUrl must not be empty");
	}

	@Test
	void operationsAreProvidedAndStable() {
		CredHubClient client = builder().build();

		assertThat(client.credentials()).isNotNull().isSameAs(client.credentials());
		assertThat(client.permissions()).isNotNull().isSameAs(client.permissions());
		assertThat(client.permissionsV2()).isNotNull().isSameAs(client.permissionsV2());
		assertThat(client.certificates()).isNotNull().isSameAs(client.certificates());
		assertThat(client.interpolation()).isNotNull().isSameAs(client.interpolation());
		assertThat(client.info()).isNotNull().isSameAs(client.info());
	}

	@Test
	void restClientIsConfiguredForCredHub() {
		CredHubClient client = builder().build();
		this.server.get()
			.expect(requestTo(BASE_URL + "/version"))
			.andExpect(method(HttpMethod.GET))
			.andExpect(header("Accept", MediaType.APPLICATION_JSON_VALUE))
			.andRespond(withSuccess("{\"version\":\"2.0.0\"}", MediaType.APPLICATION_JSON));

		assertThat(client.restClient().get().uri("/version").retrieve().body(String.class))
			.isEqualTo("{\"version\":\"2.0.0\"}");
	}

	@Test
	void requestInterceptorIsApplied() {
		CredHubClient client = builder().requestInterceptor((request, body, execution) -> {
			request.getHeaders().add("X-Test", "value");
			return execution.execute(request, body);
		}).build();
		this.server.get()
			.expect(requestTo(BASE_URL + "/version"))
			.andExpect(header("X-Test", "value"))
			.andRespond(withSuccess("{\"version\":\"2.0.0\"}", MediaType.APPLICATION_JSON));

		assertThat(client.info().version().getVersion()).isEqualTo("2.0.0");
		this.server.get().verify();
	}

	@Test
	void requestFactoryIsUsed() {
		CredHubClient client = CredHubClient.builder().baseUrl(BASE_URL).requestFactory((uri, method) -> {
			throw new IllegalStateException("request factory used");
		}).build();

		assertThatIllegalStateException().isThrownBy(() -> client.info().version()).withMessage("request factory used");
	}

	@Test
	void errorResponsesAreTranslatedForEveryArea() {
		CredHubClient client = builder().build();
		this.server.get().expect(requestTo(BASE_URL + "/version")).andRespond(withStatus(HttpStatus.FORBIDDEN));

		assertThatExceptionOfType(CredHubException.class).isThrownBy(() -> client.info().version())
			.satisfies((ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
	}

}
