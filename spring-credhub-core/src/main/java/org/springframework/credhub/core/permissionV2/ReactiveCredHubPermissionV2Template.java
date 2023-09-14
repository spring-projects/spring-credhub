/*
 * Copyright 2016-2020 the original author or authors.
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

import reactor.core.publisher.Mono;

import org.springframework.credhub.core.ExceptionUtils;
import org.springframework.credhub.core.ReactiveCredHubOperations;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

/**
 * Implements the main interaction with CredHub to add, retrieve, and delete permissions.
 *
 * @author Scott Frederick
 * @author Alberto C. Ríos
 */
public class ReactiveCredHubPermissionV2Template implements ReactiveCredHubPermissionV2Operations {

	private static final String PERMISSIONS_URL_PATH = "/api/v2/permissions";

	private static final String PERMISSIONS_ID_URL_PATH = PERMISSIONS_URL_PATH + "/{id}";

	static final String PERMISSIONS_PATH_ACTOR_URL_QUERY = PERMISSIONS_URL_PATH + "?path={path}&actor={actor}";

	private final ReactiveCredHubOperations credHubOperations;

	/**
	 * Create a new {@link ReactiveCredHubPermissionV2Template}.
	 * @param credHubOperations the {@link ReactiveCredHubOperations} to use for
	 * interactions with CredHub
	 */
	public ReactiveCredHubPermissionV2Template(ReactiveCredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	@Override
	public Mono<CredentialPermission> getPermissions(final String id) {
		Assert.notNull(id, "credential ID must not be null");

		return this.credHubOperations.doWithWebClient((webClient) -> webClient.get()
			.uri(PERMISSIONS_ID_URL_PATH, id)
			.retrieve()
			.bodyToMono(CredentialPermission.class));
	}

	@Override
	public Mono<CredentialPermission> addPermissions(final CredentialName path, final Permission permission) {
		Assert.notNull(path, "credential path must not be null");
		Assert.notNull(permission, "credential permission must not be null");

		final CredentialPermission credentialPermission = new CredentialPermission(path, permission);

		return this.credHubOperations.doWithWebClient((webClient) -> webClient.post()
			.uri(PERMISSIONS_URL_PATH)
			.bodyValue(credentialPermission)
			.retrieve()
			.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
			.bodyToMono(CredentialPermission.class));
	}

	@Override
	public Mono<CredentialPermission> getPermissionsByPathAndActor(final CredentialName path, final Actor actor) {
		Assert.notNull(path, "credential path must not be null");
		Assert.notNull(actor, "credential actor must not be null");

		return this.credHubOperations.doWithWebClient((webClient) -> webClient.get()
			.uri(PERMISSIONS_PATH_ACTOR_URL_QUERY, path.getName(), actor.getIdentity())
			.retrieve()
			.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
			.bodyToMono(CredentialPermission.class));
	}

	@Override
	public Mono<CredentialPermission> updatePermissions(final String id, final CredentialName path,
			final Permission permission) {
		Assert.notNull(id, "credential ID must not be null");
		Assert.notNull(path, "credential path must not be null");
		Assert.notNull(permission, "credential permission must not be null");

		final CredentialPermission credentialPermission = new CredentialPermission(path, permission);

		return this.credHubOperations.doWithWebClient((webClient) -> webClient.put()
			.uri(PERMISSIONS_ID_URL_PATH, id)
			.bodyValue(credentialPermission)
			.retrieve()
			.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
			.bodyToMono(CredentialPermission.class));
	}

	@Override
	public Mono<Void> deletePermission(final String id) {
		Assert.notNull(id, "credential ID must not be null");

		return this.credHubOperations.doWithWebClient((webClient) -> webClient.delete()
			.uri(PERMISSIONS_ID_URL_PATH, id)
			.retrieve()
			.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
			.bodyToMono(Void.class));
	}

}
