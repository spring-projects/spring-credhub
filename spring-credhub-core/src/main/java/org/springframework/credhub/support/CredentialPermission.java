/*
 *
 * Copyright 2013-2017 the original author or authors.
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
 * See the License for the specific language governing permission and
 * limitations under the License.
 *
 */

package org.springframework.credhub.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.credhub.support.permissions.Permission;

import java.util.Objects;

/**
 * A {@link Permission} associated with a credential. Clients don't typically instantiate
 * objects of this type, but will receive them in response to write and retrieve
 * requests.
 *
 * @author Scott Frederick
 */
public class CredentialPermission {
	@JsonProperty("uuid")
	private final String uuid;
	private final CredentialName path;

	@JsonUnwrapped
	private final Permission permission;

	/**
	 * Create a {@link CredentialPermission}.
	 */
	@SuppressWarnings("unused")
	private CredentialPermission() {
		this.uuid = null;
		this.path = null;
		this.permission = null;
	}

	/**
	 * Create a {@link CredentialPermission} from the provided parameters. Intended for internal
	 * use. Clients will get {@link CredentialPermission} objects populated from
	 * CredHub responses.
	 *
	 * @param path the path of the credential(s) that the permission will apply to
	 * @param permission a collection of {@link Permission}s
	 */
	public CredentialPermission(CredentialName path, Permission permission) {
		this.path = path;
		this.permission = permission;
		this.uuid = null;
	}

	/**
	 * Get the CredHub-assigned ID of the permission.
	 *
	 * @return the permission ID
	 */
	public String getId() {
		return this.uuid;
	}

	/**
	 * Get the name of the credential that the permission apply to.
	 *
	 * @return the credential name
	 */
	public String getPath() {
		return this.path.getName();
	}

	/**
	 * Get the collection of {@link Permission}s.
	 *
	 * @return the collection of {@link Permission}s
	 */
	public Permission getPermission() {
		return this.permission;
	}

	@Override
	public String toString() {
		return "CredentialPermissions{"
				+ "uuid=" + uuid
				+ ", path=" + path
				+ ", permission=" + permission
				+ '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CredentialPermission)) return false;

		CredentialPermission that = (CredentialPermission) o;

		if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null)
			return false;
		if (path != null ? !path.equals(that.path) : that.path != null)
			return false;
		return permission != null ? permission.equals(that.permission) : that.permission == null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, path, permission);
	}
}
