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

import org.springframework.util.Assert;

import static org.springframework.credhub.support.ValueType.CERTIFICATE;

/**
 * The details of a request to write a new or update an existing {@link CertificateCredential} in CredHub.
 *
 * @author Scott Frederick
 */
public class CertificateWriteRequest extends WriteRequest<CertificateCredential> {
	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link CertificateWriteRequest}.
	 *
	 * @return a builder
	 */
	public static CertificateWriteRequestBuilder builder() {
		return new CertificateWriteRequestBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link CertificateWriteRequest}s.
	 */
	public static class CertificateWriteRequestBuilder
			extends WriteRequestBuilder<CertificateCredential, CertificateWriteRequest, CertificateWriteRequestBuilder> {
		@Override
		protected CertificateWriteRequest createTarget() {
			return new CertificateWriteRequest();
		}

		@Override
		protected CertificateWriteRequestBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the value of an certificate credential.
		 *
		 * @param value the credential value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateWriteRequestBuilder value(CertificateCredential value) {
			Assert.notNull(value, "value must not be null");
			targetObj.setType(CERTIFICATE);
			targetObj.setValue(value);
			return this;
		}
	}
}
