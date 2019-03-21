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

package org.springframework.credhub.support;

import java.util.Date;

/**
 * A summary of a credential that has been written to CredHub. Clients don't typically
 * instantiate objects of this type, but will receive them in response to write and
 * retrieve requests. The {@literal name} field can be used in subsequent requests.
 *
 * @author Scott Frederick
 */
public class CredentialSummary {
	protected CredentialName name;
	protected Date versionCreatedAt;

	/**
	 * Create a {@link CredentialSummary}. Intended for internal use.
	 */
	CredentialSummary() {
	}

	/**
	 * Create a {@link CredentialSummary} from the provided parameters. Intended for
	 * internal use. Clients will get {@link CredentialSummary} objects populated from
	 * CredHub responses.
	 *
	 * @param name the name of the credential
	 */
	public CredentialSummary(CredentialName name) {
		this.name = name;
		this.versionCreatedAt = new Date();
	}

	/**
	 * Get the client-provided name of the credential.
	 *
	 * @return the credential name
	 */
	public CredentialName getName() {
		return this.name;
	}

	/**
	 * Get the CredHub-generated {@link Date} when this version of the credential was created.
	 *
	 * @return the credential version creation {@link Date}
	 */
	public Date getVersionCreatedAt() {
		return this.versionCreatedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CredentialSummary))
			return false;

		CredentialSummary that = (CredentialSummary) o;

		if (name != null ? !name.equals(that.name) : that.name != null)
			return false;
		return versionCreatedAt != null ? versionCreatedAt.equals(that.versionCreatedAt)
				: that.versionCreatedAt == null;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result
				+ (versionCreatedAt != null ? versionCreatedAt.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "CredentialSummary{"
				+ "name=" + name
				+ ", versionCreatedAt='" + versionCreatedAt + '\''
				+ '}';
	}
}
