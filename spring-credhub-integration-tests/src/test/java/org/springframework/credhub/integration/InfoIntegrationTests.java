/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.integration;

import org.junit.Before;
import org.junit.Test;
import org.springframework.credhub.core.info.CredHubInfoOperations;
import org.springframework.credhub.support.info.VersionInfo;

import static org.assertj.core.api.Assertions.assertThat;

public class InfoIntegrationTests extends CredHubIntegrationTests {
	private CredHubInfoOperations info;

	@Before
	public void setUp() {
		info = operations.info();
	}
	
	@Test
	public void getInfo() {
		VersionInfo version = info.version();

		assertThat(version.getVersion()).isNotNull();
	}
}