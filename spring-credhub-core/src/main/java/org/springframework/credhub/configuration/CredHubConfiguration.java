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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.credhub.core.CloudFoundryAppInstanceProperties;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.credhub.support.SslConfiguration;
import org.springframework.http.client.ClientHttpRequestFactory;

@Configuration
public class CredHubConfiguration {
	@Bean
	public CredHubProperties credHubProperties() {
		return new CredHubProperties();
	}

	@Bean
	public CloudFoundryAppInstanceProperties cloudFoundryAppInstanceProperties() {
		return new CloudFoundryAppInstanceProperties();
	}

	@Bean
	public CredHubTemplate credHubTemplate() {
		return new CredHubTemplate(credHubProperties().getApiUriBase(),
				clientHttpRequestFactoryWrapper().getClientHttpRequestFactory());
	}

	/**
	 * Create a {@link ClientFactoryWrapper} containing a {@link ClientHttpRequestFactory}.
	 * {@link ClientHttpRequestFactory} is not exposed as root bean because
	 * {@link ClientHttpRequestFactory} is configured with {@link ClientOptions} and
	 * {@link SslConfiguration} which are not necessarily applicable for the whole
	 * application.
	 *
	 * @return the {@link ClientFactoryWrapper} to wrap a {@link ClientHttpRequestFactory}
	 * instance.
	 * @see #clientOptions()
	 * @see #sslConfiguration()
	 */
	@Bean
	public ClientFactoryWrapper clientHttpRequestFactoryWrapper() {
		ClientHttpRequestFactory clientHttpRequestFactory =
				ClientHttpRequestFactoryFactory.create(clientOptions(), sslConfiguration());
		return new ClientFactoryWrapper(clientHttpRequestFactory);
	}

	/**
	 * @return {@link ClientOptions} to configure communication parameters.
	 * @see ClientOptions
	 */
	private ClientOptions clientOptions() {
		return new ClientOptions();
	}

	/**
	 * @return SSL configuration options.
	 * @see SslConfiguration
	 */
	private SslConfiguration sslConfiguration() {
		CloudFoundryAppInstanceProperties properties = cloudFoundryAppInstanceProperties();
		return SslConfiguration.forContainerCert(properties.getInstanceCertLocation(), properties.getInstanceKeyLocation());
	}

	/**
	 * Wrapper for {@link ClientHttpRequestFactory} to not expose the bean globally.
	 */
	public static class ClientFactoryWrapper implements InitializingBean, DisposableBean {

		private final ClientHttpRequestFactory clientHttpRequestFactory;

		public ClientFactoryWrapper(ClientHttpRequestFactory clientHttpRequestFactory) {
			this.clientHttpRequestFactory = clientHttpRequestFactory;
		}

		@Override
		public void destroy() throws Exception {
			if (clientHttpRequestFactory instanceof DisposableBean) {
				((DisposableBean) clientHttpRequestFactory).destroy();
			}
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			if (clientHttpRequestFactory instanceof InitializingBean) {
				((InitializingBean) clientHttpRequestFactory).afterPropertiesSet();
			}
		}

		public ClientHttpRequestFactory getClientHttpRequestFactory() {
			return clientHttpRequestFactory;
		}
	}
}
