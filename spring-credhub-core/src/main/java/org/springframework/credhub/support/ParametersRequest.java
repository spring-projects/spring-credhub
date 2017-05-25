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

/**
 * The details of a request to generate a credential in CredHub.
 *
 * @author Scott Frederick
 */
public class ParametersRequest<T> extends CredHubRequest {
	private T parameters;

	/**
	 * Initialize a {@link ParametersRequest}.
	 *
	 * @param type the type of credential this request supports
	 */
	protected ParametersRequest(CredentialType type) {
		credentialType = type;
	}

	/**
	 * Get the parameters of the credential.
	 *
	 * @return the parameters of the credential
	 */
	public T getParameters() {
		return this.parameters;
	}

	protected void setParameters(T parameters) {
		this.parameters = parameters;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ParametersRequest))
			return false;

		ParametersRequest that = (ParametersRequest) o;

		if (overwrite != that.overwrite)
			return false;
		if (!name.equals(that.name))
			return false;
		if (credentialType != that.credentialType)
			return false;
		if (!parameters.equals(that.parameters))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = (overwrite ? 1 : 0);
		result = 31 * result + name.hashCode();
		result = 31 * result + credentialType.hashCode();
		result = 31 * result + parameters.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "CredentialParameter{"
				+ "overwrite=" + overwrite
				+ ", name=" + name
				+ ", credentialType=" + credentialType
				+ ", parameters=" + parameters
				+ '}';
	}

	/**
	 * A builder that provides a fluent API for constructing {@link ParametersRequest}s.
	 */
	@SuppressWarnings("unchecked")
	protected static abstract class GenerateCredentialRequestBuilder<T, R extends ParametersRequest<T>, B extends GenerateCredentialRequestBuilder<T, R, B>> {
		private final B thisObj;
		protected final R targetObj;

		/**
		 * Create a {@link GenerateCredentialRequestBuilder}. Intended for internal use.
		 */
		protected GenerateCredentialRequestBuilder() {
			this.thisObj = createBuilder();
			this.targetObj = createTarget();
		}

		/**
		 * Provide the concrete object to build.
		 *
		 * @return the target object
		 */
		protected abstract R createTarget();

		/**
		 * Provide the concrete builder.
		 *
		 * @return the builder
		 */
		protected abstract B createBuilder();

		/**
		 * Set the parameters of a credential.
		 *
		 * @param parameters the credential parameters; must not be {@literal null}
		 * @return the builder
		 */
		public abstract B parameters(T parameters);

		/**
		 * Set the {@link CredentialName} for the credential.
		 *
		 * @param name the credential name; must not be {@literal null}
		 * @return the builder
		 */
		public B name(CredentialName name) {
			Assert.notNull(name, "name must not be null");
			targetObj.setName(name);
			return thisObj;
		}

		/**
		 * Sets a {@literal boolean} parameters indicating whether CredHub should create a new
		 * credential or update and existing credential.
		 *
		 * @param overwrite {@literal false} to create a new credential, or
		 * {@literal true} to update and existing credential
		 * @return the builder 
		 */
		public B overwrite(boolean overwrite) {
			targetObj.setOverwrite(overwrite);
			return thisObj;
		}

		/**
		 * Create a {@link ParametersRequest} from the provided values.
		 *
		 * @return a {@link ParametersRequest}
		 */
		public R build() {
			return targetObj;
		}
	}
}
