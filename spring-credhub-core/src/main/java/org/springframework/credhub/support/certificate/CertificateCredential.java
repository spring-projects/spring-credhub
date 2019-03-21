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

package org.springframework.credhub.support.certificate;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.util.Assert;

/**
 * A certificate credential consists of a certificate, a certificate authority, and a private key. At least
 * one of these three values must be provided.
 *
 * @author Scott Frederick 
 */
public class CertificateCredential {
	private final String certificate;
	@JsonProperty("ca")
	private final String certificateAuthority;
	private final String privateKey;

	/**
	 * Create an empty {@link CertificateCredential}. Intended to be used internally for deserialization of responses.
	 */
	private CertificateCredential() {
		certificate = null;
		certificateAuthority = null;
		privateKey = null;
	}

	/**
	 * Create an {@link CertificateCredential} from the provided public and private key. At least one of the key
	 * values must not be {@literal null}.
	 *
	 * @param certificate the certificate value; may be {@literal null} if one of the other parameters
	 *                       is not {@literal null}
	 * @param certificateAuthority the certificate authority value; may be {@literal null} if one of
	 *                                the other parameters is not {@literal null}
	 * @param privateKey the private key; may be {@literal null} if one of the other parameters is
	 *                      not {@literal null}
	 */
	public CertificateCredential(String certificate, String certificateAuthority, String privateKey) {
		Assert.isTrue(certificate != null || certificateAuthority != null || privateKey != null,
				"at least one of certificate, certificateAuthority, or privateKey must not be null");
		this.certificate = certificate;
		this.certificateAuthority = certificateAuthority;
		this.privateKey = privateKey;
	}


	/**
	 * Get the certificate value.
	 *
	 * @return the certificate
	 */
	public String getCertificate() {
		return certificate;
	}

	/**
	 * Get the certificate authority value.
	 *
	 * @return the certificate authority
	 */
	public String getCertificateAuthority() {
		return certificateAuthority;
	}

	/**
	 * Get the private key value.
	 *
	 * @return the private key
	 */
	public String getPrivateKey() {
		return privateKey;
	}
}
