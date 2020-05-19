/*
 * Copyright 2016-2020 the original author or authors.
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
 */

package org.springframework.credhub.support.certificate;

/**
 * The types of extended key usage extensions that can be assigned to a generated
 * certificate.
 *
 * @author Scott Frederick
 */
public enum ExtendedKeyUsage {

	/**
	 * Client authentication.
	 */
	CLIENT_AUTH("client_auth"),

	/**
	 * Server authentication.
	 */
	SERVER_AUTH("server_auth"),

	/**
	 * Code signing.
	 */
	CODE_SIGNING("code_signing"),

	/**
	 * Email protection.
	 */
	EMAIL_PROTECTION("email_protection"),

	/**
	 * Time stamping.
	 */
	TIMESTAMPING("timestamping");

	private final String value;

	ExtendedKeyUsage(String value) {
		this.value = value;
	}

	/**
	 * Get the value as a {@code String}.
	 * @return the mode value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.value;
	}

}
