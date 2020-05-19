/*
 * Copyright 2016-2020 the original author or authors.
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

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

/**
 * A request interceptor that sets OAuth2 bearer authentication headers to all CredHub
 * requests.
 *
 * @author Scott Frederick
 */
class CredHubOAuth2RequestInterceptor implements ClientHttpRequestInterceptor {

	private final ClientRegistration clientRegistration;

	private final OAuth2AuthorizedClientManager clientManager;

	CredHubOAuth2RequestInterceptor(ClientRegistration clientRegistration,
			OAuth2AuthorizedClientManager clientManager) {
		this.clientRegistration = clientRegistration;
		this.clientManager = clientManager;
	}

	/**
	 * Add an OAuth2 bearer token header to each request.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);

		HttpHeaders headers = requestWrapper.getHeaders();
		headers.setBearerAuth(authorizeClient().getAccessToken().getTokenValue());

		return execution.execute(requestWrapper, body);
	}

	private OAuth2AuthorizedClient authorizeClient() {
		OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
				.withClientRegistrationId(this.clientRegistration.getRegistrationId())
				.principal(new OAuth2ClientCredentialsGrantAuthenticationToken(this.clientRegistration)).build();
		return this.clientManager.authorize(authorizeRequest);
	}

	private static class OAuth2ClientCredentialsGrantAuthenticationToken extends AbstractAuthenticationToken {

		private final ClientRegistration clientRegistration;

		OAuth2ClientCredentialsGrantAuthenticationToken(ClientRegistration clientRegistration) {
			super(Collections.emptyList());
			this.clientRegistration = clientRegistration;
		}

		@Override
		public Object getCredentials() {
			return null;
		}

		@Override
		public Object getPrincipal() {
			return this.clientRegistration.getClientId();
		}

	}

}
