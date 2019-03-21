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
import org.springframework.credhub.core.ReactiveCredHubOperations;
import org.springframework.credhub.support.ServicesData;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/**
 * Implements the main interaction with CredHub to interpolate service binding credentials.
 *
 * @author Scott Frederick 
 */
public class ReactiveCredHubInterpolationTemplate implements ReactiveCredHubInterpolationOperations {
	private static final String INTERPOLATE_URL_PATH = "/api/v1/interpolate";

	private ReactiveCredHubOperations credHubOperations;

	/**
	 * Create a new {@link ReactiveCredHubInterpolationTemplate}.
	 *
	 * @param credHubOperations the {@link CredHubOperations} to use for interactions with CredHub
	 */
	public ReactiveCredHubInterpolationTemplate(ReactiveCredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	@Override
	public Mono<ServicesData> interpolateServiceData(final ServicesData serviceData) {
		Assert.notNull(serviceData, "serviceData must not be null");

		return credHubOperations.doWithWebClient(webClient -> webClient
				.post()
				.uri(INTERPOLATE_URL_PATH)
				.syncBody(serviceData)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(ServicesData.class));
	}
}
