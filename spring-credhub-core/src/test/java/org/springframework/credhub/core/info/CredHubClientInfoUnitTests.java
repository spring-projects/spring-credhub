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

package org.springframework.credhub.core.info;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.credhub.core.CredHubClient;
import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.support.info.VersionInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit tests of the info operations of {@link CredHubClient}, converted from the tests
 * for {@code CredHubInfoTemplate}.
 */
class CredHubClientInfoUnitTests {

	private static final String BASE_URL = "https://credhub.example.com:8844";

	private MockRestServiceServer server;

	private CredHubInfoOperations info;

	@BeforeEach
	void setUp() {
		this.info = CredHubClient.builder()
			.baseUrl(BASE_URL)
			.restClientCustomizer((builder) -> this.server = MockRestServiceServer.bindTo(builder).build())
			.build()
			.info();
	}

	@AfterEach
	void verifyExpectedRequestsWereMade() {
		this.server.verify();
	}

	@Test
	void getVersion() {
		this.server.expect(requestTo(BASE_URL + "/version"))
			.andExpect(method(HttpMethod.GET))
			.andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
			.andRespond(withSuccess("{\"version\":\"2.0.0\"}", MediaType.APPLICATION_JSON));

		VersionInfo response = this.info.version();

		assertThat(response.getVersion()).isEqualTo("2.0.0");
	}

	@Test
	void getVersionClientError() {
		this.server.expect(requestTo(BASE_URL + "/version"))
			.andRespond(withStatus(HttpStatus.NOT_FOUND).body("not found").contentType(MediaType.TEXT_PLAIN));

		assertThatExceptionOfType(CredHubException.class).isThrownBy(this.info::version).satisfies((ex) -> {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
			assertThat(ex.getResponseBodyAsByteArray()).isEqualTo("not found".getBytes(StandardCharsets.UTF_8));
		});
	}

	@Test
	void getVersionServerError() {
		this.server.expect(requestTo(BASE_URL + "/version")).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

		assertThatExceptionOfType(CredHubException.class).isThrownBy(this.info::version)
			.satisfies((ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR));
	}

}
