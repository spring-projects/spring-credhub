/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.support.user;

import org.springframework.credhub.support.ParametersRequest;
import org.springframework.credhub.support.password.PasswordParameters;
import org.springframework.util.Assert;

import static org.springframework.credhub.support.CredentialType.USER;

/**
 * The details of a request to generate a new {@link UserCredential} in CredHub.
 *
 * @author Scott Frederick
 */
public class UserParametersRequest extends ParametersRequest<PasswordParameters> {
	private UserValue value;

	/**
	 * Create a {@link UserParametersRequest}.
	 */
	UserParametersRequest() {
		super(USER);
	}

	/**
	 * Set the value of the username parameter.
	 *
	 * @param username the username
	 */
	void setValue(String username) {
		this.value = new UserValue(username);
	}

	/**
	 * Get the value of the username parameter.
	 *
	 * @return the value of the parameter
	 */
	public UserValue getValue() {
		return value;
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link UserParametersRequest}.
	 *
	 * @return a builder
	 */
	public static UserParametersRequestBuilder builder() {
		return new UserParametersRequestBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link UserParametersRequest}s.
	 */
	public static class UserParametersRequestBuilder
			extends CredHubRequestBuilder<PasswordParameters, UserParametersRequest, UserParametersRequestBuilder> {
		@Override
		protected UserParametersRequest createTarget() {
			return new UserParametersRequest();
		}

		@Override
		protected UserParametersRequestBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the parameters for generation of the password for a user credential.
		 *
		 * @param parameters the password generation parameters; must not be {@literal null}
		 * @return the builder
		 */
		public UserParametersRequestBuilder parameters(PasswordParameters parameters) {
			Assert.notNull(parameters, "parameters must not be null");
			targetObj.setParameters(parameters);
			return this;
		}

		/**
		 * Set the username for the generated user.
		 *
		 * @param username the username; must not be {@literal null}
		 * @return the builder
		 */
		public UserParametersRequestBuilder username(String username) {
			Assert.notNull(username, "username must not be null");
			targetObj.setValue(username);
			return this;
		}
	}

	/**
	 * Holds the value of the username parameter.
	 */
	private static class UserValue {
		private String username;

		UserValue(String username) {
			this.username = username;
		}

		public String getUsername() {
			return username;
		}
	}
}
