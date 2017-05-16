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

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.springframework.util.Assert;

/**
 * The details of a request to write a new or update an existing password credential in CredHub.
 *
 * @author Scott Frederick
 */
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PasswordWriteRequest extends WriteRequest<String> {
	/**
	 * Create a {@link PasswordWriteRequest} from the provided parameters. Intended for internal
	 * use. Clients should use {@link #builder()} to construct instances of this class.
	 *
	 * @param name the name of the credential
	 * @param overwrite {@literal false} to create a new credential, or
	 * {@literal true} to update and existing credential
	 * @param value the value of the credential
	 * @param additionalPermissions access control permissions for the credential
	 */
	private PasswordWriteRequest(CredentialName name, boolean overwrite, String value,
								 List<AdditionalPermission> additionalPermissions) {
		super(name, overwrite, value, ValueType.PASSWORD, additionalPermissions);
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link PasswordWriteRequest}.
	 *
	 * @return a builder
	 */
	public static PasswordWriteRequestBuilder builder() {
		return new PasswordWriteRequestBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link PasswordWriteRequest}s.
	 */
	public static class PasswordWriteRequestBuilder extends WriteRequestBuilder<String, PasswordWriteRequestBuilder> {
		/**
		 * Create a {@link PasswordWriteRequestBuilder}. Intended for internal use.
		 */
		PasswordWriteRequestBuilder() {
		}

		@Override
		protected PasswordWriteRequestBuilder getBuilder() {
			return this;
		}

		/**
		 * Set the value of a password credential. A password credential consists of
		 * a single string value. The type of the credential is set to {@link ValueType#PASSWORD}.
		 *
		 * @param value the password credential value; must not be {@literal null}
		 * @return the builder
		 */
		public PasswordWriteRequestBuilder value(String value) {
			Assert.notNull(value, "value must not be null");
			this.valueType = ValueType.PASSWORD;
			this.value = value;
			return this;
		}
	}

}
