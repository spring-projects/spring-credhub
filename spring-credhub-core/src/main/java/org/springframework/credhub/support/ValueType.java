/*
 *
 *  * Copyright 2013-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.credhub.support;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The types of credentials that can be written to CredHub.
 */
public enum ValueType {
	/**
	 * Indicates a credential of type {@link PasswordCredential}.
	 */
	PASSWORD("password"),

	/**
	 * Indicates a credential of type {@link ValueCredential}.
	 */
	VALUE("value"),

	/**
	 * Indicates a credential of type {@link UserCredential}.
	 */
	USER("user"),

	/**
	 * Indicates a credential of type {@link RsaCredential}.
	 */
	RSA("rsa"),

	/**
	 * Indicates a credential of type {@link SshCredential}.
	 */
	SSH("ssh"),

	/**
	 * Indicates a credential of type {@link CertificateCredential}.
	 */
	CERTIFICATE("certificate"),

	/**
	 * Indicates a credential of type {@link JsonCredential}.
	 */
	JSON("json");

	private final String type;

	ValueType(String type) {
		this.type = type;
	}

	/**
	 * Get the type value that will be used in requests to CredHub.
	 *
	 * @return the type value
	 */
	public String type() {
		return type;
	}

	/**
	 * Convert a {@literal String} type to its enum value.
	 *
	 * @param type the {@literal String} type to convert
	 * @return the enum value
	 */
	@JsonCreator
	public static ValueType getTypeByString(String type) {
		for (ValueType e : ValueType.values()) {
			if (e.type().equals(type)) {
				return e;
			}
		}
		return null;
	}
}
