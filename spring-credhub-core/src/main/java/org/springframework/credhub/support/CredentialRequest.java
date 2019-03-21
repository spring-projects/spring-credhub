/*
 *
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.credhub.support;

/**
 * The details of a request to write a new or update an existing credential in CredHub.
 *
 * @author Scott Frederick
 */
public class CredentialRequest<T> extends CredHubRequest<T> {
	/**
	 * Initialize a {@link CredentialRequest}.
	 *
	 * @param type the credential implementation type
	 */
	protected CredentialRequest(CredentialType type) {
		super();
		this.credentialType = type;
	}

	/**
	 * Get the value of the credential.
	 *
	 * @return the value of the credential
	 */
	public T getValue() {
		return this.details;
	}

	protected void setValue(T value) {
		this.details = value;
	}
}
