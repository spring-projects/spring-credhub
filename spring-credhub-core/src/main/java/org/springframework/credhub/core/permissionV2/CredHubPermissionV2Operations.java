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

import java.util.List;

import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialPermission;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.credhub.support.permissions.Permission;

/**
 * Specifies the interactions with CredHub to add, retrieve, and delete permissions.
 *
 * @author Scott Frederick
 * @author Alberto C. Ríos
 */
public interface CredHubPermissionV2Operations {

	/**
	 * Get a permission.
	 * @param id the CredHub-assigned ID of the permission; must not be {@literal null}
	 * @return the details if the specified permission
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_a_v2_permission_by_uuid">CredHub
	 * API docs: Get a V2 Permission by UUID</a>
	 */
	CredentialPermission getPermissions(String id);

	/**
	 * Get a permission by path and actor.
	 * @param path the path of the credentials; must not be {@literal null}
	 * @param actor the actor of the credentials; must not be {@literal null}
	 * @return the details if the specified permission
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_a_v2_permission_by_actor_and_path">CredHub
	 * API docs: Get a V2 Permission by Actor and Path</a>
	 */
	CredentialPermission getPermissionsByPathAndActor(CredentialName path, Actor actor);

	/**
	 * Add permissions to a credential path.
	 * @param path the path of the credentials; must not be {@literal null}
	 * @param permission a permission to add
	 * @return the details if the added permission
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_create_a_v2_permission">CredHub
	 * API docs: Create a V2 Permission</a>
	 */
	CredentialPermission addPermissions(CredentialName path, Permission permission);

	/**
	 * Replace an existing permission, including its path and actor.
	 * @param id the CredHub-assigned ID of the permission; must not be {@literal null}
	 * @param path the path of the credentials; must not be {@literal null}
	 * @param permission a permission to add
	 * @return the details if the added permission
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_update_a_v2_permission">CredHub
	 * API docs: Update a V2 Permission</a>
	 */
	CredentialPermission updatePermissions(String id, CredentialName path, Permission permission);

	/**
	 * Partially update a permission, replacing only its operations, leaving its path and
	 * actor unchanged. Distinct from
	 * {@link #updatePermissions(String, CredentialName, Permission)}, which replaces the
	 * full permission.
	 * <p>
	 * Requires an underlying
	 * {@link org.springframework.http.client.ClientHttpRequestFactory} that supports the
	 * HTTP {@code PATCH} method. The JDK's default {@code HttpURLConnection}-based
	 * factory, used when {@code httpclient5} isn't on the classpath, does not support
	 * {@code PATCH} and will fail with a {@code java.net.ProtocolException} wrapped in a
	 * {@link org.springframework.web.client.ResourceAccessException}.
	 * @param id the CredHub-assigned ID of the permission; must not be {@literal null}
	 * @param operations the operations to replace the permission's operations with; must
	 * not be {@literal null}
	 * @return the details of the updated permission
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_update_a_v2_permission_operation">CredHub
	 * API docs: Update a V2 Permission Operation</a>
	 */
	CredentialPermission patchPermissions(String id, List<Operation> operations);

	/**
	 * Delete a permission.
	 * @param id the CredHub-assigned ID of the permission; must not be {@literal null}
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_delete_a_v2_permission">CredHub
	 * API docs: Delete a V2 Permission</a>
	 */
	void deletePermission(String id);

}
