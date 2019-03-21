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

package org.springframework.credhub.core.credential;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.core.ExceptionUtils;
import org.springframework.credhub.core.ReactiveCredHubOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.CredentialSummaryData;
import org.springframework.credhub.support.ParametersRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the interactions with CredHub to save, retrieve,
 * and delete credentials.
 *
 * @author Scott Frederick 
 */
public class ReactiveCredHubCredentialTemplate implements ReactiveCredHubCredentialOperations {
	private static final String BASE_URL_PATH = "/api/v1/data";
	private static final String ID_URL_PATH = BASE_URL_PATH + "/{id}";
	private static final String NAME_URL_QUERY = BASE_URL_PATH + "?name={name}";
	private static final String NAME_URL_QUERY_CURRENT = NAME_URL_QUERY + "&current=true";
	private static final String NAME_URL_QUERY_VERSIONS = NAME_URL_QUERY + "&versions={versions}";
	private static final String NAME_LIKE_URL_QUERY = BASE_URL_PATH + "?name-like={name}";
	private static final String PATH_URL_QUERY = BASE_URL_PATH + "?path={path}";
	private static final String REGENERATE_URL_PATH = "/api/v1/regenerate";

	private static final String NAME_REQUEST_FIELD = "name";

	private ReactiveCredHubOperations credHubOperations;

	/**
	 * Create a new {@link ReactiveCredHubCredentialTemplate}.
	 *
	 * @param credHubOperations the {@link ReactiveCredHubOperations} to use for interactions with CredHub
	 */
	public ReactiveCredHubCredentialTemplate(ReactiveCredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	@Override
	public <T> Mono<CredentialDetails<T>> write(final CredentialRequest<T> credentialRequest) {
		Assert.notNull(credentialRequest, "credentialRequest must not be null");

		final ParameterizedTypeReference<CredentialDetails<T>> ref =
				new ParameterizedTypeReference<CredentialDetails<T>>() {};

		return credHubOperations.doWithWebClient(webClient -> webClient
				.put()
				.uri(BASE_URL_PATH)
				.syncBody(credentialRequest)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(ref));
	}

	@Override
	public <T, P> Mono<CredentialDetails<T>> generate(final ParametersRequest<P> parametersRequest,
													  Class<T> credentialType) {
		Assert.notNull(parametersRequest, "parametersRequest must not be null");

		final ParameterizedTypeReference<CredentialDetails<T>> ref =
				new ParameterizedTypeReference<CredentialDetails<T>>() {};

		return credHubOperations.doWithWebClient(webClient -> webClient
				.post()
				.uri(BASE_URL_PATH)
				.syncBody(parametersRequest)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(ref));
	}

	@Override
	public <T> Mono<CredentialDetails<T>> regenerate(final CredentialName name, Class<T> credentialType) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		final ParameterizedTypeReference<CredentialDetails<T>> ref =
				new ParameterizedTypeReference<CredentialDetails<T>>() {};

		Map<String, Object> request = new HashMap<>(1);
		request.put(NAME_REQUEST_FIELD, name.getName());

		return credHubOperations.doWithWebClient(webClient -> webClient
				.post()
				.uri(REGENERATE_URL_PATH)
				.syncBody(request)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(ref));
	}

	@Override
	public <T> Mono<CredentialDetails<T>> getById(final String id, final Class<T> credentialType) {
		Assert.notNull(id, "credential id must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		final ParameterizedTypeReference<CredentialDetails<T>> ref =
				new ParameterizedTypeReference<CredentialDetails<T>>() {};

		return credHubOperations.doWithWebClient(webClient -> webClient
				.get()
				.uri(ID_URL_PATH, id)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(ref));
	}

	@Override
	public <T> Mono<CredentialDetails<T>> getByName(final CredentialName name, final Class<T> credentialType) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		final ParameterizedTypeReference<CredentialDetailsData<T>> ref =
				new ParameterizedTypeReference<CredentialDetailsData<T>>() {};

		return credHubOperations.doWithWebClient(webClient -> webClient
				.get()
				.uri(NAME_URL_QUERY_CURRENT, name.getName())
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(ref)
				.map(body -> body.getData().get(0)));
	}

	@Override
	public <T> Flux<CredentialDetails<T>> getByNameWithHistory(final CredentialName name, final Class<T> credentialType) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		final ParameterizedTypeReference<CredentialDetailsData<T>> ref =
				new ParameterizedTypeReference<CredentialDetailsData<T>>() {};

		return credHubOperations.doWithWebClient(webClient -> webClient
				.get()
				.uri(NAME_URL_QUERY, name.getName())
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToFlux(ref)
				.flatMap(body -> Flux.fromIterable(body.getData())));
	}

	@Override
	public <T> Flux<CredentialDetails<T>> getByNameWithHistory(final CredentialName name, final int versions,
															   final Class<T> credentialType) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		final ParameterizedTypeReference<CredentialDetailsData<T>> ref =
				new ParameterizedTypeReference<CredentialDetailsData<T>>() {};

		return credHubOperations.doWithWebClient(webClient -> webClient
				.get()
				.uri(NAME_URL_QUERY_VERSIONS, name.getName(), versions)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToFlux(ref)
				.flatMap(body -> Flux.fromIterable(body.getData())));
	}

	@Override
	public Flux<CredentialSummary> findByName(final CredentialName name) {
		Assert.notNull(name, "credential name must not be null");

		return credHubOperations.doWithWebClient(webClient -> webClient
				.get()
				.uri(NAME_LIKE_URL_QUERY, name.getName())
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(CredentialSummaryData.class)
				.flatMapMany(data -> Flux.fromIterable(data.getCredentials())));
	}

	@Override
	public Flux<CredentialSummary> findByPath(final String path) {
		Assert.notNull(path, "credential path must not be null");

		return credHubOperations.doWithWebClient(webClient -> webClient
				.get()
				.uri(PATH_URL_QUERY, path)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(CredentialSummaryData.class)
				.flatMapMany(data -> Flux.fromIterable(data.getCredentials())));
	}

	@Override
	public Mono<Void> deleteByName(final CredentialName name) {
		Assert.notNull(name, "credential name must not be null");

		return credHubOperations.doWithWebClient(webClient -> webClient
				.delete()
				.uri(NAME_URL_QUERY, name.getName())
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(Void.class));
	}
}
