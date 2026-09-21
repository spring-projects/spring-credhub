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

package org.springframework.credhub.support;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import org.jspecify.annotations.Nullable;

/**
 * The details of a credential that has been written to CredHub.
 *
 * Clients don't typically instantiate objects of this type, but will receive them in
 * response to write and retrieve requests. The {@literal id} and {@literal name} fields
 * can be used in subsequent requests.
 *
 * @param <T> the type of CredHub credential
 * @author Scott Frederick
 */
public class CredentialDetails<T> extends CredentialSummary {

	private final @Nullable String id;

	@JsonProperty("type")
	private final @Nullable CredentialType credentialType;

	@JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "type")
	private final @Nullable T value;

	private final @Nullable Map<String, Object> metadata;

	/**
	 * Create a {@link CredentialDetails}.
	 */
	public CredentialDetails() {
		this.id = null;
		this.credentialType = null;
		this.value = null;
		this.metadata = null;
	}

	/**
	 * Create a {@link CredentialDetails} from the provided parameters. Intended for
	 * internal use. Clients will get {@link CredentialDetails} objects populated from
	 * CredHub responses.
	 * @param id the CredHub-generated unique ID of the credential
	 * @param name the client-provided name of the credential
	 * @param credentialType the {@link CredentialType} of the credential
	 * @param value the client-provided value for the credential created
	 */
	public CredentialDetails(String id, CredentialName name, CredentialType credentialType, T value) {
		this(id, name, credentialType, null, value);
	}

	/**
	 * Create a {@link CredentialDetails} from the provided parameters. Intended for
	 * internal use. Clients will get {@link CredentialDetails} objects populated from
	 * CredHub responses.
	 * @param id the CredHub-generated unique ID of the credential
	 * @param name the client-provided name of the credential
	 * @param credentialType the {@link CredentialType} of the credential
	 * @param metadata the additional metadata stored with the credential
	 * @param value the client-provided value for the credential created
	 */
	public CredentialDetails(String id, CredentialName name, CredentialType credentialType,
			Map<String, Object> metadata, T value) {
		super(name);
		this.id = id;
		this.credentialType = credentialType;
		this.metadata = metadata;
		this.value = value;
	}

	/**
	 * Get the CredHub-generated unique ID of the credential.
	 * @return the credential ID
	 */
	public @Nullable String getId() {
		return this.id;
	}

	/**
	 * Get the client-provided {@link CredentialType} of the credential.
	 * @return the credential type
	 */
	public @Nullable CredentialType getCredentialType() {
		return this.credentialType;
	}

	/**
	 * Get the client-provided value for the credential.
	 * @return the credential value
	 */
	public @Nullable T getValue() {
		return this.value;
	}

	/**
	 * Get the additional metadata stored with the credential. Only supported by CredHub
	 * server 2.6.0 and later; earlier server versions never populate this value.
	 * @return the credential metadata
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_find_a_credential_by_id_type_value">CredHub's
	 * metadata field on credential responses</a>
	 */
	public @Nullable Map<String, Object> getMetadata() {
		return this.metadata;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CredentialDetails)) {
			return false;
		}

		CredentialDetails<?> that = (CredentialDetails<?>) o;

		if ((this.id != null) ? !this.id.equals(that.id) : (that.id != null)) {
			return false;
		}
		if (this.credentialType != that.credentialType) {
			return false;
		}
		if ((this.value != null) ? !this.value.equals(that.value) : (that.value != null)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.credentialType, this.value, this.versionCreatedAt, this.metadata);
	}

	@Override
	public String toString() {
		return "CredentialDetails{" + "id='" + this.id + '\'' + ", name=" + this.name + ", credentialType="
				+ this.credentialType + ", value=" + this.value + ", versionCreatedAt='" + this.versionCreatedAt + '\''
				+ ", metadata=" + this.metadata + '}';
	}

}
