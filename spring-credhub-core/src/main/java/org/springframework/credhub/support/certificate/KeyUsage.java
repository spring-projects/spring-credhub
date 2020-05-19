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
 * The types of key usage extensions that can be assigned to a generated certificate.
 *
 * @author Scott Frederick
 */
public enum KeyUsage {

	/**
	 * Digital signature key.
	 */
	DIGITAL_SIGNATURE("digital_signature"),

	/**
	 * Non-repudiation key.
	 */
	NON_REPUDIATION("non_repudiation"),

	/**
	 * Key encipherment key.
	 */
	KEY_ENCIPHERMENT("key_encipherment"),

	/**
	 * Data encipherment key.
	 */
	DATA_ENCIPHERMENT("data_encipherment"),

	/**
	 * Key agreement key.
	 */
	KEY_AGREEMENT("key_agreement"),

	/**
	 * Key certificate signing key.
	 */
	KEY_CERT_SIGN("key_cert_sign"),

	/**
	 * CRL signing key.
	 */
	CRL_SIGN("crl_sign"),

	/**
	 * Encipher only key.
	 */
	ENCIPHER_ONLY("encipher_only"),

	/**
	 * Decipher only key.
	 */
	DECIPHER_ONLY("decipher_only");

	private final String value;

	KeyUsage(String value) {
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
