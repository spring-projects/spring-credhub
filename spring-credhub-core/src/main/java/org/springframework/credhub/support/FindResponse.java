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

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FindResponse extends CredHubResponse {
	private List<FoundCredential> foundCredentials;

	FindResponse(List<FoundCredential> foundCredentials) {
		this.foundCredentials = foundCredentials;
	}

	public FindResponse(String errorMessage, List<FoundCredential> foundCredentials) {
		super(errorMessage);
		this.foundCredentials = foundCredentials;
	}

	public static FindResponseBuilder builder() {
		return new FindResponseBuilder();
	}

	public List<FoundCredential> getFoundCredentials() {
		return this.foundCredentials;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FindResponse)) return false;
		if (!super.equals(o)) return false;

		FindResponse that = (FindResponse) o;

		return foundCredentials != null ? foundCredentials.equals(that.foundCredentials) : that.foundCredentials == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (foundCredentials != null ? foundCredentials.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "FindResponse{" +
				"errorMessage='" + errorMessage + '\'' +
				", foundCredentials=" + foundCredentials +
				'}';
	}

	public static class FindResponseBuilder {
		private String errorMessage;
		private ArrayList<FoundCredential> foundCredentials;

		FindResponseBuilder() {
		}

		public FindResponse.FindResponseBuilder foundCredential(FoundCredential foundCredential) {
			initFoundCredentials();
			this.foundCredentials.add(foundCredential);
			return this;
		}

		public FindResponse.FindResponseBuilder foundCredentials(Collection<? extends FoundCredential> foundCredentials) {
			initFoundCredentials();
			this.foundCredentials.addAll(foundCredentials);
			return this;
		}

		private void initFoundCredentials() {
			if (this.foundCredentials == null) {
				this.foundCredentials = new ArrayList<FoundCredential>();
			}
		}

		public FindResponse build() {
			List<FoundCredential> foundCredentials;
			switch (this.foundCredentials == null ? 0 : this.foundCredentials.size()) {
				case 0:
					foundCredentials = java.util.Collections.emptyList();
					break;
				case 1:
					foundCredentials = java.util.Collections.singletonList(this.foundCredentials.get(0));
					break;
				default:
					foundCredentials = java.util.Collections.unmodifiableList(new ArrayList<FoundCredential>(this.foundCredentials));
			}

			return new FindResponse(errorMessage, foundCredentials);
		}

		@Override
		public String toString() {
			return "FindResponseBuilder{" +
					"foundCredentials=" + foundCredentials +
					'}';
		}

		public FindResponseBuilder errorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
			return this;
		}
	}

	public static class FoundCredential {
		private CredentialName name;
		private String versionCreatedAt;

		FoundCredential(CredentialName name, String versionCreatedAt) {
			this.name = name;
			this.versionCreatedAt = versionCreatedAt;
		}

		public static FoundCredentialBuilder builder() {
			return new FoundCredentialBuilder();
		}

		public CredentialName getName() {
			return this.name;
		}

		public String getVersionCreatedAt() {
			return this.versionCreatedAt;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof FoundCredential)) return false;

			FoundCredential that = (FoundCredential) o;

			if (name != null ? !name.equals(that.name) : that.name != null) return false;
			return versionCreatedAt != null ? versionCreatedAt.equals(that.versionCreatedAt) : that.versionCreatedAt == null;
		}

		@Override
		public int hashCode() {
			int result = name != null ? name.hashCode() : 0;
			result = 31 * result + (versionCreatedAt != null ? versionCreatedAt.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "FoundCredential{" +
					"name=" + name +
					", versionCreatedAt='" + versionCreatedAt + '\'' +
					'}';
		}

		public static class FoundCredentialBuilder {
			private CredentialName name;
			private String versionCreatedAt;

			FoundCredentialBuilder() {
			}

			public FoundCredential.FoundCredentialBuilder name(CredentialName name) {
				this.name = name;
				return this;
			}

			public FoundCredential.FoundCredentialBuilder versionCreatedAt(String versionCreatedAt) {
				this.versionCreatedAt = versionCreatedAt;
				return this;
			}

			public FoundCredential build() {
				return new FoundCredential(name, versionCreatedAt);
			}

			@Override
			public String toString() {
				return "FoundCredentialBuilder{" +
						"name=" + name +
						", versionCreatedAt='" + versionCreatedAt + '\'' +
						'}';
			}
		}
	}
}
