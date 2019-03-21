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

import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.password.PasswordCredential;
import org.springframework.credhub.support.rsa.RsaCredential;
import org.springframework.credhub.support.ssh.SshCredential;
import org.springframework.credhub.support.user.UserCredential;
import org.springframework.credhub.support.value.ValueCredential;

/**
 * The types of credentials that can be written to CredHub.
 */
public enum CredentialType {
	/**
	 * Indicates a credential of type {@link PasswordCredential}.
	 */
	PASSWORD("password", PasswordCredential.class),

	/**
	 * Indicates a credential of type {@link ValueCredential}.
	 */
	VALUE("value", ValueCredential.class),

	/**
	 * Indicates a credential of type {@link UserCredential}.
	 */
	USER("user", UserCredential.class),

	/**
	 * Indicates a credential of type {@link RsaCredential}.
	 */
	RSA("rsa", RsaCredential.class),

	/**
	 * Indicates a credential of type {@link SshCredential}.
	 */
	SSH("ssh", SshCredential.class),

	/**
	 * Indicates a credential of type {@link CertificateCredential}.
	 */
	CERTIFICATE("certificate", CertificateCredential.class),

	/**
	 * Indicates a credential of type {@link JsonCredential}.
	 */
	JSON("json", JsonCredential.class);

	private final String valueType;
	private final Class<?> modelClass;

	CredentialType(String valueType, Class<?> modelClass) {
		this.valueType = valueType;
		this.modelClass = modelClass;
	}

	/**
	 * Get the type value that will be used in requests to CredHub.
	 *
	 * @return the type value
	 */
	public String getValueType() {
		return valueType;
	}

	/**
	 * Get the class that models requests of the credential type.
	 *
	 * @return the credential model class
	 */
	public Class<?> getModelClass() {
		return modelClass;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return valueType;
	}
}
