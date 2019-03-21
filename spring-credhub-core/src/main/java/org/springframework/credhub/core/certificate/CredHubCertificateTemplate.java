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

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.core.ExceptionUtils;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.credhub.support.certificate.CertificateSummaryData;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.certificate.CertificateCredentialDetails;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the interactions with CredHub to retrieve, regenerate, and update
 * certificates.
 *
 * @author Scott Frederick 
 */
public class CredHubCertificateTemplate implements CredHubCertificateOperations {
	static final String BASE_URL_PATH = "/api/v1/certificates";
	static final String NAME_URL_QUERY = BASE_URL_PATH + "?name={name}";
	static final String REGENERATE_URL_PATH = BASE_URL_PATH + "/{id}/regenerate";
	static final String UPDATE_TRANSITIONAL_URL_PATH = BASE_URL_PATH + "/{id}/update_transitional_version";
	static final String BULK_REGENERATE_URL_PATH = "/api/v1/bulk-regenerate";

	static final String TRANSITIONAL_REQUEST_FIELD = "set_as_transitional";
	static final String VERSION_REQUEST_FIELD = "version";
	static final String SIGNED_BY_REQUEST_FIELD = "signed_by";
	static final String REGENERATED_CREDENTIALS_RESPONSE_FIELD = "regenerated_credentials";

	private CredHubOperations credHubOperations;

	/**
	 * Create a new {@link CredHubCertificateTemplate}.
	 *
	 * @param credHubOperations the {@link CredHubOperations} to use for interactions with CredHub
	 */
	public CredHubCertificateTemplate(CredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	@Override
	public List<CertificateSummary> getAll() {
		return credHubOperations.doWithRest(restOperations -> {
			ResponseEntity<CertificateSummaryData> response = restOperations
					.getForEntity(BASE_URL_PATH, CertificateSummaryData.class);

			ExceptionUtils.throwExceptionOnError(response);

			return response.getBody().getCertificates();
		});
	}

	@Override
	public CertificateSummary getByName(final CredentialName name) {
		Assert.notNull(name, "certificate name must not be null");

		return credHubOperations.doWithRest(restOperations -> {
			ResponseEntity<CertificateSummaryData> response = restOperations
					.getForEntity(NAME_URL_QUERY, CertificateSummaryData.class, name.getName());

			ExceptionUtils.throwExceptionOnError(response);

			return response.getBody().getCertificates().get(0);
		});
	}

	@Override
	public CertificateCredentialDetails regenerate(final String id, final boolean setAsTransitional) {
		Assert.notNull(id, "credential ID must not be null");

		final ParameterizedTypeReference<CertificateCredentialDetails> ref =
				new ParameterizedTypeReference<CertificateCredentialDetails>() {};

		return credHubOperations.doWithRest(restOperations -> {
			Map<String, Boolean> request = new HashMap<>(1);
			request.put(TRANSITIONAL_REQUEST_FIELD, setAsTransitional);

			ResponseEntity<CertificateCredentialDetails> response =
					restOperations.exchange(REGENERATE_URL_PATH, HttpMethod.POST,
							new HttpEntity<Object>(request), ref, id);

			ExceptionUtils.throwExceptionOnError(response);

			return response.getBody();
		});
	}

	@Override
	public List<CredentialName> regenerate(final CredentialName certificateName) {
		Assert.notNull(certificateName, "certificate name must not be null");

		final ParameterizedTypeReference<Map<String, List<CredentialName>>> ref =
				new ParameterizedTypeReference<Map<String, List<CredentialName>>>() {};

		return credHubOperations.doWithRest(restOperations -> {
			Map<String, Object> request = new HashMap<>(1);
			request.put(SIGNED_BY_REQUEST_FIELD, certificateName.getName());

			ResponseEntity<Map<String, List<CredentialName>>> response =
					restOperations.exchange(BULK_REGENERATE_URL_PATH, HttpMethod.POST,
							new HttpEntity<>(request), ref);

			ExceptionUtils.throwExceptionOnError(response);

			return response.getBody().get(REGENERATED_CREDENTIALS_RESPONSE_FIELD);
		});
	}

	public List<CertificateCredentialDetails> updateTransitionalVersion(final String id,
																		final String versionId) {
		Assert.notNull(id, "credential ID must not be null");

		final ParameterizedTypeReference<List<CertificateCredentialDetails>> ref =
				new ParameterizedTypeReference<List<CertificateCredentialDetails>>() {};

		return credHubOperations.doWithRest(restOperations -> {
			Map<String, String> request = new HashMap<>(1);
			request.put(VERSION_REQUEST_FIELD, versionId);

			ResponseEntity<List<CertificateCredentialDetails>> response =
					restOperations.exchange(UPDATE_TRANSITIONAL_URL_PATH, HttpMethod.PUT,
							new HttpEntity<Object>(request), ref, id);

			ExceptionUtils.throwExceptionOnError(response);

			return response.getBody();
		});
	}
}
