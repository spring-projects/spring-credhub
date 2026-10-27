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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.certificate.CertificateCredential;
import org.springframework.credhub.support.certificate.CertificateCredentialDetails;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.credhub.support.certificate.CertificateSummaryData;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

/**
 * Default {@link CredHubCertificateOperations} implementation, written against a
 * {@link RestClient}.
 *
 * @author Roy Clarkson
 * @since 4.2.0
 */
public class DefaultCredHubCertificateOperations implements CredHubCertificateOperations {

	private static final String BASE_URL_PATH = "/api/v1/certificates";

	private static final String NAME_URL_QUERY = BASE_URL_PATH + "?name={name}";

	private static final String REGENERATE_URL_PATH = BASE_URL_PATH + "/{id}/regenerate";

	private static final String UPDATE_TRANSITIONAL_URL_PATH = BASE_URL_PATH + "/{id}/update_transitional_version";

	private static final String VERSIONS_URL_PATH = BASE_URL_PATH + "/{id}/versions";

	private static final String VERSION_URL_PATH = VERSIONS_URL_PATH + "/{versionId}";

	private static final String BULK_REGENERATE_URL_PATH = "/api/v1/bulk-regenerate";

	private static final String TRANSITIONAL_REQUEST_FIELD = "set_as_transitional";

	private static final String ALLOW_TRANSITIONAL_PARENT_TO_SIGN_REQUEST_FIELD = "allow_transitional_parent_to_sign";

	private static final String KEY_LENGTH_REQUEST_FIELD = "key_length";

	private static final String DURATION_REQUEST_FIELD = "duration";

	private static final String METADATA_REQUEST_FIELD = "metadata";

	private static final String VERSION_REQUEST_FIELD = "version";

	private static final String VALUE_REQUEST_FIELD = "value";

	private static final String VERSION_TRANSITIONAL_REQUEST_FIELD = "transitional";

	private static final String SIGNED_BY_REQUEST_FIELD = "signed_by";

	private static final String REGENERATED_CREDENTIALS_RESPONSE_FIELD = "regenerated_credentials";

	private final RestClient restClient;

	/**
	 * Create a new instance using the provided {@link RestClient}.
	 * @param restClient a {@link RestClient} configured for communication with CredHub,
	 * with error responses translated to
	 * {@link org.springframework.credhub.core.CredHubException}
	 */
	public DefaultCredHubCertificateOperations(RestClient restClient) {
		Assert.notNull(restClient, "restClient must not be null");
		this.restClient = restClient;
	}

	@Override
	public List<CertificateSummary> getAll() {
		return this.restClient.get().uri(BASE_URL_PATH).retrieve().body(CertificateSummaryData.class).getCertificates();
	}

	@Override
	public CertificateSummary getByName(CredentialName name) {
		Assert.notNull(name, "certificate name must not be null");

		return this.restClient.get()
			.uri(NAME_URL_QUERY, name.getName())
			.retrieve()
			.body(CertificateSummaryData.class)
			.getCertificates()
			.get(0);
	}

	@Override
	public CertificateCredentialDetails regenerate(String id, boolean setAsTransitional) {
		return regenerate(id, setAsTransitional, false, null, null, null);
	}

	@Override
	public CertificateCredentialDetails regenerate(String id, boolean setAsTransitional,
			boolean allowTransitionalParentToSign, Integer keyLength, Integer duration, Map<String, Object> metadata) {
		Assert.notNull(id, "credential ID must not be null");

		Map<String, Object> request = new HashMap<>();
		request.put(TRANSITIONAL_REQUEST_FIELD, setAsTransitional);
		if (allowTransitionalParentToSign) {
			request.put(ALLOW_TRANSITIONAL_PARENT_TO_SIGN_REQUEST_FIELD, true);
		}
		if (keyLength != null) {
			request.put(KEY_LENGTH_REQUEST_FIELD, keyLength);
		}
		if (duration != null) {
			request.put(DURATION_REQUEST_FIELD, duration);
		}
		if (metadata != null) {
			request.put(METADATA_REQUEST_FIELD, metadata);
		}

		return this.restClient.post()
			.uri(REGENERATE_URL_PATH, id)
			.body(request)
			.retrieve()
			.body(CertificateCredentialDetails.class);
	}

	@Override
	public List<CredentialName> regenerate(CredentialName certificateName) {
		Assert.notNull(certificateName, "certificate name must not be null");

		Map<String, Object> request = new HashMap<>(1);
		request.put(SIGNED_BY_REQUEST_FIELD, certificateName.getName());

		Map<String, List<CredentialName>> response = this.restClient.post()
			.uri(BULK_REGENERATE_URL_PATH)
			.body(request)
			.retrieve()
			.body(new ParameterizedTypeReference<Map<String, List<CredentialName>>>() {
			});
		return response.get(REGENERATED_CREDENTIALS_RESPONSE_FIELD);
	}

	@Override
	public List<CertificateCredentialDetails> getVersions(String id) {
		Assert.notNull(id, "credential ID must not be null");

		return this.restClient.get()
			.uri(VERSIONS_URL_PATH, id)
			.retrieve()
			.body(new ParameterizedTypeReference<List<CertificateCredentialDetails>>() {
			});
	}

	@Override
	public CertificateCredentialDetails addVersion(String id, CertificateCredential value, boolean transitional) {
		Assert.notNull(id, "credential ID must not be null");
		Assert.notNull(value, "certificate value must not be null");

		Map<String, Object> request = new HashMap<>(2);
		request.put(VALUE_REQUEST_FIELD, value);
		request.put(VERSION_TRANSITIONAL_REQUEST_FIELD, transitional);

		return this.restClient.post()
			.uri(VERSIONS_URL_PATH, id)
			.body(request)
			.retrieve()
			.body(CertificateCredentialDetails.class);
	}

	@Override
	public CertificateCredentialDetails deleteVersion(String id, String versionId) {
		Assert.notNull(id, "credential ID must not be null");
		Assert.notNull(versionId, "version ID must not be null");

		return this.restClient.delete()
			.uri(VERSION_URL_PATH, id, versionId)
			.retrieve()
			.body(CertificateCredentialDetails.class);
	}

	@Override
	public List<CertificateCredentialDetails> updateTransitionalVersion(String id, String versionId) {
		Assert.notNull(id, "credential ID must not be null");

		Map<String, String> request = new HashMap<>(1);
		request.put(VERSION_REQUEST_FIELD, versionId);

		return this.restClient.put()
			.uri(UPDATE_TRANSITIONAL_URL_PATH, id)
			.body(request)
			.retrieve()
			.body(new ParameterizedTypeReference<List<CertificateCredentialDetails>>() {
			});
	}

}
