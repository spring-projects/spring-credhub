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

package org.springframework.credhub.core.permissionV2;

import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

/**
 * Implements the main interaction with CredHub to add, retrieve,
 * and delete permissions.
 *
 * @author Scott Frederick 
 */
public class CredHubPermissionV2Template implements CredHubPermissionV2Operations {
	static final String PERMISSIONS_URL_PATH = "/api/v2/permissions";
	static final String PERMISSIONS_ID_URL_PATH = PERMISSIONS_URL_PATH + "/{id}";

	private CredHubOperations credHubOperations;

	/**
	 * Create a new {@link CredHubPermissionV2Template}.
	 *
	 * @param credHubOperations the {@link CredHubOperations} to use for interactions with CredHub
	 */
	public CredHubPermissionV2Template(CredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	@Override
	public CredentialPermission getPermissions(final String id) {
		Assert.notNull(id, "credential ID must not be null");

		return credHubOperations.doWithRest(restOperations -> {
			ResponseEntity<CredentialPermission> response =
					restOperations.getForEntity(PERMISSIONS_ID_URL_PATH,
							CredentialPermission.class, id);
			return response.getBody();
		});
	}

	@Override
	public CredentialPermission addPermissions(final CredentialName path,
											   final Permission permission) {
		Assert.notNull(path, "credential path must not be null");
		Assert.notNull(permission, "credential permission must not be null");

		final CredentialPermission credentialPermission = new CredentialPermission(path, permission);

		return credHubOperations.doWithRest(restOperations -> {
			ResponseEntity<CredentialPermission> response =
					restOperations.exchange(PERMISSIONS_URL_PATH, HttpMethod.POST,
							new HttpEntity<>(credentialPermission),
							CredentialPermission.class);
			return response.getBody();
		});
	}

	@Override
	public CredentialPermission updatePermissions(final String id, final CredentialName path,
												  final Permission permission) {
		Assert.notNull(id, "credential ID must not be null");
		Assert.notNull(path, "credential path must not be null");
		Assert.notNull(permission, "credential permission must not be null");

		final CredentialPermission credentialPermission = new CredentialPermission(path, permission);

		return credHubOperations.doWithRest(restOperations -> {
			ResponseEntity<CredentialPermission> response =
					restOperations.exchange(PERMISSIONS_ID_URL_PATH, HttpMethod.PUT,
							new HttpEntity<>(credentialPermission),
							CredentialPermission.class, id);
			return response.getBody();
		});
	}

	@Override
	public void deletePermission(final String id) {
		Assert.notNull(id, "credential ID must not be null");

		credHubOperations.doWithRest(restOperations -> {
			restOperations.delete(PERMISSIONS_ID_URL_PATH, id);
			return null;
		});
	}
}
