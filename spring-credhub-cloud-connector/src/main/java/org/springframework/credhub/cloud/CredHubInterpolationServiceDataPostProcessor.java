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

package org.springframework.credhub.cloud;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.cloud.cloudfoundry.CloudFoundryRawServiceData;
import org.springframework.cloud.cloudfoundry.ServiceDataPostProcessor;
import org.springframework.credhub.configuration.CredHubConfiguration;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.VcapServicesData;

/**
 * A Spring Cloud Connectors {@link ServiceDataPostProcessor} that post-processes service
 * data from {@literal VCAP_SERVICES} using the CredHub {@literal vcap} interpolation API.
 *
 * @author Scott Frederick
 */
public class CredHubInterpolationServiceDataPostProcessor implements ServiceDataPostProcessor {
	private Logger logger = Logger
			.getLogger(CredHubInterpolationServiceDataPostProcessor.class.getName());

	private CredHubOperations credHubOperations;

	/**
	 * Initialize the service data post-processor.
	 */
	public CredHubInterpolationServiceDataPostProcessor() {
		try {
			credHubOperations = new CredHubConfiguration().credHubTemplate();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "CredHubOperations cannot be initialized, " +
							"disabling processing of service data: "
							+ e.getMessage());
		}
	}

	/**
	 * Initialize the service data post-processor using the provided {@link CredHubOperations}.
	 * Intended for internal use.
	 *
	 * @param credHubOperations the CredHubOperations to use
	 */
	CredHubInterpolationServiceDataPostProcessor(CredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	/**
	 * Process the provided {@literal serviceData} parsed from {@literal VCAP_SERVICES} by
	 * Spring Cloud Connectors using the
	 * {@link CredHubOperations#interpolateServiceData(VcapServicesData)} API.
	 *
	 * @param serviceData raw service data parsed from {@literal VCAP_SERVICES}
	 * @return serviceData with CredHub references replaced by stored credentials
	 */
	@Override
	public CloudFoundryRawServiceData process(CloudFoundryRawServiceData serviceData) {
		if (credHubOperations == null) {
			return serviceData;
		}

		VcapServicesData interpolatedData = credHubOperations
				.interpolateServiceData(connectorsToCredHub(serviceData));

		return credHubToConnectors(interpolatedData);
	}

	/**
	 * Convert from the Spring Cloud Connectors service data structure to the Spring Credhub
	 * data structure.
	 *
	 * @param serviceData the Spring Cloud Connectors data structure
	 * @return the equivalent Spring CredHub data structure
	 */
	private VcapServicesData connectorsToCredHub(CloudFoundryRawServiceData serviceData) {
		VcapServicesData vcapServicesData = new VcapServicesData();
		vcapServicesData.putAll(serviceData);
		return vcapServicesData;
	}

	/**
	 * Convert from the Spring Credhub service data structure to the Spring Cloud Connectors
	 * data structure.
	 *
	 * @param interpolatedData the Spring CredHub data structure
	 * @return the equivalent Spring Cloud Connectors data structure
	 */
	private CloudFoundryRawServiceData credHubToConnectors(VcapServicesData interpolatedData) {
		CloudFoundryRawServiceData rawServicesData = new CloudFoundryRawServiceData();
		rawServicesData.putAll(interpolatedData);
		return rawServicesData;
	}
}
