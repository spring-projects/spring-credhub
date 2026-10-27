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

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit tests of the OAuth2 support of {@link CredHubClient}.
 */
class CredHubClientOAuth2UnitTests {

	private static final String BASE_URL = "https://credhub.example.com:8844";

	private static final String TOKEN_URI = "https://uaa.example.com/oauth/token";

	private static final String REGISTRATION_ID = "credhub-client";

	private final ClientRegistration registration = ClientRegistration.withRegistrationId(REGISTRATION_ID)
		.clientId("client-id")
		.clientSecret("client-secret")
		.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
		.tokenUri(TOKEN_URI)
		.build();

	private final ClientRegistrationRepository registrations = new InMemoryClientRegistrationRepository(
			this.registration);

	private final AtomicReference<MockRestServiceServer> server = new AtomicReference<>();

	private CredHubClient.Builder builder() {
		return CredHubClient.builder()
			.baseUrl(BASE_URL)
			.restClientCustomizer((builder) -> this.server.set(MockRestServiceServer.bindTo(builder).build()));
	}

	@Test
	void requestsAreAuthorizedWithTheTokenFromTheClientManager() {
		OAuth2AuthorizedClientManager clientManager = mock(OAuth2AuthorizedClientManager.class);
		given(clientManager.authorize(any())).willReturn(authorizedClient("token-value"));
		CredHubClient client = builder().oauth2(REGISTRATION_ID, this.registrations, clientManager).build();
		this.server.get()
			.expect(requestTo(BASE_URL + "/version"))
			.andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-value"))
			.andRespond(withSuccess("{\"version\":\"2.0.0\"}", MediaType.APPLICATION_JSON));

		assertThat(client.info().version().getVersion()).isEqualTo("2.0.0");

		ArgumentCaptor<OAuth2AuthorizeRequest> authorizeRequest = ArgumentCaptor.forClass(OAuth2AuthorizeRequest.class);
		verify(clientManager).authorize(authorizeRequest.capture());
		assertThat(authorizeRequest.getValue().getClientRegistrationId()).isEqualTo(REGISTRATION_ID);
		assertThat(authorizeRequest.getValue().getPrincipal().getName()).isEqualTo("client-id");
		this.server.get().verify();
	}

	@Test
	void tokenIsObtainedFromTheTokenServerWithTheClientCredentialsGrant() {
		AtomicReference<MockClientHttpRequest> tokenRequest = new AtomicReference<>();
		ClientHttpRequestFactory tokenServer = tokenServer(tokenRequest, "issued-token");
		OAuth2AuthorizedClientRepository authorizedClients = new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(
				new InMemoryOAuth2AuthorizedClientService(this.registrations));
		CredHubClient client = builder().requestFactory(tokenServer)
			.oauth2(REGISTRATION_ID, this.registrations, authorizedClients)
			.build();
		this.server.get()
			.expect(requestTo(BASE_URL + "/version"))
			.andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer issued-token"))
			.andRespond(withSuccess("{\"version\":\"2.0.0\"}", MediaType.APPLICATION_JSON));

		// the manager created from a repository is Spring Security's servlet-based one,
		// which needs a current request and response; the integration tests get them
		// from the Spring test context
		RequestContextHolder.setRequestAttributes(
				new ServletRequestAttributes(new MockHttpServletRequest(), new MockHttpServletResponse()));
		try {
			assertThat(client.info().version().getVersion()).isEqualTo("2.0.0");
		}
		finally {
			RequestContextHolder.resetRequestAttributes();
		}

		assertTokenRequest(tokenRequest.get());
		this.server.get().verify();
	}

	@Test
	void tokenIsObtainedWithoutACurrentRequestWhenUsingAnAuthorizedClientService() {
		AtomicReference<MockClientHttpRequest> tokenRequest = new AtomicReference<>();
		ClientHttpRequestFactory tokenServer = tokenServer(tokenRequest, "service-token");
		CredHubClient client = builder().requestFactory(tokenServer)
			.oauth2(REGISTRATION_ID, this.registrations, new InMemoryOAuth2AuthorizedClientService(this.registrations))
			.build();
		this.server.get()
			.expect(requestTo(BASE_URL + "/version"))
			.andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer service-token"))
			.andRespond(withSuccess("{\"version\":\"2.0.0\"}", MediaType.APPLICATION_JSON));
		assertThat(RequestContextHolder.getRequestAttributes()).as("no current request").isNull();

		assertThat(client.info().version().getVersion()).isEqualTo("2.0.0");

		assertTokenRequest(tokenRequest.get());
		this.server.get().verify();
	}

	@Test
	void everyRequestAsksTheClientManagerForAToken() {
		OAuth2AuthorizedClientManager clientManager = mock(OAuth2AuthorizedClientManager.class);
		given(clientManager.authorize(any())).willReturn(authorizedClient("token-1"), authorizedClient("token-2"));
		CredHubClient client = builder().oauth2(REGISTRATION_ID, this.registrations, clientManager).build();
		expectVersionRequest("token-1");
		expectVersionRequest("token-2");

		client.info().version();
		client.info().version();

		verify(clientManager, times(2)).authorize(any());
		this.server.get().verify();
	}

