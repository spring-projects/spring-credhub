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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CredHubRestTemplateFactoryUnitTests {

	@Mock
	private ClientHttpRequestFactory clientHttpRequestFactory;

	@Test
	public void restTemplateIsCreated() {
		CredHubProperties properties = new CredHubProperties();
		properties.setUrl("https://credhub.cf.example.com:8844");
		RestTemplate restTemplate = CredHubRestTemplateFactory.createRestTemplate(properties,
				this.clientHttpRequestFactory);

		assertThat(restTemplate).isNotNull();
	}

}
