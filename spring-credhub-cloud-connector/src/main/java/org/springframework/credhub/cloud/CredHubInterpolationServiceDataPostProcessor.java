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

package org.springframework.credhub.cloud;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.cloud.cloudfoundry.CloudFoundryRawServiceData;
import org.springframework.cloud.cloudfoundry.ServiceDataPostProcessor;
import org.springframework.credhub.configuration.CredHubTemplateFactory;
import org.springframework.credhub.core.interpolation.CredHubInterpolationOperations;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.credhub.support.ServicesData;

/**
 * A Spring Cloud Connectors {@link ServiceDataPostProcessor} that post-processes service
 * data from {@literal VCAP_SERVICES} using the CredHub interpolation API.
 *
 * @author Scott Frederick
 * @author Daniel Lavoie
 */
public class CredHubInterpolationServiceDataPostProcessor
		implements ServiceDataPostProcessor {
	private Logger logger = Logger
			.getLogger(CredHubInterpolationServiceDataPostProcessor.class.getName());

	private CredHubInterpolationOperations credHubOperations;

	/**
	 * Initialize the service data post-processor.
	 */
	public CredHubInterpolationServiceDataPostProcessor() {
		try {
			CredHubTemplateFactory credHubTemplateFactory = new CredHubTemplateFactory();
			CredHubProperties credHubProperties = new CredHubProperties();

			credHubProperties.setUrl(System.getProperty("spring.credhub.url"));

			if (credHubProperties.getUrl() != null
					&& !credHubProperties.getUrl().isEmpty()) {
				credHubOperations = credHubTemplateFactory.credHubTemplate(
						credHubProperties,
						new ClientOptions())
						.interpolation();
			}
			else {
				logger.log(Level.WARNING,
						"System property spring.credhub.url is undefined. CredHubOperations cannot be initialized, disabling processing of service data");
			}
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "CredHubOperations cannot be initialized, "
					+ "disabling processing of service data", e);
		}
	}

	/**
	 * Initialize the service data post-processor using the provided
	 * {@link CredHubOperations}. Intended for internal use.
	 *
	 * @param credHubOperations the CredHubOperations to use
	 */
	CredHubInterpolationServiceDataPostProcessor(CredHubInterpolationOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	/**
	 * Process the provided {@literal serviceData} parsed from {@literal VCAP_SERVICES} by
	 * Spring Cloud Connectors using the
	 * {@link CredHubInterpolationOperations#interpolateServiceData(ServicesData)} API.
	 *
	 * @param serviceData raw service data parsed from {@literal VCAP_SERVICES}
	 * @return serviceData with CredHub references replaced by stored credentials
	 */
	@Override
	public CloudFoundryRawServiceData process(CloudFoundryRawServiceData serviceData) {
		if (credHubOperations == null) {
			return serviceData;
		}

		try {
			ServicesData interpolatedData = credHubOperations
					.interpolateServiceData(connectorsToCredHub(serviceData));

			return credHubToConnectors(interpolatedData);
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Error interpolating service data from CredHub.",
					e);
			return serviceData;
		}
	}

	/**
	 * Convert from the Spring Cloud Connectors service data structure to the Spring
	 * Credhub data structure.
	 *
	 * @param rawServiceData the Spring Cloud Connectors data structure
	 * @return the equivalent Spring CredHub data structure
	 */
	private ServicesData connectorsToCredHub(CloudFoundryRawServiceData rawServiceData) {
		ServicesData servicesData = new ServicesData();
		servicesData.putAll(rawServiceData);
		return servicesData;
	}

	/**
	 * Convert from the Spring Credhub service data structure to the Spring Cloud
	 * Connectors data structure.
	 *
	 * @param interpolatedData the Spring CredHub data structure
	 * @return the equivalent Spring Cloud Connectors data structure
	 */
	private CloudFoundryRawServiceData credHubToConnectors(
			ServicesData interpolatedData) {
		CloudFoundryRawServiceData rawServicesData = new CloudFoundryRawServiceData();
		rawServicesData.putAll(interpolatedData);
		return rawServicesData;
	}
}
