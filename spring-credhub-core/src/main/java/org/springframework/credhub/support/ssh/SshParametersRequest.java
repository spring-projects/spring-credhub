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

package org.springframework.credhub.support.ssh;

import org.springframework.credhub.support.ParametersRequest;
import org.springframework.util.Assert;

import static org.springframework.credhub.support.CredentialType.SSH;

/**
 * The details of a request to generate a new {@link SshCredential} in CredHub.
 *
 * @author Scott Frederick
 */
public class SshParametersRequest extends ParametersRequest<SshParameters> {
	/**
	 * Create a {@link SshParametersRequest}.
	 */
	SshParametersRequest() {
		super(SSH);
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link SshParametersRequest}.
	 *
	 * @return a builder
	 */
	public static SshParametersRequestBuilder builder() {
		return new SshParametersRequestBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link SshParametersRequest}s.
	 */
	public static class SshParametersRequestBuilder
			extends CredHubRequestBuilder<SshParameters, SshParametersRequest, SshParametersRequestBuilder> {
		@Override
		protected SshParametersRequest createTarget() {
			return new SshParametersRequest();
		}

		@Override
		protected SshParametersRequestBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the parameters for generation of an SSH credential.
		 *
		 * @param parameters the generation parameters; must not be {@literal null}
		 * @return the builder
		 */
		public SshParametersRequestBuilder parameters(SshParameters parameters) {
			Assert.notNull(parameters, "parameters must not be null");
			targetObj.setParameters(parameters);
			return this;
		}
	}
}
