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
 * A collection of {@link CredentialSummary}s. Clients don't typically instantiate
 * objects of this type, but will receive them in response to write and retrieve
 * requests.
 *
 * @author Scott Frederick
 */
public class CredentialSummaryData {
	private final List<CredentialSummary> credentials;

	/**
	 * Create a {@link CredentialSummaryData}.
	 */
	@SuppressWarnings("unused")
	private CredentialSummaryData() {
		this.credentials = null;
	}

	/**
	 * Create a {@link CredentialSummaryData} from the provided parameters. Intended for internal
	 * use. Clients will get {@link CredentialSummaryData} objects populated from
	 * CredHub responses.
	 *
	 * @param credentials a collection of {@link CredentialSummary}s
	 */
	public CredentialSummaryData(CredentialSummary... credentials) {
		this.credentials = Arrays.asList(credentials);
	}

	/**
	 * Get the collection of {@link CredentialSummary}s.
	 *
	 * @return the collection of {@link CredentialSummary}s
	 */
	public List<CredentialSummary> getCredentials() {
		return this.credentials;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CredentialSummaryData))
			return false;
		if (!super.equals(o))
			return false;

		CredentialSummaryData that = (CredentialSummaryData) o;

		return credentials != null ? credentials.equals(that.credentials)
				: that.credentials == null;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(credentials);
	}

	@Override
	public String toString() {
		return "CredentialSummaryData{"
				+ "credentials=" + credentials
				+ '}';
	}
}
