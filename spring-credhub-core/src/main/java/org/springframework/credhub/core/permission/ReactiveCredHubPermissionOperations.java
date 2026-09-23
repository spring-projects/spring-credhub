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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.Permission;

/**
 * Specifies the interactions with CredHub to add, retrieve, and delete permissions.
 * CredHub's own API docs mark this v1 permissions API as deprecated in favor of the
 * path-based v2 permissions API; see
 * {@link org.springframework.credhub.core.permissionV2.ReactiveCredHubPermissionV2Operations}.
 *
 * @author Scott Frederick
 */
public interface ReactiveCredHubPermissionOperations {

	/**
	 * Get the permissions associated with a credential.
	 * @param name the name of the credential; must not be {@literal null}
	 * @return the collection of permissions associated with the credential
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_a_v1_permission_by_actor_and_name">CredHub
	 * API docs: Get a V1 Permission by Actor and Name</a>
	 */
	Flux<Permission> getPermissions(CredentialName name);

	/**
	 * Add permissions to an existing credential.
	 * @param name the name of the credential; must not be {@literal null}
	 * @param permissions a collection of permissions to add
	 * @return an empty {@code Mono}
	 * @see <a href=
	 * "https://github.com/cloudfoundry/credhub/blob/eb8337a87ab8bd663f2fa4ab46f9ba65e7fdc908/backends/credhub/src/main/kotlin/org/cloudfoundry/credhub/permissions/PermissionsV1Controller.kt#L43-L55">CredHub's
	 * {@code POST /api/v1/permissions} handler, not covered by the official API docs</a>
	 */
	Mono<Void> addPermissions(CredentialName name, Permission... permissions);

	/**
	 * Delete a permission associated with a credential.
	 * @param name the name of the credential; must not be {@literal null}
	 * @param actor the actor of the permission; must not be {@literal null}
	 * @return an empty {@code Mono}
	 * @see <a href=
	 * "https://github.com/cloudfoundry/credhub/blob/eb8337a87ab8bd663f2fa4ab46f9ba65e7fdc908/backends/credhub/src/main/kotlin/org/cloudfoundry/credhub/permissions/PermissionsV1Controller.kt#L57-L69">CredHub's
	 * {@code DELETE /api/v1/permissions} handler, not covered by the official API
	 * docs</a>
	 */
	Mono<Void> deletePermission(CredentialName name, Actor actor);

}
