/*
 *
 *  * Copyright 2013-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.credhub.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.AbstractUriTemplateHandler;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CredHubClientUnitTests {
	private static final String CREDHUB_URI = "https://credhub.cf.example.com:8844";

	@Mock
	private ClientHttpRequestFactory clientHttpRequestFactory;

	@Test
	public void restTemplateIsCreated() throws Exception {
		RestTemplate restTemplate = CredHubClient.createRestTemplate(CREDHUB_URI,
				clientHttpRequestFactory);

		assertThat(restTemplate.getUriTemplateHandler(),
				instanceOf(AbstractUriTemplateHandler.class));

		AbstractUriTemplateHandler uriTemplateHandler = (AbstractUriTemplateHandler) restTemplate
				.getUriTemplateHandler();
		assertThat(uriTemplateHandler.getBaseUrl(), equalTo(CREDHUB_URI));
	}
}