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

package org.springframework.credhub.support;

/**
 * The details of a request to generate a credential in CredHub.
 *
 * @author Scott Frederick
 */
public class ParametersRequest<T> extends CredHubRequest<T> {
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
		return this.details;
	}

	protected void setParameters(T parameters) {
		this.details = parameters;
	}
}
