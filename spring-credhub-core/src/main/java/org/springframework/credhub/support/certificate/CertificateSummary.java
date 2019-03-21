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

import java.util.Objects;

/**
 * A summary of a certificate that has been stored to CredHub. Clients don't typically
 * instantiate objects of this type, but will receive them in response to write and
 * retrieve requests.
 *
 * @author Scott Frederick
 */
public class CertificateSummary {
	private final String id;

	private final String name;

	@SuppressWarnings("unused")
	private CertificateSummary() {
		id = null;
		name = null;
	}

	/**
	 * Create a {@link CertificateSummary} from the provided parameters. Intended for
	 * internal use. Clients will get {@link CertificateSummary} objects populated from
	 * CredHub responses.
	 *
	 * @param id the ID of the certificate credential
	 * @param name the name of the certificate credential
	 */
	public CertificateSummary(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Get the CredHub-generated ID of the certificate credential.
	 *
	 * @return the credential ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the client-provided name of the certificate credential.
	 *
	 * @return the credential name
	 */
	public String getName() {
		return name;
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
		return Objects.equals(id, that.id) &&
				Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString() {
		return "CertificateSummary{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
