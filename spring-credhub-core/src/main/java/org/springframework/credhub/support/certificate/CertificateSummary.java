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

package org.springframework.credhub.support.certificate;

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * A summary of a certificate that has been stored to CredHub. Clients don't typically
 * instantiate objects of this type, but will receive them in response to write and
 * retrieve requests.
 *
 * @author Scott Frederick
 */
public class CertificateSummary {

	private final @Nullable String id;

	private final @Nullable String name;

	private final @Nullable List<CertificateVersionSummary> versions;

	private final @Nullable String signedBy;

	private final @Nullable List<String> signs;

	@SuppressWarnings("unused")
	private CertificateSummary() {
		this.id = null;
		this.name = null;
		this.versions = null;
		this.signedBy = null;
		this.signs = null;
	}

	/**
	 * Create a {@link CertificateSummary} from the provided parameters. Intended for
	 * internal use. Clients will get {@link CertificateSummary} objects populated from
	 * CredHub responses.
	 * @param id the ID of the certificate credential
	 * @param name the name of the certificate credential
	 */
	public CertificateSummary(String id, String name) {
		this.id = id;
		this.name = name;
		this.versions = null;
		this.signedBy = null;
		this.signs = null;
	}

	/**
	 * Get the CredHub-generated ID of the certificate credential.
	 * @return the credential ID
	 */
	public @Nullable String getId() {
		return this.id;
	}

	/**
	 * Get the client-provided name of the certificate credential.
	 * @return the credential name
	 */
	public @Nullable String getName() {
		return this.name;
	}

	/**
	 * Get the summaries of all versions of the certificate credential.
	 * @return the version summaries, or {@literal null} if not reported by the server
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_all_certificates">CredHub
	 * API docs: Get All Certificates</a>
	 */
	public @Nullable List<CertificateVersionSummary> getVersions() {
		return this.versions;
	}

	/**
	 * Get the name of the certificate authority that signed this certificate, if any.
	 * @return the signing certificate authority's name, or {@literal null} if not
	 * reported by the server
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_all_certificates">CredHub
	 * API docs: Get All Certificates</a>
	 */
	public @Nullable String getSignedBy() {
		return this.signedBy;
	}

	/**
	 * Get the names of the certificates signed by this certificate authority.
	 * @return the names of the signed certificates, or {@literal null} if not reported by
	 * the server
	 * @see <a href=
	 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_all_certificates">CredHub
	 * API docs: Get All Certificates</a>
	 */
	public @Nullable List<String> getSigns() {
		return this.signs;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CertificateSummary)) {
			return false;
		}
		CertificateSummary that = (CertificateSummary) o;
		return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name)
				&& Objects.equals(this.versions, that.versions) && Objects.equals(this.signedBy, that.signedBy)
				&& Objects.equals(this.signs, that.signs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.versions, this.signedBy, this.signs);
	}

	@Override
	public String toString() {
		return "CertificateSummary{" + "id='" + this.id + '\'' + ", name='" + this.name + '\'' + ", versions="
				+ this.versions + ", signedBy='" + this.signedBy + '\'' + ", signs=" + this.signs + '}';
	}

}
