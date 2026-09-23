/*
 * Copyright 2016-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.core.credential;

import java.util.List;
import java.util.Map;

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.ParametersRequest;

/**
 * Specifies the interactions with CredHub to save, generate, retrieve, and delete
 * credentials.
 *
 * @author Scott Frederick
 */
public interface CredHubCredentialOperations {

	/**
	 * Write a new credential to CredHub, or overwrite an existing credential with a new
	 * value.
	 * @param credentialRequest the credential to write to CredHub; must not be
	 * {@literal null}
	 * @param <T> the credential implementation type
	 * @return the details of the written credential
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_set_a_json_credential">CredHub
	 * API docs: Set a JSON Credential</a>
	 */
	<T> CredentialDetails<T> write(CredentialRequest<T> credentialRequest);

	/**
	 * Generate a new credential in CredHub, or overwrite an existing credential with a
	 * new generated value.
	 * @param parametersRequest the parameters of the new credential to generate in
	 * CredHub; must not be {@literal null}
	 * @param <T> the credential implementation type
	 * @param <P> the credential parameter implementation type
	 * @return the details of the generated credential
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_generate_a_password_credential">CredHub
	 * API docs: Generate a Password Credential</a>
	 */
	<T, P> CredentialDetails<T> generate(ParametersRequest<P> parametersRequest);

	/**
	 * Regenerate a credential in CredHub. Only credentials that were previously generated
	 * can be re-generated.
	 * @param <T> the credential implementation type
	 * @param name the name of the credential; must not be {@literal null}
	 * @param credentialType the type of the credential to be regenerated; must not be
	 * {@literal null}
	 * @return the details of the regenerated credential
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_regenerate_a_credential">CredHub
	 * API docs: Regenerate a Credential</a>
	 */
	<T> CredentialDetails<T> regenerate(CredentialName name, Class<T> credentialType);

	/**
	 * Regenerate a credential in CredHub, storing additional metadata alongside the new
	 * version. Only credentials that were previously generated can be re-generated.
	 * Metadata is only supported by CredHub server 2.6.0 and later; earlier server
	 * versions reject this field as an unrecognized request parameter.
	 * @param <T> the credential implementation type
	 * @param name the name of the credential; must not be {@literal null}
	 * @param credentialType the type of the credential to be regenerated; must not be
	 * {@literal null}
	 * @param metadata additional metadata to store with the credential
	 * @return the details of the regenerated credential
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_regenerate_a_credential">CredHub's
	 * metadata field on the regenerate request</a>
	 */
	<T> CredentialDetails<T> regenerate(CredentialName name, Class<T> credentialType, Map<String, Object> metadata);

	/**
	 * Retrieve a credential using its ID, as returned in a write request.
	 * @param id the ID of the credential; must not be {@literal null}
	 * @param credentialType the type of the credential to be retrieved; must not be
	 * {@literal null}
	 * @param <T> the credential implementation type
	 * @return the details of the retrieved credential
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_find_a_credential_by_id_type_value">CredHub
	 * API docs: Find a Credential by ID</a>
	 */
	<T> CredentialDetails<T> getById(String id, Class<T> credentialType);

	/**
	 * Retrieve a credential using its name, as passed to a write request. Only the
	 * current credential value will be returned. CredHub can return more than one current
	 * version for some credential types (for example, a certificate with both a
	 * {@literal current} and a {@literal transitional} version). In that case, only the
	 * first version in the response is returned here, specifically, the
	 * {@literal current}, non-transitional version, not the {@literal transitional} one.
	 * This ordering isn't a documented CredHub API contract, just an observed
	 * implementation detail of the server-side query this endpoint uses, so it could
	 * change without notice in a future CredHub release.
	 * @param name the name of the credential; must not be {@literal null}
	 * @param credentialType the type of credential expected to be returned
	 * @param <T> the credential implementation type
	 * @return the details of the retrieved credential
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_a_credential_by_name">CredHub
	 * API docs: Get a Credential by Name</a>
	 */
	<T> CredentialDetails<T> getByName(CredentialName name, Class<T> credentialType);

	/**
	 * Retrieve a credential using its name, as passed to a write request. A collection of
	 * all stored values for the named credential will be returned, including historical
	 * values.
	 * @param name the name of the credential; must not be {@literal null}
	 * @param credentialType the type of credential expected to be returned
	 * @param <T> the credential implementation type
	 * @return the details of the retrieved credential, including history
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_a_credential_by_name">CredHub
	 * API docs: Get a Credential by Name</a>
	 */
	<T> List<CredentialDetails<T>> getByNameWithHistory(CredentialName name, Class<T> credentialType);

	/**
	 * Retrieve a credential using its name, as passed to a write request. A collection of
	 * stored values for the named credential will be returned, with the specified number
	 * of historical values.
	 * @param name the name of the credential; must not be {@literal null}
	 * @param versions the number of historical versions to retrieve
	 * @param credentialType the type of credential expected to be returned
	 * @param <T> the credential implementation type
	 * @return the details of the retrieved credential, including history
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_a_credential_by_name">CredHub
	 * API docs: Get a Credential by Name</a>
	 */
	<T> List<CredentialDetails<T>> getByNameWithHistory(CredentialName name, int versions, Class<T> credentialType);

	/**
	 * Find a credential using a full or partial name.
	 * @param name the name of the credential; must not be {@literal null}
	 * @return a summary of the credential search results
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_find_a_credential_by_name_like">CredHub
	 * API docs: Find a Credential by Name-Like</a>
	 */
	List<CredentialSummary> findByName(CredentialName name);

	/**
	 * Find a credential using a path.
	 * @param path the path to the credential; must not be {@literal null}
	 * @return a summary of the credential search results
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_find_a_credential_by_path">CredHub
	 * API docs: Find a Credential by Path</a>
	 */
	List<CredentialSummary> findByPath(String path);

	/**
	 * Delete a credential by its full name.
	 * @param name the name of the credential; must not be {@literal null}
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_delete_a_credential">CredHub
	 * API docs: Delete a Credential</a>
	 */
	void deleteByName(CredentialName name);

}
