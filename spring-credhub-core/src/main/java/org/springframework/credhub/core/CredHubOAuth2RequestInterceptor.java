/*
 * Copyright 2013-2017 the original author or authors.
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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

/**
 * A request interceptor that sets OAuth2 bearer authentication headers to all CredHub requests.
 *
 * @author Scott Frederick
 */
class CredHubOAuth2RequestInterceptor implements ClientHttpRequestInterceptor {
	private final ClientRegistration clientRegistration;
	private final OAuth2AuthorizedClientService authorizedClientService;

	private final DefaultClientCredentialsTokenResponseClient clientCredentialsTokenResponseClient;

	private final Clock clock = Clock.systemUTC();
	private final Duration accessTokenExpiresSkew = Duration.ofMinutes(1);

	CredHubOAuth2RequestInterceptor(RestOperations tokenServerRestTemplate,
									ClientRegistration clientRegistration,
									OAuth2AuthorizedClientService authorizedClientService) {
		this.clientRegistration = clientRegistration;
		this.authorizedClientService = authorizedClientService;

		this.clientCredentialsTokenResponseClient = createClientCredentialsTokenResponseClient(tokenServerRestTemplate);
	}

	/**
	 * Add an OAuth2 bearer token header to each request.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
										ClientHttpRequestExecution execution) throws IOException {
		HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);

		HttpHeaders headers = requestWrapper.getHeaders();
		headers.setBearerAuth(getAccessToken().getTokenValue());

		return execution.execute(requestWrapper, body);
	}

	private OAuth2AccessToken getAccessToken() {
		OAuth2AuthorizedClient client = authorizedClientService
				.loadAuthorizedClient(clientRegistration.getRegistrationId(), clientRegistration.getClientId());

		if (client == null || tokenExpiring(client)) {
			client = authorizeClient();
		}

		return client.getAccessToken();
	}

	private OAuth2AuthorizedClient authorizeClient() {
		OAuth2ClientCredentialsGrantRequest request =
				new OAuth2ClientCredentialsGrantRequest(clientRegistration);
		OAuth2AccessTokenResponse tokenResponse = clientCredentialsTokenResponseClient.getTokenResponse(request);

		OAuth2AccessToken accessToken = tokenResponse.getAccessToken();
		OAuth2RefreshToken refreshToken = tokenResponse.getRefreshToken();

		OAuth2AuthorizedClient authorizedClient =
				new OAuth2AuthorizedClient(clientRegistration,
						clientRegistration.getClientId(),
						accessToken, refreshToken);

		saveAuthorizedClient(clientRegistration, accessToken, authorizedClient);

		return authorizedClient;
	}

	private boolean tokenExpiring(OAuth2AuthorizedClient client) {
		Instant now = this.clock.instant();
		Instant expiresAt = client.getAccessToken().getExpiresAt();
		if (expiresAt != null && now.isAfter(expiresAt.minus(this.accessTokenExpiresSkew))) {
			return true;
		}
		return false;
	}

	private void saveAuthorizedClient(ClientRegistration clientRegistration,
									  OAuth2AccessToken accessToken,
									  OAuth2AuthorizedClient authorizedClient) {
		OAuth2ClientCredentialsGrantAuthenticationToken authentication =
				new OAuth2ClientCredentialsGrantAuthenticationToken(clientRegistration, accessToken);
		authorizedClientService.saveAuthorizedClient(authorizedClient, authentication);
	}

	private static DefaultClientCredentialsTokenResponseClient createClientCredentialsTokenResponseClient(RestOperations restTemplate) {
		DefaultClientCredentialsTokenResponseClient clientCredentialsTokenResponseClient =
				new DefaultClientCredentialsTokenResponseClient();
		clientCredentialsTokenResponseClient.setRestOperations(restTemplate);
		return clientCredentialsTokenResponseClient;
	}

	private static class OAuth2ClientCredentialsGrantAuthenticationToken extends AbstractAuthenticationToken {
		private final ClientRegistration clientRegistration;
		private final OAuth2AccessToken accessToken;

		OAuth2ClientCredentialsGrantAuthenticationToken(ClientRegistration clientRegistration,
														OAuth2AccessToken accessToken) {
			super(Collections.emptyList());
			this.clientRegistration = clientRegistration;
			this.accessToken = accessToken;
		}

		@Override
		public Object getCredentials() {
			return accessToken.getTokenValue();
		}

		@Override
		public Object getPrincipal() {
			return this.clientRegistration.getClientId();
		}
	}
}
