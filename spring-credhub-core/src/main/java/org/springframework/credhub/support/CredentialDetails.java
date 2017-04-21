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

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.util.Assert;

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
public class CredentialDetails extends CredentialSummary {
	private String id;
	@JsonProperty("type")
	private ValueType valueType;
	private Object value;

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
	 * @param versionCreatedAt the {@link Date} when this version of the credential was
	 * created
	 */
	CredentialDetails(String id, CredentialName name, ValueType valueType,
					  Object value, Date versionCreatedAt) {
		super(name, versionCreatedAt);
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
	public Object getValue() {
		return this.value;
	}

	/**
	 * Create a builder for a {@link CredentialDetails} object. Intended for internal
	 * use in tests. Clients will get {@link CredentialDetails} objects populated from
	 * CredHub responses.
	 *
	 * @return the builder
	 */
	public static CredentialDetailsBuilder detailsBuilder() {
		return new CredentialDetailsBuilder();
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

	/**
	 * A builder that provides a fluent API for constructing {@link CredentialDetails}
	 * instances. Intended to be used internally for testing.
	 */
	public static class CredentialDetailsBuilder {
		private String id;
		private CredentialName name;
		private ValueType valueType;
		private Object value;
		private Date versionCreatedAt;

		CredentialDetailsBuilder() {
		}

		/**
		 * Set the ID of the credential.
		 *
		 * @param id the ID; must not be {@literal null}
		 * @return the builder
		 */
		public CredentialDetailsBuilder id(String id) {
			Assert.notNull(id, "id must not be null");
			this.id = id;
			return this;
		}

		/**
		 * Set the name of the credential.
		 *
		 * @param name the name; must not be {@literal null}
		 * @return the builder
		 */
		public CredentialDetailsBuilder name(CredentialName name) {
			Assert.notNull(name, "name must not be null");
			this.name = name;
			return this;
		}

		/**
		 * Set a password value and {@link ValueType#PASSWORD} type for the credential.
		 *
		 * @param value the password value; must not be {@literal null}
		 * @return the builder
		 */
		public CredentialDetailsBuilder passwordValue(String value) {
			Assert.notNull(value, "value must not be null");
			this.valueType = ValueType.PASSWORD;
			this.value = value;
			return this;
		}

		/**
		 * Set a JSON value and {@link ValueType#JSON} type for the credential.
		 *
		 * @param value the JSON value; must not be {@literal null}
		 * @return the builder
		 */
		public CredentialDetailsBuilder jsonValue(Map<String, Object> value) {
			Assert.notNull(value, "value must not be null");
			this.valueType = ValueType.JSON;
			this.value = value;
			return this;
		}

		/**
		 * Set a creation date for the credential.
		 *
		 * @param versionCreatedAt the creation date; must not be {@literal null}
		 * @return the builder
		 */
		public CredentialDetailsBuilder versionCreatedAt(Date versionCreatedAt) {
			Assert.notNull(versionCreatedAt, "versionCreatedAt must not be null");
			this.versionCreatedAt = versionCreatedAt;
			return this;
		}

		/**
		 * Construct a {@link CredentialDetails} from the provided values.
		 *
		 * @return a {@link CredentialDetails}
		 */
		public CredentialDetails build() {
			return new CredentialDetails(id, name, valueType, value, versionCreatedAt);
		}
	}
}
