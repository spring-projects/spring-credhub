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

package org.springframework.credhub.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.core.info.CredHubInfoOperations;
import org.springframework.credhub.support.info.VersionInfo;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CredHubClientInfoIntegrationTests extends CredHubClientIntegrationTests {

	private CredHubInfoOperations info;

	@BeforeEach
	void setUp() {
		this.info = this.client.info();
	}

	@Test
	void getInfo() {
		VersionInfo version = this.info.version();

		assertThat(version.getVersion()).isNotNull();
	}

	@Test
	void unauthenticatedRequestIsRejected() {
		assertThatExceptionOfType(CredHubException.class).isThrownBy(() -> unauthenticatedClient().info().version())
			.satisfies((ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
	}

	@Test
	void autoConfiguredClientDoesNotNeedACurrentRequest() {
		// the Spring test context binds a request to the test thread; remove it
		RequestAttributes boundRequest = RequestContextHolder.getRequestAttributes();
		RequestContextHolder.resetRequestAttributes();
		try {
			assertThat(this.client.info().version().getVersion()).isNotNull();
		}
		finally {
			RequestContextHolder.setRequestAttributes(boundRequest);
		}
	}

	@Test
	void authorizedClientRepositoryWorksWithACurrentRequest() {
		assertThat(repositoryBasedClient().info().version().getVersion()).isNotNull();
	}

}
