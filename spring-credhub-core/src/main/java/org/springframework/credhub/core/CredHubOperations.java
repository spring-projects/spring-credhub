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

package org.springframework.credhub.core;

import java.util.List;

import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.CredentialPermission;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.ParametersRequest;
import org.springframework.credhub.support.ServicesData;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.web.client.RestTemplate;

/**
 * Specifies the main interaction with CredHub to save, generate, retrieve,
 * and delete credentials.
 *
 * @author Scott Frederick
 */
public interface CredHubOperations {
	/**
	 * Write a new credential to CredHub, or overwrite an existing credential with a new
	 * value.
	 *
	 * @param credentialRequest the credential to write to CredHub; must not be {@literal null}
	 * @param <T> the credential implementation type
	 * @return the details of the written credential
	 */
	<T> CredentialDetails<T> write(final CredentialRequest<T> credentialRequest);

	/**
	 * Generate a new credential in CredHub, or overwrite an existing credential with a new
	 * generated value.
	 *
	 * @param parametersRequest the parameters of the new credential to generate in CredHub;
	 *                                must not be {@literal null}
	 * @param <T> the credential implementation type
	 * @param <P> the credential parameter implementation type
	 * @return the details of the generated credential
	 */
	<T, P> CredentialDetails<T> generate(ParametersRequest<P> parametersRequest);

	/**
	 * Regenerate a credential in CredHub. Only credentials that were previously generated can be
	 * re-generated.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 * @param <T> the credential implementation type
	 * @return the details of the regenerated credential
	 */
	<T> CredentialDetails<T> regenerate(CredentialName name);

	/**
	 * Retrieve a credential using its ID, as returned in a write request.
	 *
	 * @param id the ID of the credential; must not be {@literal null}
	 * @param credentialType the type of the credential to be retrieved; must not be {@literal null}
	 * @param <T> the credential implementation type
	 * @return the details of the retrieved credential
	 */
	<T> CredentialDetails<T> getById(final String id, Class<T> credentialType);

	/**
	 * Retrieve a credential using its name, as passed to a write request.
	 * Only the current credential value will be returned.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 * @param credentialType the type of credential expected to be returned
	 * @param <T> the credential implementation type
	 * @return the details of the retrieved credential
	 */
	<T> CredentialDetails<T> getByName(final CredentialName name, Class<T> credentialType);

	/**
	 * Retrieve a credential using its name, as passed to a write request.
	 * A collection of all stored values for the named credential will be returned,
	 * including historical values.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 * @param credentialType the type of credential expected to be returned
	 * @param <T> the credential implementation type
	 * @return the details of the retrieved credential, including history
	 */
	<T> List<CredentialDetails<T>> getByNameWithHistory(CredentialName name, Class<T> credentialType);

	/**
	 * Find a credential using a full or partial name.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 * @return a summary of the credential search results
	 */
	List<CredentialSummary> findByName(CredentialName name);

	/**
	 * Find a credential using a path.
	 *
	 * @param path the path to the credential; must not be {@literal null}
	 * @return a summary of the credential search results
	 */
	List<CredentialSummary> findByPath(String path);

	/**
	 * Delete a credential by its full name.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 */
	void deleteByName(CredentialName name);

	/**
	 * Get the permissions associated with a credential.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 * @return the collection of permissions associated with the credential
	 */
	List<CredentialPermission> getPermissions(CredentialName name);

	/**
	 * Add permissions to an existing credential.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 * @param permissions a collection of permissions to add
	 * @return the collection of permissions associated with the credential
	 */
	List<CredentialPermission> addPermissions(CredentialName name, CredentialPermission... permissions);

	/**
	 * Delete a permission associated with a credential.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 * @param actor the actor of the permission; must not be {@literal null}
	 */
	void deletePermission(CredentialName name, Actor actor);

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
	ServicesData interpolateServiceData(ServicesData serviceData);

	/**
	 * Allow interaction with the configured {@link RestTemplate} not provided
	 * by other methods.
	 *
	 * @param callback wrapper for the callback method
	 * @param <T> the credential implementation type
	 * @return the return value from the callback method
	 */
	<T> T doWithRest(RestOperationsCallback<T> callback);
}
