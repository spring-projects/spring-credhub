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

import com.fasterxml.jackson.annotation.JsonValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.client.ResponseCreator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Base class for unit tests of {@link CredHubClient}. These are conversions of the tests
 * for the {@code RestTemplate}-based templates: the mocked {@code RestTemplate} is
 * replaced with a {@link MockRestServiceServer}, so each test asserts the request that
 * goes over the wire (method, URL, and body) and feeds back a real HTTP response.
 *
 * @author Roy Clarkson
 */
public abstract class CredHubClientUnitTestsBase {

	protected static final String BASE_URL = "https://credhub.example.com:8844";

	/**
	 * The mapper the client uses; it is also used here to build the expected request
	 * bodies.
	 */
	protected static final JsonMapper MAPPER = JsonUtils.buildJsonMapper();

	/**
	 * A mapper for building the responses the mock server returns. The client only ever
	 * deserializes responses, so {@link CredentialName} has no serializer of its own; the
	 * mix-in writes it as the plain string CredHub uses on the wire.
	 */
	private static final JsonMapper RESPONSE_MAPPER = JsonUtils.buildJsonMapper()
		.rebuild()
		.addMixIn(CredentialName.class, CredentialNameMixin.class)
		.build();

	protected MockRestServiceServer server;

	protected CredHubClient client;

	@BeforeEach
	protected void setUpCredHubClient() {
		this.client = CredHubClient.builder()
			.baseUrl(BASE_URL)
			.restClientCustomizer((builder) -> this.server = MockRestServiceServer.bindTo(builder).build())
			.build();
	}

	@AfterEach
	protected void verifyExpectedRequestsWereMade() {
		this.server.verify();
	}

	/**
	 * The status line a {@code CredHubException} message starts with, such as
	 * {@code 404 Not Found}.
	 */
	protected static String statusLine(HttpStatusCode status) {
		return status.value() + " " + HttpStatus.valueOf(status.value()).getReasonPhrase();
	}

	protected static String json(Object value) {
		return MAPPER.writeValueAsString(value);
	}

	/**
	 * Match a request body that is JSON-equivalent to the expected object, ignoring the
	 * order of properties.
	 */
	protected static RequestMatcher jsonBody(Object expected) {
		JsonNode expectedTree = MAPPER.readTree(json(expected));
		return (request) -> {
			String body = ((MockClientHttpRequest) request).getBodyAsString();
			assertThat(MAPPER.readTree(body)).isEqualTo(expectedTree);
		};
	}

	/**
	 * Create a {@code 200 OK} response with the body serialized as JSON.
	 */
	protected static ResponseCreator respondWith(Object body) {
		return withSuccess(RESPONSE_MAPPER.writeValueAsString(body), MediaType.APPLICATION_JSON);
	}

	/**
	 * Create the response the mock server returns: the status of the expected response,
	 * with the body serialized as JSON for successful statuses.
	 */
	protected static ResponseCreator respond(ResponseEntity<?> expected) {
		HttpStatusCode status = expected.getStatusCode();
		if (status.is2xxSuccessful()) {
			return withStatus(status).contentType(MediaType.APPLICATION_JSON)
				.body(RESPONSE_MAPPER.writeValueAsString(expected.getBody()));
		}
		return withStatus(status);
	}

	private abstract static class CredentialNameMixin {

		@JsonValue
		abstract String getName();

	}

}
