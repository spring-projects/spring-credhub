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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Fields common to all types of CredHub requests.
 *
 * @author Scott Frederick
 */
public class CredHubRequest {
	protected boolean overwrite;
	protected CredentialName name;
	protected CredentialType credentialType;

	/**
	 * Get the value of the {@literal boolean} flag indicating whether the CredHub
	 * should create a new credential or update an existing credential.
	 *
	 * @return the {@literal boolean} overwrite value
	 */
	public boolean isOverwrite() {
		return this.overwrite;
	}

	void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * Get the {@link CredentialName} of the credential.
	 *
	 * @return the name of the credential
	 */
	@JsonInclude
	public String getName() {
		return name.getName();
	}

	void setName(CredentialName name) {
		this.name = name;
	}

	/**
	 * Get the {@link CredentialType} of the credential.
	 *
 	 * @return the type of the credential
	 */
	public String getType() {
		return credentialType.getValueType();
	}

	void setType(CredentialType credentialType) {
		this.credentialType = credentialType;
	}
}
