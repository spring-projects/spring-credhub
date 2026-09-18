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

package org.springframework.credhub.core.certificate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.credhub.core.ReactiveCredHubTemplate;
import org.springframework.credhub.support.utils.JsonUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReactiveCredHubCertificateTemplateUnitTests {

	private static final ExchangeStrategies EXCHANGE_STRATEGIES = ExchangeStrategies.builder().codecs((configurer) -> {
		JsonMapper mapper = JsonUtils.buildJsonMapper();
		configurer.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(mapper));
		configurer.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(mapper));
	}).build();

	@Mock
	private ExchangeFunction exchangeFunction;

	private ReactiveCredHubCertificateOperations credHubTemplate;

	@BeforeEach
	void setUp() {
		WebClient webClient = WebClient.builder()
			.baseUrl("https://example.com")
			.exchangeStrategies(EXCHANGE_STRATEGIES)
			.exchangeFunction(this.exchangeFunction)
			.build();

		this.credHubTemplate = new ReactiveCredHubTemplate(webClient).certificates();
	}

	@Test
	void getVersions() {
		String responseBody = """
				[
					{
						"type": "certificate",
						"transitional": false,
						"id": "id1",
						"name": "/example/certificate",
						"value": { "certificate": "cert1", "ca": "authority1", "private_key": "key1" }
					},
					{
						"type": "certificate",
						"transitional": true,
						"id": "id2",
						"name": "/example/certificate",
						"value": { "certificate": "cert2", "ca": "authority2", "private_key": "key2" }
					}
				]
				""";

		given(this.exchangeFunction.exchange(argThat(isGetRequestTo("/api/v1/certificates/id1/versions"))))
			.willReturn(Mono.just(ClientResponse.create(HttpStatus.OK, EXCHANGE_STRATEGIES)
				.header("Content-Type", "application/json")
				.body(responseBody)
				.build()));

		StepVerifier.create(this.credHubTemplate.getVersions("id1")).assertNext((response) -> {
			assertThat(response.getId()).isEqualTo("id1");
			assertThat(response.isTransitional()).isFalse();
			assertThat(response.getValue().getCertificate()).isEqualTo("cert1");
		}).assertNext((response) -> {
			assertThat(response.getId()).isEqualTo("id2");
			assertThat(response.isTransitional()).isTrue();
			assertThat(response.getValue().getCertificate()).isEqualTo("cert2");
		}).verifyComplete();
	}

	private static ArgumentMatcher<ClientRequest> isGetRequestTo(String path) {
		return (request) -> request.method() == HttpMethod.GET && request.url().getPath().equals(path);
	}

}
