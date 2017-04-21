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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Map;

@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CredentialData {
	private String id;
	private CredentialName name;
	@JsonProperty("type")
	private ValueType valueType;
	private Object value;
	private String versionCreatedAt;

	public CredentialData() {
	}

	CredentialData(String id, CredentialName name, ValueType valueType, Object value, String versionCreatedAt) {
		this.id = id;
		this.name = name;
		this.valueType = valueType;
		this.value = value;
		this.versionCreatedAt = versionCreatedAt;
	}

	public static CredentialDataBuilder builder() {
		return new CredentialDataBuilder();
	}

	public String getId() {
		return this.id;
	}

	public CredentialName getName() {
		return this.name;
	}

	public ValueType getValueType() {
		return this.valueType;
	}

	public Object getValue() {
		return this.value;
	}

	public String getVersionCreatedAt() {
		return this.versionCreatedAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CredentialData)) return false;

		CredentialData that = (CredentialData) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (valueType != that.valueType) return false;
		if (value != null ? !value.equals(that.value) : that.value != null) return false;
		return versionCreatedAt != null ? versionCreatedAt.equals(that.versionCreatedAt) : that.versionCreatedAt == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
		result = 31 * result + (value != null ? value.hashCode() : 0);
		result = 31 * result + (versionCreatedAt != null ? versionCreatedAt.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "CredentialData{" +
				"id='" + id + '\'' +
				", name=" + name +
				", valueType=" + valueType +
				", value=" + value +
				", versionCreatedAt='" + versionCreatedAt + '\'' +
				'}';
	}

	public static class CredentialDataBuilder {
		private String id;
		private CredentialName name;
		private ValueType valueType;
		private Object value;
		private String versionCreatedAt;

		CredentialDataBuilder() {
		}

		public CredentialData.CredentialDataBuilder id(String id) {
			this.id = id;
			return this;
		}

		public CredentialData.CredentialDataBuilder name(CredentialName name) {
			this.name = name;
			return this;
		}

		public CredentialData.CredentialDataBuilder passwordValue(String value) {
			this.valueType = ValueType.PASSWORD;
			this.value = value;
			return this;
		}

		public CredentialData.CredentialDataBuilder jsonValue(Map<String, Object> value) {
			this.valueType = ValueType.JSON;
			this.value = value;
			return this;
		}

		public CredentialData.CredentialDataBuilder versionCreatedAt(String versionCreatedAt) {
			this.versionCreatedAt = versionCreatedAt;
			return this;
		}

		public CredentialData build() {
			return new CredentialData(id, name, valueType, value, versionCreatedAt);
		}

		@Override
		public String toString() {
			return "CredentialDataBuilder{" +
					"id='" + id + '\'' +
					", name=" + name +
					", valueType=" + valueType +
					", value=" + value +
					", versionCreatedAt='" + versionCreatedAt + '\'' +
					'}';
		}
	}
}
