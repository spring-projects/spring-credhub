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

package org.springframework.credhub.core.permissionV2;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.core.ReactiveCredHubTemplate;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.JsonTestUtils;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;
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
class ReactiveCredHubPermissionV2TemplateUnitTests {

	private static final SimpleCredentialName PATH = new SimpleCredentialName("example", "credential", "*");

	private static final ExchangeStrategies EXCHANGE_STRATEGIES = ExchangeStrategies.builder().codecs((configurer) -> {
		JsonMapper mapper = JsonUtils.buildJsonMapper();
		configurer.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(mapper));
		configurer.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(mapper));
	}).build();

	@Mock
	private ExchangeFunction exchangeFunction;

	private ReactiveCredHubPermissionV2Operations credHubTemplate;

	@BeforeEach
	void setUp() {
		WebClient webClient = WebClient.builder()
			.baseUrl("https://example.com")
			.exchangeStrategies(EXCHANGE_STRATEGIES)
			.exchangeFunction(this.exchangeFunction)
			.build();

		this.credHubTemplate = new ReactiveCredHubTemplate(webClient).permissionsV2();
	}

	@Test
	void getPermissions() {
		String permissionId = "uuid";

		CredentialPermission expectedResponse = new CredentialPermission(PATH,
				Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE).build());

		given(this.exchangeFunction.exchange(argThat(isGetRequestTo("/api/v2/permissions/" + permissionId))))
			.willReturn(Mono.just(ClientResponse.create(HttpStatus.OK, EXCHANGE_STRATEGIES)
				.header("Content-Type", "application/json")
				.body(JsonTestUtils.toJson(expectedResponse))
				.build()));

		StepVerifier.create(this.credHubTemplate.getPermissions(permissionId))
			.assertNext((response) -> assertThat(response.getPath()).isEqualTo(PATH.getName()))
			.verifyComplete();
	}

	@Test
	void getPermissionsHandlesErrorStatus() {
		String permissionId = "uuid";

		given(this.exchangeFunction.exchange(argThat(isGetRequestTo("/api/v2/permissions/" + permissionId))))
			.willReturn(Mono.just(ClientResponse.create(HttpStatus.NOT_FOUND, EXCHANGE_STRATEGIES).build()));

		StepVerifier.create(this.credHubTemplate.getPermissions(permissionId))
			.expectError(CredHubException.class)
			.verify();
	}

	@Test
	void patchPermissions() {
		String permissionId = "uuid";
		List<Operation> operations = Arrays.asList(Operation.READ, Operation.WRITE);

		CredentialPermission expectedResponse = new CredentialPermission(PATH,
				Permission.builder().app("app-id").operation(Operation.READ).operation(Operation.WRITE).build());

		given(this.exchangeFunction.exchange(argThat(isPatchRequestTo("/api/v2/permissions/" + permissionId))))
			.willReturn(Mono.just(ClientResponse.create(HttpStatus.OK, EXCHANGE_STRATEGIES)
				.header("Content-Type", "application/json")
				.body(JsonTestUtils.toJson(expectedResponse))
				.build()));

		StepVerifier.create(this.credHubTemplate.patchPermissions(permissionId, operations))
			.assertNext((response) -> assertThat(response.getPath()).isEqualTo(PATH.getName()))
			.verifyComplete();
	}

	@Test
	void patchPermissionsHandlesErrorStatus() {
		String permissionId = "uuid";
		List<Operation> operations = Arrays.asList(Operation.READ, Operation.WRITE);

		given(this.exchangeFunction.exchange(argThat(isPatchRequestTo("/api/v2/permissions/" + permissionId))))
			.willReturn(Mono.just(ClientResponse.create(HttpStatus.NOT_FOUND, EXCHANGE_STRATEGIES).build()));

		StepVerifier.create(this.credHubTemplate.patchPermissions(permissionId, operations))
			.expectError(CredHubException.class)
			.verify();
	}

	private static ArgumentMatcher<ClientRequest> isGetRequestTo(String path) {
		return (request) -> request.method() == HttpMethod.GET && request.url().getPath().equals(path);
	}

	private static ArgumentMatcher<ClientRequest> isPatchRequestTo(String path) {
		return (request) -> request.method() == HttpMethod.PATCH && request.url().getPath().equals(path);
	}

}
