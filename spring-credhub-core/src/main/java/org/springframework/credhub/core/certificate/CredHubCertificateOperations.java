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

package org.springframework.credhub.core.certificate;

import org.springframework.credhub.support.CertificateSummary;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;

import java.util.List;

/**
 * Specifies the interactions with CredHub to retrieve, regenerate, and update
 * certificates.
 *
 * @author Scott Frederick
 */
public interface CredHubCertificateOperations {
	/**
	 * Retrieve all certificates from CredHub.
	 *
	 * @return a collection of certificates
	 */
	List<CertificateSummary> getAll();

	/**
	 * Retrieve a certificate using its name.
	 *
	 * @param name the name of the certificate credential; must not be {@literal null}
	 * @return the details of the retrieved certificate credential
	 */
	CertificateSummary getByName(final CredentialName name);

	/**
	 * Regenerate a certificate.
	 *
	 * @param id the CredHub-generated ID of the certificate credential; must not be {@literal null}
	 * @param setAsTransitional make the certificate version transitional or not
	 * @return the details of the certificate credential
	 */
	CredentialDetails<CertificateCredential> regenerate(final String id, final boolean setAsTransitional);
}
