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

import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.Permission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Specifies the interactions with CredHub to add, retrieve, and delete permissions.
 *
 * @author Scott Frederick
 */
public interface ReactiveCredHubPermissionOperations {
	/**
	 * Get the permissions associated with a credential.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 * @return the collection of permissions associated with the credential
	 */
	Flux<Permission> getPermissions(final CredentialName name);

	/**
	 * Add permissions to an existing credential.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 * @param permissions a collection of permissions to add
	 * @return an empty {@code Mono}
	 */
	Mono<Void> addPermissions(final CredentialName name, final Permission... permissions);

	/**
	 * Delete a permission associated with a credential.
	 *
	 * @param name the name of the credential; must not be {@literal null}
	 * @param actor the actor of the permission; must not be {@literal null}
	 * @return an empty {@code Mono}
	 */
	Mono<Void> deletePermission(final CredentialName name, final Actor actor);
}
