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

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.credhub.support.info.VersionInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests the {@link RestClient} configuration shared by every {@link CredHubClient}: the
 * base URL, the headers sent on every request, and the message converters.
 */
class CredHubRestClientFactoryUnitTests {

	private static final String BASE_URL = "https://credhub.example.com:8844";

	private final RestClient.Builder builder = CredHubRestClientFactory.builder(BASE_URL);

	private final MockRestServiceServer server = MockRestServiceServer.bindTo(this.builder).build();

	private final RestClient restClient = this.builder.build();

	@Test
	void baseUrlIsRequired() {
		assertThatIllegalArgumentException().isThrownBy(() -> CredHubRestClientFactory.builder(""))
			.withMessage("baseUrl must not be empty");
		assertThatIllegalArgumentException().isThrownBy(() -> CredHubRestClientFactory.builder(null))
			.withMessage("baseUrl must not be empty");
	}

	@Test
	void relativeUrisResolveAgainstTheBaseUrl() {
		this.server.expect(requestTo(BASE_URL + "/info")).andRespond(withSuccess());

		this.restClient.get().uri("/info").retrieve().toBodilessEntity();

		this.server.verify();
	}

	@Test
	void acceptAndContentTypeAreJsonOnGetRequests() {
		this.server.expect(requestTo(BASE_URL + "/info"))
			.andExpect(method(HttpMethod.GET))
			.andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
			.andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
			.andRespond(withSuccess());

		this.restClient.get().uri("/info").retrieve().toBodilessEntity();

		this.server.verify();
	}

	@Test
	void acceptAndContentTypeAreJsonOnRequestsWithABody() {
		this.server.expect(requestTo(BASE_URL + "/data"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
			.andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
			.andRespond(withSuccess());

		this.restClient.post().uri("/data").body("{}").retrieve().toBodilessEntity();

		this.server.verify();
	}

	@Test
	void requestBodiesUseSnakeCaseAndOmitEmptyValues() {
		this.server.expect(requestTo(BASE_URL + "/data"))
			.andExpect(content().string("{\"some_value\":\"x\"}"))
			.andRespond(withSuccess());

		this.restClient.post().uri("/data").body(new Example("x", List.of(), "")).retrieve().toBodilessEntity();

		this.server.verify();
	}

	@Test
	void unknownResponsePropertiesAreIgnored() {
		this.server.expect(requestTo(BASE_URL + "/version"))
			.andRespond(withSuccess("{\"version\":\"2.0.0\",\"unexpected\":true}", MediaType.APPLICATION_JSON));

		VersionInfo version = this.restClient.get().uri("/version").retrieve().body(VersionInfo.class);

		assertThat(version.getVersion()).isEqualTo("2.0.0");
	}

	@Test
	void stringResponsesAreReadAsText() {
		this.server.expect(requestTo(BASE_URL + "/text")).andRespond(withSuccess("plain text", MediaType.TEXT_PLAIN));

		assertThat(this.restClient.get().uri("/text").retrieve().body(String.class)).isEqualTo("plain text");
	}

	@Test
	void byteArrayResponsesAreReadAsBytes() {
		byte[] bytes = "bytes".getBytes(StandardCharsets.UTF_8);
		this.server.expect(requestTo(BASE_URL + "/bytes"))
			.andRespond(withSuccess(bytes, MediaType.APPLICATION_OCTET_STREAM));

		assertThat(this.restClient.get().uri("/bytes").retrieve().body(byte[].class)).isEqualTo(bytes);
	}

	private record Example(String someValue, List<String> items, String blank) {

	}

}
