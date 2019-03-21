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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.credhub.support;

import org.springframework.credhub.support.permissions.CredentialPermission;

import java.util.Arrays;
import java.util.List;

/**
 * A collection of {@link CredentialPermission}s. Clients don't typically instantiate
 * objects of this type, but will receive them in response to write and retrieve
 * requests.
 *
 * @author Scott Frederick
 */
public class CredentialPermissions {
	private CredentialName credentialName;
	private List<CredentialPermission> permissions;

	/**
	 * Create a {@link CredentialPermissions}.
	 */
	public CredentialPermissions() {
	}

	/**
	 * Create a {@link CredentialPermissions} from the provided parameters. Intended for internal
	 * use. Clients will get {@link CredentialPermissions} objects populated from
	 * CredHub responses.
	 *
	 * @param credentialName the name of the credential that the permissions will apply to
	 * @param permissions a collection of {@link CredentialPermission}s
	 */
	public CredentialPermissions(CredentialName credentialName, CredentialPermission... permissions) {
		this.credentialName = credentialName;
		this.permissions = Arrays.asList(permissions);
	}

	/**
	 * Get the name of the credential that the permissions apply to.
	 *
	 * @return the credential name
	 */
	public String getCredentialName() {
		return this.credentialName.getName();
	}

	/**
	 * Get the collection of {@link CredentialPermission}s.
	 *
	 * @return the collection of {@link CredentialPermission}s
	 */
	public List<CredentialPermission> getPermissions() {
		return this.permissions;
	}

	@Override
	public String toString() {
		return "CredentialPermissions{"
				+ "credentialName=" + credentialName
				+ ", permissions=" + permissions
				+ '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CredentialPermissions)) return false;

		CredentialPermissions that = (CredentialPermissions) o;

		if (credentialName != null ? !credentialName.equals(that.credentialName) : that.credentialName != null)
			return false;
		return permissions != null ? permissions.equals(that.permissions) : that.permissions == null;
	}

	@Override
	public int hashCode() {
		int result = credentialName != null ? credentialName.hashCode() : 0;
		result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
		return result;
	}
}
