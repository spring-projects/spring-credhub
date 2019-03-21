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

package org.springframework.credhub.configuration;

import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * Factory for {@link CredHubTemplate} used to communicate with CredHub.
 *
 * @author Scott Frederick
 * @author Daniel Lavoie
 */
public class CredHubTemplateFactory {

	public CredHubTemplate credHubTemplate(CredHubProperties credHubProperties,
			ClientHttpRequestFactory clientHttpRequestFactory) {
		return new CredHubTemplate(credHubProperties.getUrl(), clientHttpRequestFactory);
	}

	/**
	 * Create a {@link ClientHttpRequestFactory}.
	 *
	 * @return the {@link ClientHttpRequestFactory} instance.
	 * 
	 * @see #clientOptions()
	 */
	public ClientHttpRequestFactory clientHttpRequestFactoryWrapper() {
		return ClientHttpRequestFactoryFactory.create(clientOptions());
	}

	/**
	 * Create the default {@link ClientOptions} to configure communication parameters.
	 *
	 * @return the default {@link ClientOptions}
	 */
	private ClientOptions clientOptions() {
		return new ClientOptions();
	}
}
