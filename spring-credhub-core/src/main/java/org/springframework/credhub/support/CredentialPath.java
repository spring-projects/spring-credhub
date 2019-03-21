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

import java.util.Objects;

/**
 * The path to a credential that has been written to CredHub. Clients don't typically
 * instantiate objects of this type, but will receive them in response to requests.
 *
 * @author Scott Frederick
 */
public class CredentialPath {
	private final String path;

	/**
	 * Create a {@link CredentialPath}. Intended for internal use.
	 */
	@SuppressWarnings("unused")
	private CredentialPath() {
		this.path = null;
	}

	/**
	 * Create a {@link CredentialPath} from the provided parameters. Intended for
	 * internal use. Clients will get {@link CredentialPath} objects populated from
	 * CredHub responses.
	 *
	 * @param path the name of the credential
	 */
	public CredentialPath(String path) {
		this.path = path;
	}

	/**
	 * Get the path to the credential.
	 *
	 * @return the credential path
	 */
	public String getPath() {
		return this.path;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CredentialPath))
			return false;

		CredentialPath that = (CredentialPath) o;

		return (path != null ? !path.equals(that.path) : that.path != null);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(path);
	}

	@Override
	public String toString() {
		return "CredentialPath{"
				+ "path=" + path
				+ '}';
	}
}
