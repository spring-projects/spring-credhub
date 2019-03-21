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

import org.springframework.credhub.core.certificate.CredHubCertificateOperations;
import org.springframework.credhub.core.credential.CredHubCredentialOperations;
import org.springframework.credhub.core.info.CredHubInfoOperations;
import org.springframework.credhub.core.interpolation.CredHubInterpolationOperations;
import org.springframework.credhub.core.permission.CredHubPermissionOperations;
import org.springframework.credhub.core.permissionV2.CredHubPermissionV2Operations;
import org.springframework.web.client.RestTemplate;

/**
 * Specifies the main interaction with CredHub.
 *
 * @author Scott Frederick
 */
public interface CredHubOperations {
	/**
	 * Get the operations for saving, retrieving, and deleting credentials.
	 *
	 * @return the credentials operations
	 */
	CredHubCredentialOperations credentials();

	/**
	 * Get the operations for adding, retrieving, and deleting credential permissions.
	 *
	 * @return the permissions operations
	 */
	CredHubPermissionOperations permissions();

	/**
	 * Get the operations for adding, retrieving, and deleting credential permissions.
	 *
	 * @return the permissions operations
	 */
	CredHubPermissionV2Operations permissionsV2();

	/**
	 * Get the operations for retrieving, regenerating, and updating certificates.
	 *
	 * @return the certificates operations
	 */
	CredHubCertificateOperations certificates();

	/**
	 * Get the operations for interpolating service binding credentials.
	 *
	 * @return the interpolation operations
	 */
	CredHubInterpolationOperations interpolation();

	/**
	 * Get the operations for retrieving CredHub server information.
	 *
	 * @return the info operations
	 */
	CredHubInfoOperations info();

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
