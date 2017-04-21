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


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Singular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static org.springframework.credhub.support.ValueType.JSON;

@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WriteRequest extends CredHubRequest {
	private boolean overwrite;

	private ValueType valueType;

	private Object value;

	@JsonInclude(NON_EMPTY)
	private List<AccessControlEntry> accessControlEntries;

	private WriteRequest(CredentialName name, boolean overwrite, Object value, ValueType valueType,
						 @Singular List<AccessControlEntry> accessControlEntries) {
		super(name);
		this.overwrite = overwrite;
		this.valueType = valueType;
		this.value = value;
		this.accessControlEntries = accessControlEntries;
	}

	public static WriteRequestBuilder builder() {
		return new WriteRequestBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof WriteRequest)) return false;
		if (!super.equals(o)) return false;

		WriteRequest that = (WriteRequest) o;

		if (overwrite != that.overwrite) return false;
		if (valueType != that.valueType) return false;
		if (value != null ? !value.equals(that.value) : that.value != null) return false;
		return accessControlEntries != null ? accessControlEntries.equals(that.accessControlEntries) : that.accessControlEntries == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (overwrite ? 1 : 0);
		result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
		result = 31 * result + (value != null ? value.hashCode() : 0);
		result = 31 * result + (accessControlEntries != null ? accessControlEntries.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "WriteRequest{" +
				"overwrite=" + overwrite +
				", valueType=" + valueType +
				", value=" + value +
				", accessControlEntries=" + accessControlEntries +
				'}';
	}

	public boolean isOverwrite() {
		return this.overwrite;
	}

	public Object getValue() {
		return this.value;
	}

	public String getType() {
		return valueType.type();
	}

	public List<AccessControlEntry> getAccessControlEntries() {
		return this.accessControlEntries;
	}

	public static class WriteRequestBuilder {
		private CredentialName name;
		private boolean overwrite;
		private Object value;
		private ValueType valueType;
		private ArrayList<AccessControlEntry> accessControlEntries;

		WriteRequestBuilder() {
		}

		public WriteRequestBuilder passwordValue(String value) {
			this.valueType = ValueType.PASSWORD;
			this.value = value;
			return this;
		}

		public WriteRequestBuilder jsonValue(Map<String, Object> value) {
			this.valueType = JSON;
			this.value = value;
			return this;
		}

		public WriteRequestBuilder name(CredentialName name) {
			this.name = name;
			return this;
		}

		public WriteRequestBuilder overwrite(boolean overwrite) {
			this.overwrite = overwrite;
			return this;
		}

		public WriteRequestBuilder accessControlEntry(AccessControlEntry accessControlEntry) {
			if (this.accessControlEntries == null)
				this.accessControlEntries = new ArrayList<AccessControlEntry>();
			this.accessControlEntries.add(accessControlEntry);
			return this;
		}

		public WriteRequestBuilder accessControlEntries(Collection<? extends AccessControlEntry> accessControlEntries) {
			if (this.accessControlEntries == null)
				this.accessControlEntries = new ArrayList<AccessControlEntry>();
			this.accessControlEntries.addAll(accessControlEntries);
			return this;
		}

		public WriteRequest build() {
			List<AccessControlEntry> accessControlEntries;
			switch (this.accessControlEntries == null ? 0 : this.accessControlEntries.size()) {
				case 0:
					accessControlEntries = java.util.Collections.emptyList();
					break;
				case 1:
					accessControlEntries = java.util.Collections.singletonList(this.accessControlEntries.get(0));
					break;
				default:
					accessControlEntries = java.util.Collections.unmodifiableList(new ArrayList<AccessControlEntry>(this.accessControlEntries));
			}

			return new WriteRequest(name, overwrite, value, valueType, accessControlEntries);
		}

		@Override
		public String toString() {
			return "WriteRequestBuilder{" +
					"name=" + name +
					", overwrite=" + overwrite +
					", value=" + value +
					", valueType=" + valueType +
					", accessControlEntries=" + accessControlEntries +
					'}';
		}
	}

}
