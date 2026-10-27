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

package org.springframework.credhub.core.interpolation;

import org.springframework.credhub.support.ServicesData;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

/**
 * Default {@link CredHubInterpolationOperations} implementation, written against a
 * {@link RestClient}.
 *
 * @author Roy Clarkson
 * @since 4.2.0
 */
public class DefaultCredHubInterpolationOperations implements CredHubInterpolationOperations {

	private static final String INTERPOLATE_URL_PATH = "/api/v1/interpolate";

	private final RestClient restClient;

	/**
	 * Create a new instance using the provided {@link RestClient}.
	 * @param restClient a {@link RestClient} configured for communication with CredHub,
	 * with error responses translated to
	 * {@link org.springframework.credhub.core.CredHubException}
	 */
	public DefaultCredHubInterpolationOperations(RestClient restClient) {
		Assert.notNull(restClient, "restClient must not be null");
		this.restClient = restClient;
	}

	@Override
	public ServicesData interpolateServiceData(ServicesData serviceData) {
		Assert.notNull(serviceData, "serviceData must not be null");

		return this.restClient.post().uri(INTERPOLATE_URL_PATH).body(serviceData).retrieve().body(ServicesData.class);
	}

}
