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

package org.springframework.credhub.core.certificate;

import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.certificate.CertificateCredentialDetails;
import org.springframework.credhub.support.certificate.CertificateSummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Specifies the interactions with CredHub to retrieve, regenerate, and update
 * certificates.
 *
 * @author Scott Frederick
 */
public interface ReactiveCredHubCertificateOperations {
	/**
	 * Retrieve all certificates from CredHub.
	 *
	 * @return a collection of certificates
	 */
	Flux<CertificateSummary> getAll();

	/**
	 * Retrieve a certificate using its name.
	 *
	 * @param name the name of the certificate credential; must not be {@literal null}
	 * @return the details of the retrieved certificate credential
	 */
	Mono<CertificateSummary> getByName(final CredentialName name);

	/**
	 * Regenerate a certificate.
	 *
	 * @param id                the CredHub-generated ID of the certificate credential; must not be {@literal null}
	 *                          and must be an ID returned by {@link #getAll()}
	 *                          or {@link #getByName(CredentialName)}
	 * @param setAsTransitional {@code true} to mark the certificate version transitional;
	 *                          {@code false} otherwise
	 * @return the details of the certificate credential
	 */
	Mono<CertificateCredentialDetails> regenerate(final String id, final boolean setAsTransitional);

	/**
	 * Regenerate all certificates in CredHub that were signed by the specified certificate.
	 *
	 * @param certificateName the name of the signing certificate credential; must not be {@literal null}
	 * @return the names of all regenerated certificate credentials
	 */
	Flux<CredentialName> regenerate(CredentialName certificateName);

	/**
	 * Make the specified version of a certificate the {@literal transitional} version.
	 *
	 * @param id        the CredHub-generated ID of the certificate credential; must not be {@literal null}
	 *                  and must be an ID returned by {@link #getAll()}
	 *                  or {@link #getByName(CredentialName)}
	 * @param versionId the CredHub-generated ID of the version of the certificate credential that should be
	 *                  marked {@literal transitional}, or {@literal null} to indicate that no version
	 *                  is {@literal transitional}
	 * @return the details of the certificate credential, including all versions
	 */
	Flux<CertificateCredentialDetails> updateTransitionalVersion(final String id, final String versionId);
}
