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

package org.springframework.credhub.core;

import org.springframework.beans.factory.annotation.Value;

/**
 * Properties containing information about an application instance
 * running on Cloud Foundry.
 *
 * @author Scott Frederick
 */
public class CloudFoundryAppInstanceProperties {
	@Value("${CF_INSTANCE_CERT}")
	private String instanceCertLocation;

	@Value("${CF_INSTANCE_KEY}")
	private String instanceKeyLocation;

	/**
	 * Create a new instance without initializing properties.
	 */
	public CloudFoundryAppInstanceProperties() {
	}

	/**
	 * Create a new instance from the provided property values. Intended to be used
	 * internally for testing.
	 *
	 * @param instanceCertLocation the absolute path of the certificate file in the app
	 * instance container
	 * @param instanceKeyLocation the absolute path of the private key file in the app
	 * instance container
	 */
	CloudFoundryAppInstanceProperties(String instanceCertLocation,
			String instanceKeyLocation) {
		this.instanceCertLocation = instanceCertLocation;
		this.instanceKeyLocation = instanceKeyLocation;
	}

	/**
	 * Get the absolute path of the certificate file in the app instance container.
	 *
	 * @return the certificate file path
	 */
	public String getInstanceCertLocation() {
		return instanceCertLocation;
	}

	/**
	 * Get the absolute path of the private key file in the app instance container.
	 *
	 * @return the key file path
	 */
	public String getInstanceKeyLocation() {
		return instanceKeyLocation;
	}
}
