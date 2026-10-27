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

package org.springframework.credhub.core.permissionV2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

/**
 * Default {@link CredHubPermissionV2Operations} implementation, written against a
 * {@link RestClient}.
 *
 * @author Roy Clarkson
 * @since 4.2.0
 */
public class DefaultCredHubPermissionV2Operations implements CredHubPermissionV2Operations {

	private static final String PERMISSIONS_URL_PATH = "/api/v2/permissions";

	private static final String PERMISSIONS_ID_URL_PATH = PERMISSIONS_URL_PATH + "/{id}";

	private static final String PERMISSIONS_PATH_ACTOR_URL_QUERY = PERMISSIONS_URL_PATH + "?path={path}&actor={actor}";

	private static final String OPERATIONS_REQUEST_FIELD = "operations";

	private final RestClient restClient;

	/**
	 * Create a new instance using the provided {@link RestClient}.
	 * @param restClient a {@link RestClient} configured for communication with CredHub,
	 * with error responses translated to
	 * {@link org.springframework.credhub.core.CredHubException}
	 */
	public DefaultCredHubPermissionV2Operations(RestClient restClient) {
		Assert.notNull(restClient, "restClient must not be null");
		this.restClient = restClient;
	}

	@Override
	public CredentialPermission getPermissions(String id) {
		Assert.notNull(id, "credential ID must not be null");

		return this.restClient.get().uri(PERMISSIONS_ID_URL_PATH, id).retrieve().body(CredentialPermission.class);
	}

	@Override
	public CredentialPermission getPermissionsByPathAndActor(CredentialName path, Actor actor) {
		Assert.notNull(path, "credential path must not be null");
		Assert.notNull(actor, "credential actor must not be null");

		return this.restClient.get()
			.uri(PERMISSIONS_PATH_ACTOR_URL_QUERY, path.getName(), actor.getIdentity())
			.retrieve()
			.body(CredentialPermission.class);
	}

	@Override
	public CredentialPermission addPermissions(CredentialName path, Permission permission) {
		Assert.notNull(path, "credential path must not be null");
		Assert.notNull(permission, "credential permission must not be null");

		return this.restClient.post()
			.uri(PERMISSIONS_URL_PATH)
			.body(new CredentialPermission(path, permission))
			.retrieve()
			.body(CredentialPermission.class);
	}

	@Override
	public CredentialPermission updatePermissions(String id, CredentialName path, Permission permission) {
		Assert.notNull(id, "credential ID must not be null");
		Assert.notNull(path, "credential path must not be null");
		Assert.notNull(permission, "credential permission must not be null");

		return this.restClient.put()
			.uri(PERMISSIONS_ID_URL_PATH, id)
			.body(new CredentialPermission(path, permission))
			.retrieve()
			.body(CredentialPermission.class);
	}

	@Override
	public CredentialPermission patchPermissions(String id, List<Operation> operations) {
		Assert.notNull(id, "credential ID must not be null");
		Assert.notNull(operations, "operations must not be null");

		Map<String, List<Operation>> request = new HashMap<>(1);
		request.put(OPERATIONS_REQUEST_FIELD, operations);

		return this.restClient.patch()
			.uri(PERMISSIONS_ID_URL_PATH, id)
			.body(request)
			.retrieve()
			.body(CredentialPermission.class);
	}

	@Override
	public void deletePermission(String id) {
		Assert.notNull(id, "credential ID must not be null");

		this.restClient.delete().uri(PERMISSIONS_ID_URL_PATH, id).retrieve().toBodilessEntity();
	}

}
