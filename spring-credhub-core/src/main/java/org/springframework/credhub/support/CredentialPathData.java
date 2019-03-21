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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A collection of {@link CredentialPath}s. Clients don't typically instantiate
 * objects of this type, but will receive them in response to requests.
 *
 * @author Scott Frederick
 */
public class CredentialPathData {
	private final List<CredentialPath> paths;

	/**
	 * Create a {@link CredentialPathData}.
	 */
	@SuppressWarnings("unused")
	private CredentialPathData() {
		this.paths = null;
	}

	/**
	 * Create a {@link CredentialPathData} from the provided parameters. Intended for internal
	 * use. Clients will get {@link CredentialPathData} objects populated from
	 * CredHub responses.
	 *
	 * @param paths a collection of {@link CredentialPath}s
	 */
	public CredentialPathData(CredentialPath... paths) {
		this.paths = Arrays.asList(paths);
	}

	/**
	 * Get the collection of {@link CredentialPath}s.
	 *
	 * @return the collection of {@link CredentialPath}s
	 */
	public List<CredentialPath> getPaths() {
		return this.paths;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CredentialPathData))
			return false;
		if (!super.equals(o))
			return false;

		CredentialPathData that = (CredentialPathData) o;

		return paths != null ? paths.equals(that.paths)
				: that.paths == null;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(paths);
	}

	@Override
	public String toString() {
		return "CredentialPathData{"
				+ "paths=" + paths
				+ '}';
	}
}
