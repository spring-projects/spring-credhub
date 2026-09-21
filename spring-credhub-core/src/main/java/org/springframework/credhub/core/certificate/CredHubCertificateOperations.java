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

package org.springframework.credhub.core.certificate;

import java.util.List;
import java.util.Map;

import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateCredentialDetails;
import org.springframework.credhub.support.certificate.CertificateSummary;

/**
 * Specifies the interactions with CredHub to retrieve, regenerate, and update
 * certificates.
 *
 * @author Scott Frederick
 */
public interface CredHubCertificateOperations {

	/**
	 * Retrieve all certificates from CredHub.
	 * @return a collection of certificates
	 */
	List<CertificateSummary> getAll();

	/**
	 * Retrieve a certificate using its name.
	 * @param name the name of the certificate credential; must not be {@literal null}
	 * @return the details of the retrieved certificate credential
	 */
	CertificateSummary getByName(CredentialName name);

	/**
	 * Regenerate a certificate.
	 * @param id the CredHub-generated ID of the certificate credential; must not be
	 * {@literal null} and must be an ID returned by {@link #getAll()} or
	 * {@link #getByName(CredentialName)}
	 * @param setAsTransitional {@code true} to mark the certificate version transitional;
	 * {@code false} otherwise
	 * @return the details of the certificate credential
	 */
	CertificateCredentialDetails regenerate(String id, boolean setAsTransitional);

	/**
	 * Regenerate a certificate, with full control over the regeneration parameters
	 * CredHub supports. Metadata is only supported by CredHub server 2.6.0 and later;
	 * earlier server versions reject this field as an unrecognized request parameter.
	 * @param id the CredHub-generated ID of the certificate credential; must not be
	 * {@literal null} and must be an ID returned by {@link #getAll()} or
	 * {@link #getByName(CredentialName)}
	 * @param setAsTransitional {@code true} to mark the certificate version transitional;
	 * {@code false} otherwise
	 * @param allowTransitionalParentToSign {@code true} to allow a transitional version
	 * of the parent CA to sign this certificate if the transitional version is the latest
	 * version; {@code false} otherwise
	 * @param keyLength the length of the new key, or {@literal null} to use the same
	 * length as the previous version
	 * @param duration the validity duration in days of the new version, or
	 * {@literal null} to use the same duration as the previous version
	 * @param metadata additional metadata to store with the certificate, or
	 * {@literal null} to store no metadata
	 * @return the details of the certificate credential
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_regenerate_a_certificate">CredHub
	 * API docs: Regenerate a Certificate</a>
	 */
	CertificateCredentialDetails regenerate(String id, boolean setAsTransitional, boolean allowTransitionalParentToSign,
			Integer keyLength, Integer duration, Map<String, Object> metadata);

	/**
	 * Regenerate all certificates in CredHub that were signed by the specified
	 * certificate.
	 * @param certificateName the name of the signing certificate credential; must not be
	 * {@literal null}
	 * @return the names of all regenerated certificate credentials
	 */
	List<CredentialName> regenerate(CredentialName certificateName);

	/**
	 * Retrieve all versions of a certificate.
	 * @param id the CredHub-generated ID of the certificate credential; must not be
	 * {@literal null} and must be an ID returned by {@link #getAll()} or
	 * {@link #getByName(CredentialName)}
	 * @return the details of all versions of the certificate credential
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_all_versions_of_a_certificate">CredHub
	 * API docs: Get All Versions of a Certificate</a>
	 */
	List<CertificateCredentialDetails> getVersions(String id);

	/**
	 * Add a new version of a certificate, importing an existing certificate value rather
	 * than generating one.
	 * @param id the CredHub-generated ID of the certificate credential; must not be
	 * {@literal null} and must be an ID returned by {@link #getAll()} or
	 * {@link #getByName(CredentialName)}
	 * @param value the certificate value to import as a new version; must not be
	 * {@literal null}. CredHub validates the value's consistency: the certificate must be
	 * signed by the given certificate authority, match the given private key, and the
	 * certificate authority itself must be a valid X.509 certificate authority (i.e. have
	 * the CA basic constraint set) — a self-signed certificate that isn't itself a CA
	 * will be rejected
	 * @param transitional {@code true} to mark the new version transitional;
	 * {@code false} otherwise
	 * @return the details of the newly added certificate version
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_create_a_version_of_a_certificate">CredHub
	 * API docs: Create a Version of a Certificate</a>
	 */
	CertificateCredentialDetails addVersion(String id, CertificateCredential value, boolean transitional);

	/**
	 * Make the specified version of a certificate the {@literal transitional} version.
	 * @param id the CredHub-generated ID of the certificate credential; must not be
	 * {@literal null} and must be an ID returned by {@link #getAll()} or
	 * {@link #getByName(CredentialName)}
	 * @param versionId the CredHub-generated ID of the version of the certificate
	 * credential that should be marked {@literal transitional}, or {@literal null} to
	 * indicate that no version is {@literal transitional}
	 * @return the details of the certificate credential, including all versions
	 */
	List<CertificateCredentialDetails> updateTransitionalVersion(String id, String versionId);

	/**
	 * Delete a specific version of a certificate.
	 * @param id the CredHub-generated ID of the certificate credential; must not be
	 * {@literal null} and must be an ID returned by {@link #getAll()} or
	 * {@link #getByName(CredentialName)}
	 * @param versionId the CredHub-generated ID of the version of the certificate
	 * credential to delete; must not be {@literal null}
	 * @return the details of the deleted certificate version
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_delete_a_version_of_a_certificate">CredHub
	 * API docs: Delete a Version of a Certificate</a>
	 */
	CertificateCredentialDetails deleteVersion(String id, String versionId);

}
