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

import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialType;

/**
 * The details of a certificate credential that has been written to CredHub. This is a specialization
 * of {@link CredentialDetails} that adds certificate-specific fields.
 *
 * Clients don't typically instantiate objects of this type, but will receive them in response
 * to credential operation requests. The {@literal id} and {@literal name} fields
 * can be used in subsequent requests.
 *
 * @author Scott Frederick
 */
public class CertificateCredentialDetails extends CredentialDetails<CertificateCredential> {
	private final boolean transitional;

	/**
	 * Create a {@link CertificateCredentialDetails}.
	 */
	@SuppressWarnings("unused")
	private CertificateCredentialDetails() {
		super();
		this.transitional = false;
	}

	/**
	 * Create a {@link CertificateCredentialDetails} from the provided parameters. Intended for
	 * internal use. Clients will get {@link CertificateCredentialDetails} objects populated from
	 * CredHub responses.
	 *
	 * @param id the CredHub-generated unique ID of the credential
	 * @param name the client-provided name of the credential
	 * @param credentialType the {@link CredentialType} of the credential
	 * @param transitional a flag indicating whether the certificate will be used for signing
	 * @param value the client-provided value for the credential
	 */
	public CertificateCredentialDetails(String id, CredentialName name, CredentialType credentialType,
										boolean transitional, CertificateCredential value) {
		super(id, name, credentialType, value);
		this.transitional = transitional;
	}

	/**
	 * Get the value of the flag indicating whether the certificate is currently being used for signing
	 * or if it is being staged.
	 *
	 * @return the transitional flag
	 */
	public boolean isTransitional() {
		return transitional;
	}
}
