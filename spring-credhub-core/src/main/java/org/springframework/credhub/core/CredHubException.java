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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Exception class to capture errors specific to CredHub communication.
 *
 * @author Scott Frederick
 */
@SuppressWarnings("serial")
public class CredHubException extends HttpStatusCodeException {

	/**
	 * Create a new exception with the provided root cause.
	 * @param e an {@link HttpStatusCodeException} caught while attempting to communicate
	 * with CredHub
	 */
	public CredHubException(HttpStatusCodeException e) {
		super(e.getStatusCode(), e.getStatusText(), e.getResponseHeaders(), e.getResponseBodyAsByteArray(), null);
	}

	/**
	 * Create a new exception with the provided error status code.
	 * @param statusCode an {@link HttpStatusCode} indicating an error while attempting to
	 * communicate with CredHub
	 */
	public CredHubException(HttpStatusCode statusCode) {
		super(statusCode);
	}

	/**
	 * Create a new exception with the provided error status code, headers, and response
	 * body.
	 * @param statusCode an {@link HttpStatusCode} indicating an error
	 * @param headers the response headers
	 * @param responseBody the response body as a byte array
	 */
	public CredHubException(HttpStatusCode statusCode, HttpHeaders headers, byte[] responseBody) {
		super(statusCode, statusCode.toString(), headers, responseBody, null);
	}

}
