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

package org.springframework.credhub.core;

import org.springframework.credhub.support.CertificateSummary;
import org.springframework.credhub.support.CertificateSummaryData;
import org.springframework.credhub.support.CredentialName;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

import java.util.List;

/**
 * Implements the interactions with CredHub to retrieve, regenerate, and update
 *  * certificates.
 *
 * @author Scott Frederick 
 */
public class CredHubCertificateTemplate implements CredHubCertificateOperations {
	static final String BASE_URL_PATH = "/api/v1/certificates";
	static final String NAME_URL_QUERY = BASE_URL_PATH + "?name={name}";

	private CredHubOperations credHubOperations;

	/**
	 * Create a new {@link CredHubCertificateTemplate}.
	 *
	 * @param credHubOperations the {@link CredHubOperations} to use for interactions with CredHub
	 */
	CredHubCertificateTemplate(CredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	@Override
	public List<CertificateSummary> getAll() {
		return credHubOperations.doWithRest(new RestOperationsCallback<List<CertificateSummary>>() {
			@Override
			public List<CertificateSummary> doWithRestOperations(
					RestOperations restOperations) {
				ResponseEntity<CertificateSummaryData> response = restOperations
						.getForEntity(BASE_URL_PATH, CertificateSummaryData.class);

				ExceptionUtils.throwExceptionOnError(response);

				return response.getBody().getCertificates();
			}
		});
	}

	@Override
	public CertificateSummary getByName(final CredentialName name) {
		Assert.notNull(name, "certificate name must not be null");

		return credHubOperations.doWithRest(new RestOperationsCallback<CertificateSummary>() {
			@Override
			public CertificateSummary doWithRestOperations(
					RestOperations restOperations) {
				ResponseEntity<CertificateSummaryData> response = restOperations
						.getForEntity(NAME_URL_QUERY, CertificateSummaryData.class, name.getName());

				ExceptionUtils.throwExceptionOnError(response);

				return response.getBody().getCertificates().get(0);
			}
		});
	}
}
