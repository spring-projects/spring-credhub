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

package org.springframework.credhub.core.interpolation;

import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.ExceptionUtils;
import org.springframework.credhub.support.ServicesData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

/**
 * Implements the main interaction with CredHub to interpolate service binding credentials.
 *
 * @author Scott Frederick 
 */
public class CredHubInterpolationTemplate implements CredHubInterpolationOperations {
	static final String INTERPOLATE_URL_PATH = "/api/v1/interpolate";

	private CredHubOperations credHubOperations;

	/**
	 * Create a new {@link CredHubInterpolationTemplate}.
	 *
	 * @param credHubOperations the {@link CredHubOperations} to use for interactions with CredHub
	 */
	public CredHubInterpolationTemplate(CredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	@Override
	public ServicesData interpolateServiceData(final ServicesData serviceData) {
		Assert.notNull(serviceData, "serviceData must not be null");

		return credHubOperations.doWithRest(restOperations -> {
			ResponseEntity<ServicesData> response = restOperations
					.exchange(INTERPOLATE_URL_PATH, HttpMethod.POST,
							new HttpEntity<>(serviceData), ServicesData.class);

			ExceptionUtils.throwExceptionOnError(response);

			return response.getBody();
		});
	}
}
