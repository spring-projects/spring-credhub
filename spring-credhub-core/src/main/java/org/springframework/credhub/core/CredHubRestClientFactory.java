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

import org.springframework.credhub.support.utils.JsonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

/**
 * Creates {@link RestClient.Builder} instances configured for communication with a
 * CredHub server. Everything the {@code CredHubTemplate} family repeats per operation
 * lives here once: the base URL, the common headers, the JSON converters, and the
 * translation of error responses to {@link CredHubException}.
 *
 * @author Roy Clarkson
 */
final class CredHubRestClientFactory {

	private CredHubRestClientFactory() {
	}

	/**
	 * Create a {@link RestClient.Builder} for the CredHub server at the given base URL.
	 * @param baseUrl the base URL of the CredHub server
	 * @return a configured {@link RestClient.Builder}
	 */
	static RestClient.Builder builder(String baseUrl) {
		Assert.hasText(baseUrl, "baseUrl must not be empty");

		return RestClient.builder()
			.baseUrl(baseUrl)
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.configureMessageConverters((converters) -> converters.disableDefaults()
				.addCustomConverter(new ByteArrayHttpMessageConverter())
				.withStringConverter(new StringHttpMessageConverter())
				.withJsonConverter(new JacksonJsonHttpMessageConverter(JsonUtils.buildJsonMapper())))
			.defaultStatusHandler((status) -> !status.is2xxSuccessful(), (request, response) -> {
				throw new CredHubException(new ErrorResponseException(response.getStatusCode(),
						response.getStatusText(), response.getHeaders(), response.getBody().readAllBytes()));
			});
	}

	/**
	 * Carries the status, reason phrase, headers, and body of a non-2xx response so that
	 * {@link CredHubException} reports them the way it does for the exceptions that
	 * {@code RestTemplate} throws. Every non-2xx response is treated as an error, not
	 * only 4xx and 5xx, so that a redirect from something in front of CredHub is not
	 * mistaken for success.
	 */
	@SuppressWarnings("serial")
	private static final class ErrorResponseException extends HttpStatusCodeException {

		ErrorResponseException(HttpStatusCode statusCode, String statusText, HttpHeaders headers, byte[] body) {
			super(statusCode, statusText, headers, body, null);
		}

	}

}
