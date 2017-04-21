/*
 *
 *  * Copyright 2013-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.credhub.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.util.Assert;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static org.springframework.credhub.support.ValueType.JSON;

/**
 * The details of a request to write a new or update an existing credential in CredHub.
 *
 * @author Scott Frederick
 */
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WriteRequest {
	private boolean overwrite;
	private CredentialName name;
	private ValueType valueType;
	private Object value;
	@JsonInclude(NON_EMPTY)
	private List<AccessControlEntry> accessControlEntries;

	/**
	 * Create a {@link WriteRequest} from the provided parameters. Intended for internal
	 * use. Clients should use {@link #builder()} to construct instances of this class.
	 *
	 * @param name the name of the credential
	 * @param overwrite {@literal false} to create a new credential, or
	 * {@literal true} to update and existing credential
	 * @param value the value of the credential
	 * @param valueType the {@link ValueType} of the credential
	 * @param accessControlEntries requirements for access control for the credential
	 */
	private WriteRequest(CredentialName name, boolean overwrite,
						 Object value, ValueType valueType,
						 List<AccessControlEntry> accessControlEntries) {
		this.name = name;
		this.overwrite = overwrite;
		this.valueType = valueType;
		this.value = value;
		this.accessControlEntries = accessControlEntries;
	}

	/**
	 * Get the value of the {@literal boolean} flag indicating whether the CredHub
	 * should create a new credential or update an existing credential.
	 *
	 * @return the {@literal boolean} overwrite value
	 */
	public boolean isOverwrite() {
		return this.overwrite;
	}


	/**
	 * Get the {@link CredentialName} of the credential.
	 *
	 * @return the name of the credential
	 */
	@JsonInclude
	public String getName() {
		return name.getName();
	}

	/**
	 * Get the value of the credential.
	 *
	 * @return the value of the credential
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Get the {@link ValueType} of the credential.
	 *
 	 * @return the type of the credential
	 */
	public String getType() {
		return valueType.type();
	}

	/**
	 * Get the set of {@link AccessControlEntry} to assign to the credential.
	 *
	 * @return the set of {@link AccessControlEntry}
	 */
	public List<AccessControlEntry> getAccessControlEntries() {
		return this.accessControlEntries;
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link WriteRequest}.
	 *
	 * @return a builder
	 */
	public static WriteRequestBuilder builder() {
		return new WriteRequestBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof WriteRequest))
			return false;

		WriteRequest that = (WriteRequest) o;

		if (overwrite != that.overwrite)
			return false;
		if (!name.equals(that.name))
			return false;
		if (valueType != that.valueType)
			return false;
		if (!value.equals(that.value))
			return false;
		return accessControlEntries.equals(that.accessControlEntries);
	}

	@Override
	public int hashCode() {
		int result = (overwrite ? 1 : 0);
		result = 31 * result + name.hashCode();
		result = 31 * result + valueType.hashCode();
		result = 31 * result + value.hashCode();
		result = 31 * result + accessControlEntries.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "WriteRequest{"
				+ "overwrite=" + overwrite
				+ ", name=" + name
				+ ", valueType=" + valueType
				+ ", value=" + value
				+ ", accessControlEntries=" + accessControlEntries
				+ '}';
	}

	/**
	 * A builder that provides a fluent API for constructing {@link WriteRequest}s.
	 */
	public static class WriteRequestBuilder {
		private CredentialName name;
		private boolean overwrite;
		private Object value;
		private ValueType valueType;
		private ArrayList<AccessControlEntry> accessControlEntries;

		/**
		 * Create a {@link WriteRequestBuilder}. Intended for internal use.
		 */
		WriteRequestBuilder() {
		}

		/**
		 * Set the value of a password credential. A password credential consists of
		 * a single string value. The type of the credential is set to {@link ValueType#PASSWORD}.
		 *
		 * @param value the password credential value; must not be {@literal null}
		 * @return the builder
		 */
		public WriteRequestBuilder passwordValue(String value) {
			Assert.notNull(value, "value must not be null");
			this.valueType = ValueType.PASSWORD;
			this.value = value;
			return this;
		}

		/**
		 * Set the value of a JSON credential. A JSON credential consists of
		 * one or more fields in a JSON document. The provided {@literal Map} parameter.
		 * will be converted to a JSON document before sending to CredHub. The type of
		 * the credential is set to {@link ValueType#JSON}.
		 *
		 * @param value the json credential value; must not be {@literal null}
		 * @return the builder
		 */
		public WriteRequestBuilder jsonValue(Map<String, Object> value) {
			Assert.notNull(value, "value must not be null");
			this.valueType = JSON;
			this.value = value;
			return this;
		}

		/**
		 * Set the {@link CredentialName} for the credential.
		 *
		 * @param name the credential name; must not be {@literal null}
		 * @return the builder
		 */
		public WriteRequestBuilder name(CredentialName name) {
			Assert.notNull(name, "name must not be null");
			this.name = name;
			return this;
		}

		/**
		 * Sets a {@literal boolean} value indicating whether CredHub should create a new
		 * credential or update and existing credential.
		 *
		 * @param overwrite {@literal false} to create a new credential, or
		 * {@literal true} to update and existing credential
		 * @return the builder 
		 */
		public WriteRequestBuilder overwrite(boolean overwrite) {
			this.overwrite = overwrite;
			return this;
		}

		/**
		 * Add an {@link AccessControlEntry} to the controls that will be assigned to the
		 * credential.
		 *
		 * @param accessControlEntry an {@link AccessControlEntry} to assign to the
		 * credential
		 * @return the builder
		 */
		public WriteRequestBuilder accessControlEntry(AccessControlEntry accessControlEntry) {
			initAccessControls();
			this.accessControlEntries.add(accessControlEntry);
			return this;
		}

		/**
		 * Add a collection of {@link AccessControlEntry}s to the controls that will be
		 * assigned to the credential.
		 *
		 * @param accessControlEntries an collection of {@link AccessControlEntry}s to
		 * assign to the credential
		 * @return the builder
		 */
		public WriteRequestBuilder accessControlEntries(Collection<? extends AccessControlEntry> accessControlEntries) {
			initAccessControls();
			this.accessControlEntries.addAll(accessControlEntries);
			return this;
		}

		private void initAccessControls() {
			if (this.accessControlEntries == null) {
				this.accessControlEntries = new ArrayList<AccessControlEntry>();
			}
		}

		/**
		 * Create a {@link WriteRequest} from the provided values.
		 *
		 * @return a {@link WriteRequest}
		 */
		public WriteRequest build() {
			List<AccessControlEntry> accessControlEntries;
			switch (this.accessControlEntries == null ? 0
					: this.accessControlEntries.size()) {
			case 0:
				accessControlEntries = java.util.Collections.emptyList();
				break;
			case 1:
				accessControlEntries = java.util.Collections
						.singletonList(this.accessControlEntries.get(0));
				break;
			default:
				accessControlEntries = java.util.Collections.unmodifiableList(
						new ArrayList<AccessControlEntry>(this.accessControlEntries));
			}

			return new WriteRequest(name, overwrite, value, valueType,
					accessControlEntries);
		}
	}

}
