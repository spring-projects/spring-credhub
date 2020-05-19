/*
 * Copyright 2016-2020 the original author or authors.
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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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

	private final String id;

	@JsonProperty("type")
	private final CredentialType credentialType;

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
	private final T value;

	/**
	 * Create a {@link CredentialDetails}.
	 */
	public CredentialDetails() {
		this.id = null;
		this.credentialType = null;
		this.value = null;
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
		super(name);
		this.id = id;
		this.credentialType = credentialType;
		this.value = value;
	}

	/**
	 * Get the the CredHub-generated unique ID of the credential.
	 * @return the credential ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get the client-provided {@link CredentialType} of the credential.
	 * @return the credential type
	 */
	public CredentialType getCredentialType() {
		return this.credentialType;
	}

	/**
	 * Get the client-provided value for the credential.
	 * @return the credential value
	 */
	public T getValue() {
		return this.value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CredentialDetails)) {
			return false;
		}

		CredentialDetails that = (CredentialDetails) o;

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
		return Objects.hash(this.id, this.name, this.credentialType, this.value, this.versionCreatedAt);
	}

	@Override
	public String toString() {
		return "CredentialDetails{" + "id='" + this.id + '\'' + ", name=" + this.name + ", credentialType="
				+ this.credentialType + ", value=" + this.value + ", versionCreatedAt='" + this.versionCreatedAt + '\''
				+ '}';
	}

}
