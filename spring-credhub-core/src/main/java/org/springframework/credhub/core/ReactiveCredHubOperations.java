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

import org.reactivestreams.Publisher;
import org.springframework.credhub.core.certificate.ReactiveCredHubCertificateOperations;
import org.springframework.credhub.core.credential.ReactiveCredHubCredentialOperations;
import org.springframework.credhub.core.info.ReactiveCredHubInfoOperations;
import org.springframework.credhub.core.interpolation.ReactiveCredHubInterpolationOperations;
import org.springframework.credhub.core.permission.ReactiveCredHubPermissionOperations;
import org.springframework.credhub.core.permissionV2.ReactiveCredHubPermissionV2Operations;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Function;

/**
 * Specifies the main interaction with CredHub.
 *
 * @author Scott Frederick
 */
public interface ReactiveCredHubOperations {
	/**
	 * Get the operations for saving, retrieving, and deleting credentials.
	 *
	 * @return the credentials operations
	 */
	ReactiveCredHubCredentialOperations credentials();

	/**
	 * Get the operations for adding, retrieving, and deleting credential permissions.
	 *
	 * @return the permissions operations
	 */
	ReactiveCredHubPermissionOperations permissions();

	/**
	 * Get the operations for adding, retrieving, and deleting credential permissions.
	 *
	 * @return the permissions operations
	 */
	ReactiveCredHubPermissionV2Operations permissionsV2();

	/**
	 * Get the operations for retrieving, regenerating, and updating certificates.
	 *
	 * @return the certificates operations
	 */
	ReactiveCredHubCertificateOperations certificates();

	/**
	 * Get the operations for interpolating service binding credentials.
	 *
	 * @return the interpolation operations
	 */
	ReactiveCredHubInterpolationOperations interpolation();

	/**
	 * Get the operations for retrieving CredHub server information.
	 *
	 * @return the info operations
	 */
	ReactiveCredHubInfoOperations info();

	/**
	 * Allow interaction with the configured {@link WebClient} not provided
	 * by other methods.
	 *
	 * @param callback wrapper for the callback method
	 * @param <V> the publisher type
	 * @param <T> the credential implementation type
	 * @return the return value from the callback method
	 */
	<V, T extends Publisher<V>> T doWithWebClient(Function<WebClient, ? extends T> callback);
}
