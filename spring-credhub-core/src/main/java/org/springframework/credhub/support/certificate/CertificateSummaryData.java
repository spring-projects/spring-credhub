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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A collection of {@link CertificateSummary}s. Clients don't typically instantiate
 * objects of this type, but will receive them in response to requests.
 *
 * @author Scott Frederick
 */
public class CertificateSummaryData {
	private List<CertificateSummary> certificates;

	/**
	 * Create a {@link CertificateSummaryData}.
	 */
	@SuppressWarnings("unused")
	private CertificateSummaryData() {
	}

	/**
	 * Create a {@link CertificateSummaryData} from the provided parameters. Intended for internal
	 * use. Clients will get {@link CertificateSummaryData} objects populated from
	 * CredHub responses.
	 *
	 * @param certificates a collection of {@link CertificateSummary}s
	 */
	public CertificateSummaryData(CertificateSummary... certificates) {
		this.certificates = Arrays.asList(certificates);
	}

	/**
	 * Get the collection of {@link CertificateSummary}s.
	 *
	 * @return the collection of {@link CertificateSummary}s
	 */
	public List<CertificateSummary> getCertificates() {
		return this.certificates;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CertificateSummaryData))
			return false;
		if (!super.equals(o))
			return false;

		CertificateSummaryData that = (CertificateSummaryData) o;

		return certificates != null ? certificates.equals(that.certificates)
				: that.certificates == null;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(certificates);
	}

	@Override
	public String toString() {
		return "CertificateSummaryData{"
				+ "certificates=" + certificates
				+ '}';
	}
}
