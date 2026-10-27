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

package org.springframework.credhub.core.permission;

import java.util.List;

import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialPermissions;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

/**
 * Default {@link CredHubPermissionOperations} implementation, written against a
 * {@link RestClient}.
 *
 * @author Roy Clarkson
 * @since 4.2.0
 */
public class DefaultCredHubPermissionOperations implements CredHubPermissionOperations {

	private static final String PERMISSIONS_URL_PATH = "/api/v1/permissions";

	private static final String PERMISSIONS_URL_QUERY = PERMISSIONS_URL_PATH + "?credential_name={name}";

	private static final String PERMISSIONS_ACTOR_URL_QUERY = PERMISSIONS_URL_QUERY + "&actor={actor}";

	private final RestClient restClient;

	/**
	 * Create a new instance using the provided {@link RestClient}.
	 * @param restClient a {@link RestClient} configured for communication with CredHub,
	 * with error responses translated to
	 * {@link org.springframework.credhub.core.CredHubException}
	 */
	public DefaultCredHubPermissionOperations(RestClient restClient) {
		Assert.notNull(restClient, "restClient must not be null");
		this.restClient = restClient;
	}

	@Override
	public List<Permission> getPermissions(CredentialName name) {
		Assert.notNull(name, "credential name must not be null");

		return this.restClient.get()
			.uri(PERMISSIONS_URL_QUERY, name.getName())
			.retrieve()
			.body(CredentialPermissions.class)
			.getPermissions();
	}

	@Override
	public void addPermissions(CredentialName name, Permission... permissions) {
		Assert.notNull(name, "credential name must not be null");

		this.restClient.post()
			.uri(PERMISSIONS_URL_PATH)
			.body(new CredentialPermissions(name, permissions))
			.retrieve()
			.toBodilessEntity();
	}

	@Override
	public void deletePermission(CredentialName name, Actor actor) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(actor, "actor must not be null");

		this.restClient.delete()
			.uri(PERMISSIONS_ACTOR_URL_QUERY, name.getName(), actor.getIdentity())
			.retrieve()
			.toBodilessEntity();
	}

}
