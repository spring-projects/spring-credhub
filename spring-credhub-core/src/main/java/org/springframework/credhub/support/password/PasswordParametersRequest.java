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

package org.springframework.credhub.support.password;

import org.springframework.credhub.support.ParametersRequest;
import org.springframework.util.Assert;

import static org.springframework.credhub.support.CredentialType.PASSWORD;

/**
 * The details of a request to generate a new {@link PasswordCredential} in CredHub.
 *
 * @author Scott Frederick
 */
public class PasswordParametersRequest extends ParametersRequest<PasswordParameters> {
	/**
	 * Create a {@link PasswordParametersRequest}.
	 */
	PasswordParametersRequest() {
		super(PASSWORD);
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link PasswordParametersRequest}.
	 *
	 * @return a builder
	 */
	public static PasswordGenerateRequestBuilder builder() {
		return new PasswordGenerateRequestBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link PasswordParametersRequest}s.
	 */
	public static class PasswordGenerateRequestBuilder
			extends GenerateCredentialRequestBuilder<PasswordParameters, PasswordParametersRequest, PasswordGenerateRequestBuilder> {
		@Override
		protected PasswordParametersRequest createTarget() {
			return new PasswordParametersRequest();
		}

		@Override
		protected PasswordGenerateRequestBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the parameters for generation of a password credential.
		 *
		 * @param parameters the generation parameters; must not be {@literal null}
		 * @return the builder
		 */
		public PasswordGenerateRequestBuilder parameters(PasswordParameters parameters) {
			Assert.notNull(parameters, "parameters must not be null");
			targetObj.setParameters(parameters);
			return this;
		}
	}

}
