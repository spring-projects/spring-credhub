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

package org.springframework.credhub.support.ssh;

import org.springframework.credhub.support.KeyPairCredential;

/**
 * An SSH credential consists of a public and/or private key. At least one of these key values must be provided.
 *
 * @author Scott Frederick
 */
public class SshCredential extends KeyPairCredential {
	private final String publicKeyFingerprint;

	/**
	 * Create an empty {@link SshCredential}. Intended to be used internally for deserialization of responses.
	 */
	private SshCredential() {
		super();
		publicKeyFingerprint = null;
	}

	/**
	 * Create an {@link SshCredential} from the provided public and private key. At least one of the key
	 * values must not be {@literal null}.
	 *
	 * @param publicKey the public key; may be {@literal null} only if {@literal privateKey} is not null
	 * @param privateKey the private key; may be {@literal null} only if {@literal publicKey} is not null
	 */
	public SshCredential(String publicKey, String privateKey) {
		super(publicKey, privateKey);
		publicKeyFingerprint = null;
	}

	/**
	 * Get the fingerprint of the public key associated with the credential. This value can not be provided
	 * when creating an {@code SshCredential}, but may be provided by CredHub when retrieving one.
	 * 
	 * @return the public key fingerprint value
	 */
	public String getPublicKeyFingerprint() {
		return publicKeyFingerprint;
	}
}
