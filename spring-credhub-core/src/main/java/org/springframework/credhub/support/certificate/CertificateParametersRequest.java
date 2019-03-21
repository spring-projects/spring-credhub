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

import org.springframework.credhub.support.ParametersRequest;
import org.springframework.util.Assert;

import static org.springframework.credhub.support.CredentialType.CERTIFICATE;

/**
 * The details of a request to generate a new {@link CertificateCredential} in CredHub.
 *
 * @author Scott Frederick
 */
public class CertificateParametersRequest extends ParametersRequest<CertificateParameters> {
	/**
	 * Create a {@link CertificateParametersRequest}.
	 */
	CertificateParametersRequest() {
		super(CERTIFICATE);
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link CertificateParametersRequest}.
	 *
	 * @return a builder
	 */
	public static CertificateParametersRequestBuilder builder() {
		return new CertificateParametersRequestBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link CertificateParametersRequest}s.
	 */
	public static class CertificateParametersRequestBuilder
			extends CredHubRequestBuilder<CertificateParameters, CertificateParametersRequest, CertificateParametersRequestBuilder> {
		@Override
		protected CertificateParametersRequest createTarget() {
			return new CertificateParametersRequest();
		}

		@Override
		protected CertificateParametersRequestBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the parameters for generation of a password credential.
		 *
		 * @param parameters the generation parameters; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersRequestBuilder parameters(CertificateParameters parameters) {
			Assert.notNull(parameters, "parameters must not be null");
			targetObj.setParameters(parameters);
			return this;
		}
	}
}
