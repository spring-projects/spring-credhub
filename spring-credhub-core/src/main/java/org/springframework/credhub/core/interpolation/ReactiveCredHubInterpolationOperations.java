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

package org.springframework.credhub.core.interpolation;

import org.springframework.credhub.support.ServicesData;
import reactor.core.publisher.Mono;

/**
 * Specifies the interactions with CredHub to interpolate service binding credentials.
 *
 * @author Scott Frederick
 */
public interface ReactiveCredHubInterpolationOperations {
	/**
	 * Search the provided data structure of bound service credentials, looking for
	 * references to CredHub credentials. Any CredHub credentials found in the data
	 * structure will be replaced by the credential value stored in CredHub.
	 *
	 * Example:
	 *
	 * A JSON data structure parsed from a {@literal VCAP_SERVICES} environment
	 * variable might look like this if the service broker that provided the binding
	 * is integrated with CredHub:
	 *
	 * <pre>
	 * {@code
	 * {
	 *    "service-offering": [{
	 *      "credentials": {
	 *        "credhub-ref": "((/c/service-broker/service-offering/1111-2222-3333-4444/credentials))"
	 *      }
	 *      "label": "service-offering",
	 *      "name": "service-instance",
	 *      "plan": "standard",
	 *      "tags": ["
	 *        "cloud-service"
	 *      ]
	 *    }]
	 * }
	 * }
	 * </pre>
	 *
	 * Assuming that CredHub has a credential with the name
	 * {@literal /c/service-broker/service-offering/1111-2222-3333-4444/credentials},
	 * passing the data structure above to this method would result in the
	 * {@literal credhub-ref} field being replaced by the credentials stored in CredHub:
	 *
	 * <pre>
	 * {@code
	 * {
	 *    "service-offering": [{
	 *      "credentials": {
	 *        "url": "https://servicehost.example.com/",
	 *        "username": "someuser",
	 *        "password": "secret"
	 *      }
	 *      "label": "service-offering",
	 *      "name": "service-instance",
	 *      "plan": "standard",
	 *      "tags": ["
	 *        "cloud-service"
	 *      ]
	 *    }]
	 * }
	 * }
	 * </pre>
	 *
	 * @param serviceData a data structure of bound service credentials, as would be
	 * parsed from the {@literal VCAP_SERVICES} environment variable provided to
	 * applications running on Cloud Foundry
	 * @return the serviceData structure with CredHub references replaced by stored
	 * credential values
	 */
	Mono<ServicesData> interpolateServiceData(final ServicesData serviceData);
}
