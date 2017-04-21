/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.util.Assert;

/**
 * A collection of {@link CredentialSummary}s. Clients don't typically instantiate
 * objects of this type, but will receive them in response to write and retrieve
 * requests.
 *
 * @author Scott Frederick
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CredentialSummaryData {
	private List<CredentialSummary> credentials;

	/**
	 * Create a {@link CredentialSummaryData}.
	 */
	CredentialSummaryData() {
	}

	/**
	 * Create a {@link CredentialSummaryData} from the provided parameters. Intended for internal
	 * use. Clients will get {@link CredentialSummaryData} objects populated from
	 * CredHub responses.
	 *
	 * @param credentials a collection of {@link CredentialSummary}s
	 */
	CredentialSummaryData(List<CredentialSummary> credentials) {
		this.credentials = credentials;
	}

	/**
	 * Get the collection of {@link CredentialSummary}s.
	 *
	 * @return the collection of {@link CredentialSummary}s
	 */
	public List<CredentialSummary> getCredentials() {
		return this.credentials;
	}

	/**
	 * Create a builder for a {@link CredentialSummaryData} object. Intended for internal
	 * use. Clients will get {@link CredentialSummaryData} objects populated from
	 * CredHub responses.
	 *
	 * @return the builder
	 */
	public static CredentialSummaryDataBuilder builder() {
		return new CredentialSummaryDataBuilder();
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
		int result = super.hashCode();
		result = 31 * result + (credentials != null ? credentials.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "CredentialSummaryResponse{"
				+ "credentials=" + credentials
				+ '}';
	}

	/**
	 * Create a builder for a {@link CredentialSummaryData} object. Intended for internal
	 * use. Clients will get {@link CredentialSummaryData} objects populated from
	 * CredHub responses.
	 *
	 * @return the builder
	 */
	public static class CredentialSummaryDataBuilder {
		private List<CredentialSummary> credentialSummaries;

		CredentialSummaryDataBuilder() {
		}

		/**
		 * Add a {@link CredentialSummary} to the collection of summaries.
		 *
		 * @param credential the {@link CredentialSummary} to add; must not be
		 * {@literal null}
		 * @return the builder
		 */
		public CredentialSummaryDataBuilder credential(CredentialSummary credential) {
			Assert.notNull(credential, "credential must not be null");
			initCredentials();
			this.credentialSummaries.add(credential);
			return this;
		}

		/**
		 * Add a collection of {@link CredentialSummary}s to the collection of summaries.
		 *
		 * @param credentials the {@link CredentialSummary}s to add; must not be
		 * {@literal null}
		 * @return the builder
		 */
		public CredentialSummaryDataBuilder credentials(
				Collection<? extends CredentialSummary> credentials) {
			Assert.notNull(credentials, "credentials must not be null");
			initCredentials();
			this.credentialSummaries.addAll(credentials);
			return this;
		}

		private void initCredentials() {
			if (this.credentialSummaries == null) {
				this.credentialSummaries = new ArrayList<CredentialSummary>();
			}
		}

		/**
		 * Construct a {@link CredentialSummaryData} from the provided values.
		 *
		 * @return a {@link CredentialSummaryData}
		 */
		public CredentialSummaryData build() {
			List<CredentialSummary> credentialSummaries;
			switch (this.credentialSummaries == null ? 0
					: this.credentialSummaries.size()) {
			case 0:
				credentialSummaries = java.util.Collections.emptyList();
				break;
			case 1:
				credentialSummaries = java.util.Collections
						.singletonList(this.credentialSummaries.get(0));
				break;
			default:
				credentialSummaries = java.util.Collections.unmodifiableList(
						new ArrayList<CredentialSummary>(this.credentialSummaries));
			}

			return new CredentialSummaryData(credentialSummaries);
		}
	}

}
