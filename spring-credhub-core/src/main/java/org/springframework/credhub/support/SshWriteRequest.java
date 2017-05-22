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

import org.springframework.util.Assert;

import static org.springframework.credhub.support.ValueType.SSH;

/**
 * The details of a request to write a new or update an existing {@link SshCredential} in CredHub.
 *
 * @author Scott Frederick
 */
public class SshWriteRequest extends WriteRequest<SshCredential> {
	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link SshWriteRequest}.
	 *
	 * @return a builder
	 */
	public static SshWriteRequestBuilder builder() {
		return new SshWriteRequestBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link SshWriteRequest}s.
	 */
	public static class SshWriteRequestBuilder
			extends WriteRequestBuilder<SshCredential, SshWriteRequest, SshWriteRequestBuilder> {
		@Override
		protected SshWriteRequest createTarget() {
			return new SshWriteRequest();
		}

		@Override
		protected SshWriteRequestBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the value of an SSH credential.
		 *
		 * @param value the credential value; must not be {@literal null}
		 * @return the builder
		 */
		public SshWriteRequestBuilder value(SshCredential value) {
			Assert.notNull(value, "value must not be null");
			targetObj.setType(SSH);
			targetObj.setValue(value);
			return this;
		}
	}
}
