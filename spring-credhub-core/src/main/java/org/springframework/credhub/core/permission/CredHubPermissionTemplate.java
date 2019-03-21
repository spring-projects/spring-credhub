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

package org.springframework.credhub.core.permission;

import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialPermissions;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Implements the main interaction with CredHub to add, retrieve,
 * and delete permissions.
 *
 * @author Scott Frederick 
 */
public class CredHubPermissionTemplate implements CredHubPermissionOperations {
	static final String PERMISSIONS_URL_PATH = "/api/v1/permissions";
	static final String PERMISSIONS_URL_QUERY = PERMISSIONS_URL_PATH + "?credential_name={name}";
	static final String PERMISSIONS_ACTOR_URL_QUERY = PERMISSIONS_URL_QUERY + "&actor={actor}";

	private CredHubOperations credHubOperations;

	/**
	 * Create a new {@link CredHubPermissionTemplate}.
	 *
	 * @param credHubOperations the {@link CredHubOperations} to use for interactions with CredHub
	 */
	public CredHubPermissionTemplate(CredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	@Override
	public List<Permission> getPermissions(final CredentialName name) {
		Assert.notNull(name, "credential name must not be null");

		return credHubOperations.doWithRest(restOperations -> {
			ResponseEntity<CredentialPermissions> response =
					restOperations.getForEntity(PERMISSIONS_URL_QUERY,
							CredentialPermissions.class, name.getName());
			return response.getBody().getPermissions();
		});
	}

	@Override
	public void addPermissions(final CredentialName name,
							   final Permission... permissions) {
		Assert.notNull(name, "credential name must not be null");

		final CredentialPermissions credentialPermissions = new CredentialPermissions(name, permissions);

		credHubOperations.doWithRest(restOperations -> {
			restOperations.exchange(PERMISSIONS_URL_PATH, HttpMethod.POST,
					new HttpEntity<>(credentialPermissions),
					CredentialPermissions.class);
			return null;
		});
	}

	@Override
	public void deletePermission(final CredentialName name, final Actor actor) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(actor, "actor must not be null");

		credHubOperations.doWithRest(restOperations -> {
			restOperations.delete(PERMISSIONS_ACTOR_URL_QUERY, name.getName(), actor.getIdentity());
			return null;
		});
	}
}
