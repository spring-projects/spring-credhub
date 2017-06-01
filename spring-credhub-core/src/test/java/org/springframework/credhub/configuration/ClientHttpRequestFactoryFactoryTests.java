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

package org.springframework.credhub.configuration;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.springframework.credhub.configuration.ClientHttpRequestFactoryFactory.HttpComponents.usingHttpComponents;
import static org.springframework.credhub.configuration.ClientHttpRequestFactoryFactory.HttpURLConnection.usingJdk;

public class ClientHttpRequestFactoryFactoryTests {

	@Test
	public void jdkDefaultClientCreated() throws Exception {
		ClientHttpRequestFactory factory = usingJdk(new ClientOptions());

		assertThat(factory, instanceOf(SimpleClientHttpRequestFactory.class));
	}

	@Test
	public void httpComponentsClientCreated() throws Exception {

		ClientHttpRequestFactory factory = usingHttpComponents(new ClientOptions());

		assertThat(factory, instanceOf(HttpComponentsClientHttpRequestFactory.class));

		HttpClient httpClient = ((HttpComponentsClientHttpRequestFactory) factory).getHttpClient();

		assertThat(httpClient, instanceOf(CloseableHttpClient.class));

		((DisposableBean) factory).destroy();
	}
}