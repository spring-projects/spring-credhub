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

import org.springframework.credhub.support.CredentialRequest;
import org.springframework.util.Assert;

import static org.springframework.credhub.support.CredentialType.CERTIFICATE;

/**
 * The details of a request to write a new or update an existing {@link CertificateCredential} in CredHub.
 *
 * @author Scott Frederick
 */
public class CertificateCredentialRequest extends CredentialRequest<CertificateCredential> {
	/**
	 * Initialize a {@link CredentialRequest}.
	 */
	CertificateCredentialRequest() {
		super(CERTIFICATE);
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link CertificateCredentialRequest}.
	 *
	 * @return a builder
	 */
	public static CertificateCredentialRequestBuilder builder() {
		return new CertificateCredentialRequestBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link CertificateCredentialRequest}s.
	 */
	public static class CertificateCredentialRequestBuilder
			extends CredHubRequestBuilder<CertificateCredential, CertificateCredentialRequest, CertificateCredentialRequestBuilder> {
		@Override
		protected CertificateCredentialRequest createTarget() {
			return new CertificateCredentialRequest();
		}

		@Override
		protected CertificateCredentialRequestBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the value of a certificate credential.
		 *
		 * @param value the credential value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateCredentialRequestBuilder value(CertificateCredential value) {
			Assert.notNull(value, "value must not be null");
			targetObj.setValue(value);
			return this;
		}
	}
}
