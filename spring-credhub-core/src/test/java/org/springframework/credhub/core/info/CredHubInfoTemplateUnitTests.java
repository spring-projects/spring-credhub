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

package org.springframework.credhub.core.info;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.info.VersionInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.info.CredHubInfoTemplate.VERSION_URL_PATH;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class CredHubInfoTemplateUnitTests {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

	@Mock
	private RestTemplate restTemplate;

	private CredHubInfoOperations credHubTemplate;

	@Before
	public void setUp() {
		credHubTemplate = new CredHubTemplate(restTemplate).info();
	}

	@Test
	public void getVersion() {
		when(restTemplate.getForEntity(VERSION_URL_PATH, VersionInfo.class))
				.thenReturn(new ResponseEntity<>(new VersionInfo("2.0.0"), OK));

		VersionInfo response = credHubTemplate.version();

		assertThat(response.getVersion()).isEqualTo("2.0.0");
	}

}