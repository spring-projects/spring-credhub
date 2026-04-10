/*
 * Copyright 2016-2026 the original author or authors.
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

package org.springframework.credhub.support;

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;

/**
 * Base class for credentials types that contain a public/private key pair.
 *
 * @author Scott Frederick
 */
public class KeyPairCredential {

	private final @Nullable String publicKey;

	private final @Nullable String privateKey;

	/**
	 * Create an empty {@link KeyPairCredential}. Intended to be used internally for
	 * deserialization of responses.
	 */
	protected KeyPairCredential() {
		this.publicKey = null;
		this.privateKey = null;
	}

	/**
	 * Create a {@link KeyPairCredential} from the provided parameters. Intended for
	 * internal use.
	 * @param publicKey the public key
	 * @param privateKey the private key
	 */
	protected KeyPairCredential(@Nullable String publicKey, @Nullable String privateKey) {
		Assert.isTrue(publicKey != null || privateKey != null, "one of publicKey or privateKey must not be null");

		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	/**
	 * Get the value of the public key.
	 * @return the public key
	 */
	public @Nullable String getPublicKey() {
		return this.publicKey;
	}

	/**
	 * Get the value of the private key.
	 * @return the private key
	 */
	public @Nullable String getPrivateKey() {
		return this.privateKey;
	}

}