	@Test
	void validTokenIsReusedAcrossRequests() {
		AtomicInteger tokenRequests = new AtomicInteger();
		CredHubClient client = builder().requestFactory(countingTokenServer(tokenRequests, 3600))
			.oauth2(REGISTRATION_ID, this.registrations, new InMemoryOAuth2AuthorizedClientService(this.registrations))
			.build();
		expectVersionRequest("token-1");
		expectVersionRequest("token-1");

		client.info().version();
		client.info().version();

		assertThat(tokenRequests).hasValue(1);
		this.server.get().verify();
	}

	@Test
	void expiredTokenIsRenewed() {
		AtomicInteger tokenRequests = new AtomicInteger();
		// a token this short-lived is already inside Spring Security's clock skew window
		CredHubClient client = builder().requestFactory(countingTokenServer(tokenRequests, 1))
			.oauth2(REGISTRATION_ID, this.registrations, new InMemoryOAuth2AuthorizedClientService(this.registrations))
			.build();
		expectVersionRequest("token-1");
		expectVersionRequest("token-2");

		client.info().version();
		client.info().version();

		assertThat(tokenRequests).hasValue(2);
		this.server.get().verify();
	}

	@Test
	void unknownRegistrationIdIsRejectedWhenBuilding() {
		OAuth2AuthorizedClientManager clientManager = mock(OAuth2AuthorizedClientManager.class);

		assertThatIllegalStateException()
			.isThrownBy(() -> builder().oauth2("unknown", this.registrations, clientManager).build())
			.withMessage("The CredHub OAuth2 client registration ID 'unknown' is not a valid Spring Security "
					+ "OAuth2 client registration");
	}

	@Test
	void argumentsAreRequired() {
		OAuth2AuthorizedClientManager clientManager = mock(OAuth2AuthorizedClientManager.class);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> CredHubClient.builder().oauth2("", this.registrations, clientManager))
			.withMessage("registrationId must not be empty");
		assertThatIllegalArgumentException()
			.isThrownBy(() -> CredHubClient.builder().oauth2(REGISTRATION_ID, null, clientManager))
			.withMessage("clientRegistrationRepository must not be null");
		assertThatIllegalArgumentException()
			.isThrownBy(() -> CredHubClient.builder()
				.oauth2(REGISTRATION_ID, this.registrations, (OAuth2AuthorizedClientManager) null))
			.withMessage("clientManager must not be null");
		assertThatIllegalArgumentException()
			.isThrownBy(() -> CredHubClient.builder()
				.oauth2(REGISTRATION_ID, this.registrations, (OAuth2AuthorizedClientRepository) null))
			.withMessage("authorizedClientRepository must not be null");
		assertThatIllegalArgumentException()
			.isThrownBy(() -> CredHubClient.builder()
				.oauth2(REGISTRATION_ID, this.registrations, (OAuth2AuthorizedClientService) null))
			.withMessage("authorizedClientService must not be null");
	}

	private ClientHttpRequestFactory tokenServer(AtomicReference<MockClientHttpRequest> tokenRequest,
			String accessToken) {
		return (uri, method) -> {
			MockClientHttpRequest request = new MockClientHttpRequest(method, uri);
			MockClientHttpResponse response = new MockClientHttpResponse(
					("{\"access_token\":\"" + accessToken + "\",\"token_type\":\"Bearer\",\"expires_in\":3600}")
						.getBytes(),
					200);
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			request.setResponse(response);
			tokenRequest.set(request);
			return request;
		};
	}

	private ClientHttpRequestFactory countingTokenServer(AtomicInteger tokenRequests, int expiresIn) {
		return (uri, method) -> {
			MockClientHttpRequest request = new MockClientHttpRequest(method, uri);
			MockClientHttpResponse response = new MockClientHttpResponse(
					("{\"access_token\":\"token-" + tokenRequests.incrementAndGet()
							+ "\",\"token_type\":\"Bearer\",\"expires_in\":" + expiresIn + "}")
						.getBytes(),
					200);
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			request.setResponse(response);
			return request;
		};
	}

	private void expectVersionRequest(String bearerToken) {
		this.server.get()
			.expect(requestTo(BASE_URL + "/version"))
			.andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
			.andRespond(withSuccess("{\"version\":\"2.0.0\"}", MediaType.APPLICATION_JSON));
	}

	private void assertTokenRequest(MockClientHttpRequest request) {
		assertThat(request).isNotNull();
		assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
		assertThat(request.getURI().toString()).isEqualTo(TOKEN_URI);
		assertThat(request.getBodyAsString()).contains("grant_type=client_credentials");
	}

	private OAuth2AuthorizedClient authorizedClient(String tokenValue) {
		Instant issuedAt = Instant.now();
		return new OAuth2AuthorizedClient(this.registration, "client-id", new OAuth2AccessToken(
				OAuth2AccessToken.TokenType.BEARER, tokenValue, issuedAt, issuedAt.plusSeconds(3600)));
	}

}
