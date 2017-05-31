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
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * Configuration for the {@link CredHubTemplate} used to communicate with CredHub. This
 * class is typically imported into Java-based Spring application configuration as in
 * this example:
 *
 * <pre>
 * {@code
 * &#64;Configuration
 * &#64;Import(CredHubConfiguration.class)
 * public class MyConfiguration {
 * }
 * }
 * </pre>
 *
 * @author Scott Frederick 
 */
@Configuration
public class CredHubConfiguration {

	/**
	 * Create the {@link CredHubProperties} that contains information about the
	 * CredHub server.
	 *
	 * @return the {@link CredHubProperties} bean
	 */
	@Bean
	public CredHubProperties credHubProperties() {
		return new CredHubProperties();
	}

	/**
	 * Create the {@link CloudFoundryAppInstanceProperties} that contains information
	 * about the application instance running on Cloud Foundry.
	 *
	 * @return the {@link CloudFoundryAppInstanceProperties} bean
	 */
	@Bean
	public CloudFoundryAppInstanceProperties cloudFoundryAppInstanceProperties() {
		return new CloudFoundryAppInstanceProperties();
	}

	/**
	 * Create the {@link CredHubTemplate} that the application will use to interact
	 * with CredHub.
	 *
	 * @return the {@link CredHubTemplate} bean
	 */
	@Bean
	public CredHubTemplate credHubTemplate() {
		return new CredHubTemplate(credHubProperties().getApiUriBase(),
				clientHttpRequestFactoryWrapper().getClientHttpRequestFactory());
	}

	/**
	 * Create a {@link ClientFactoryWrapper} containing a
	 * {@link ClientHttpRequestFactory}. {@link ClientHttpRequestFactory} is not exposed
	 * as root bean because {@link ClientHttpRequestFactory} is configured with
	 * {@link ClientOptions} which are not necessarily
	 * applicable for the whole application.
	 *
	 * @return the {@link ClientFactoryWrapper} to wrap a {@link ClientHttpRequestFactory}
	 * instance.
	 * @see #clientOptions()
	 */
	@Bean
	public ClientFactoryWrapper clientHttpRequestFactoryWrapper() {
		ClientHttpRequestFactory clientHttpRequestFactory =
				ClientHttpRequestFactoryFactory.create(clientOptions());
		return new ClientFactoryWrapper(clientHttpRequestFactory);
	}

	/**
	 * Create the default {@link ClientOptions} to configure communication parameters.
	 *
	 * @return the default {@link ClientOptions}
	 */
	private ClientOptions clientOptions() {
		return new ClientOptions();
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
