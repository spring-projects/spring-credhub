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
import org.springframework.credhub.core.ExceptionUtils;
import org.springframework.credhub.core.ReactiveCredHubOperations;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.certificate.CertificateCredentialDetails;
import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.credhub.support.certificate.CertificateSummaryData;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the interactions with CredHub to retrieve, regenerate, and update
 * certificates.
 *
 * @author Scott Frederick 
 */
public class ReactiveCredHubCertificateTemplate implements ReactiveCredHubCertificateOperations {
	private static final String BASE_URL_PATH = "/api/v1/certificates";
	private static final String NAME_URL_QUERY = BASE_URL_PATH + "?name={name}";
	private static final String REGENERATE_URL_PATH = BASE_URL_PATH + "/{id}/regenerate";
	private static final String UPDATE_TRANSITIONAL_URL_PATH = BASE_URL_PATH + "/{id}/update_transitional_version";
	private static final String BULK_REGENERATE_URL_PATH = "/api/v1/bulk-regenerate";

	private static final String TRANSITIONAL_REQUEST_FIELD = "set_as_transitional";
	private static final String VERSION_REQUEST_FIELD = "version";
	private static final String SIGNED_BY_REQUEST_FIELD = "signed_by";
	private static final String REGENERATED_CREDENTIALS_RESPONSE_FIELD = "regenerated_credentials";

	private ReactiveCredHubOperations credHubOperations;

	/**
	 * Create a new {@link ReactiveCredHubCertificateTemplate}.
	 *
	 * @param credHubOperations the {@link ReactiveCredHubOperations} to use for interactions with CredHub
	 */
	public ReactiveCredHubCertificateTemplate(ReactiveCredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	@Override
	public Flux<CertificateSummary> getAll() {
		return credHubOperations.doWithWebClient(webClient -> webClient
				.get()
				.uri(BASE_URL_PATH)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(CertificateSummaryData.class)
				.flatMapMany(data -> Flux.fromIterable(data.getCertificates())));
	}

	@Override
	public Mono<CertificateSummary> getByName(final CredentialName name) {
		Assert.notNull(name, "certificate name must not be null");

		return credHubOperations.doWithWebClient(webClient -> webClient
				.get()
				.uri(NAME_URL_QUERY, name.getName())
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(CertificateSummaryData.class)
				.flatMapMany(data -> Flux.fromIterable(data.getCertificates())))
				.single();
	}

	@Override
	public Mono<CertificateCredentialDetails> regenerate(final String id, final boolean setAsTransitional) {
		Assert.notNull(id, "credential ID must not be null");

		final ParameterizedTypeReference<CertificateCredentialDetails> ref =
				new ParameterizedTypeReference<CertificateCredentialDetails>() {};

		Map<String, Boolean> request = new HashMap<>(1);
		request.put(TRANSITIONAL_REQUEST_FIELD, setAsTransitional);

		return credHubOperations.doWithWebClient(webClient -> webClient
				.post()
				.uri(REGENERATE_URL_PATH, id)
				.syncBody(request)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(ref));
	}

	@Override
	public Flux<CredentialName> regenerate(final CredentialName certificateName) {
		Assert.notNull(certificateName, "certificate name must not be null");

		final ParameterizedTypeReference<Map<String, List<CredentialName>>> ref =
				new ParameterizedTypeReference<Map<String, List<CredentialName>>>() {};

		Map<String, Object> request = new HashMap<>(1);
		request.put(SIGNED_BY_REQUEST_FIELD, certificateName.getName());

		return credHubOperations.doWithWebClient(webClient -> webClient
				.post()
				.uri(BULK_REGENERATE_URL_PATH)
				.syncBody(request)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToFlux(ref)
				.flatMap(body -> Flux.fromIterable(body.get(REGENERATED_CREDENTIALS_RESPONSE_FIELD))));
	}

	public Flux<CertificateCredentialDetails> updateTransitionalVersion(final String id,
																		final String versionId) {
		Assert.notNull(id, "credential ID must not be null");

		Map<String, String> request = new HashMap<>(1);
		request.put(VERSION_REQUEST_FIELD, versionId);

		return credHubOperations.doWithWebClient(webClient -> webClient
				.put()
				.uri(UPDATE_TRANSITIONAL_URL_PATH, id)
				.syncBody(request)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToFlux(CertificateCredentialDetails.class));
	}
}
