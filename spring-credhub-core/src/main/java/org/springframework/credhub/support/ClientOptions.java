/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.support;

import java.util.concurrent.TimeUnit;

/**
 * Client options for CredHub connectivity.
 *
 * @author Mark Paluch
 * @author Scott Frederick
 */
public class ClientOptions {

	/**
	 * Connection timeout;
	 */
	private final Integer connectionTimeout;

	/**
	 * Read timeout;
	 */
	private final Integer readTimeout;

	/**
	 * Create new {@link ClientOptions} with default timeouts.
	 */
	public ClientOptions() {
		this.connectionTimeout = null;
		this.readTimeout = null;
	}

	/**
	 * Create a {@link ClientOptions} with the provided values.
	 *
	 * @param connectionTimeout connection timeout in {@link TimeUnit#MILLISECONDS}, must
	 * be greater {@literal 0}.
	 * @param readTimeout read timeout in {@link TimeUnit#MILLISECONDS}, must be greater
	 * {@literal 0}.
	 */
	public ClientOptions(int connectionTimeout, int readTimeout) {
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
	}

	/**
	 * Get the connection timeout in {@link TimeUnit#MILLISECONDS}.
	 *
	 * @return the connection timeout; can be {@literal null if not explicitly set}
	 */
	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * Get the read timeout in {@link TimeUnit#MILLISECONDS}.
	 *
	 * @return the read timeout; can be {@literal null if not explicitly set}
	 */
	public Integer getReadTimeout() {
		return readTimeout;
	}

}