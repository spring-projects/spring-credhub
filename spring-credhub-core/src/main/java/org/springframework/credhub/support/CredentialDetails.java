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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * The details of a credential that has been written to CredHub. Clients don't
 * typically instantiate objects of this type, but will receive them in response
 * to write and retrieve requests. The {@literal id} and {@literal name} fields
 * can be used in subsequent requests.
 *
 * @author Scott Frederick
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CredentialDetails<T> extends CredentialSummary {
	private String id;
	
	@JsonProperty("type")
	private ValueType valueType;

	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
	@JsonSubTypes({
			@Type(value = String.class, name = "password"),
			@Type(value = JsonCredential.class, name = "json")
	})
	private T value;

	/**
	 * Create a {@link CredentialDetails}.
	 */
	public CredentialDetails() {
	}

	/**
	 * Create a {@link CredentialDetails} from the provided parameters. Intended for
	 * internal use. Clients will get {@link CredentialDetails} objects populated from
	 * CredHub responses.
	 *
	 * @param id the CredHub-generated unique ID of the credential
	 * @param name the client-provided name of the credential
	 * @param valueType the {@link ValueType} of the credential
	 * @param value the client-provided value for the credential
	 * created
	 */
	public CredentialDetails(String id, CredentialName name, ValueType valueType, T value) {
		super(name);
		this.id = id;
		this.valueType = valueType;
		this.value = value;
	}

	/**
	 * Get the the CredHub-generated unique ID of the credential.
	 *
	 * @return the credential ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get the client-provided {@link ValueType} of the credential.
	 *
	 * @return the credential type
	 */
	public ValueType getValueType() {
		return this.valueType;
	}

	/**
	 * Get the client-provided value for the credential.
	 *
	 * @return the credential value
	 */
	public T getValue() {
		return this.value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CredentialDetails))
			return false;

		CredentialDetails that = (CredentialDetails) o;

		if (id != null ? !id.equals(that.id) : that.id != null)
			return false;
		if (valueType != that.valueType)
			return false;
		if (value != null ? !value.equals(that.value) : that.value != null)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
		result = 31 * result + (value != null ? value.hashCode() : 0);
		result = 31 * result
				+ (versionCreatedAt != null ? versionCreatedAt.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "CredentialDetails{"
				+ "id='" + id + '\''
				+ ", name=" + name
				+ ", valueType=" + valueType
				+ ", value=" + value
				+ ", versionCreatedAt='" + versionCreatedAt + '\'' +
				'}';
	}
}
