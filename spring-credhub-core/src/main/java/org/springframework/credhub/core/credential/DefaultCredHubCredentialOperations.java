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

package org.springframework.credhub.core.credential;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.CredentialSummaryData;
import org.springframework.credhub.support.ParametersRequest;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

/**
 * Default {@link CredHubCredentialOperations} implementation, written against a
 * {@link RestClient}.
 *
 * @author Roy Clarkson
 * @since 4.2.0
 */
public class DefaultCredHubCredentialOperations implements CredHubCredentialOperations {

	private static final String BASE_URL_PATH = "/api/v1/data";

	private static final String ID_URL_PATH = BASE_URL_PATH + "/{id}";

	private static final String NAME_URL_QUERY = BASE_URL_PATH + "?name={name}";

	private static final String NAME_URL_QUERY_CURRENT = NAME_URL_QUERY + "&current=true";

	private static final String NAME_URL_QUERY_VERSIONS = NAME_URL_QUERY + "&versions={versions}";

	private static final String NAME_LIKE_URL_QUERY = BASE_URL_PATH + "?name-like={name}";

	private static final String PATH_URL_QUERY = BASE_URL_PATH + "?path={path}";

	private static final String REGENERATE_URL_PATH = "/api/v1/regenerate";

	private static final String NAME_REQUEST_FIELD = "name";

	private static final String METADATA_REQUEST_FIELD = "metadata";

	private final RestClient restClient;

	/**
	 * Create a new instance using the provided {@link RestClient}.
	 * @param restClient a {@link RestClient} configured for communication with CredHub,
	 * with error responses translated to
	 * {@link org.springframework.credhub.core.CredHubException}
	 */
	public DefaultCredHubCredentialOperations(RestClient restClient) {
		Assert.notNull(restClient, "restClient must not be null");
		this.restClient = restClient;
	}

	@Override
	public <T> CredentialDetails<T> write(CredentialRequest<T> credentialRequest) {
		Assert.notNull(credentialRequest, "credentialRequest must not be null");

		return this.restClient.put()
			.uri(BASE_URL_PATH)
			.body(credentialRequest)
			.retrieve()
			.body(new ParameterizedTypeReference<CredentialDetails<T>>() {
			});
	}

	@Override
	public <T, P> CredentialDetails<T> generate(ParametersRequest<P> parametersRequest) {
		Assert.notNull(parametersRequest, "parametersRequest must not be null");

		return this.restClient.post()
			.uri(BASE_URL_PATH)
			.body(parametersRequest)
			.retrieve()
			.body(new ParameterizedTypeReference<CredentialDetails<T>>() {
			});
	}

	@Override
	public <T> CredentialDetails<T> regenerate(CredentialName name, Class<T> credentialType) {
		return regenerate(name, credentialType, null);
	}

	@Override
	public <T> CredentialDetails<T> regenerate(CredentialName name, Class<T> credentialType,
			Map<String, Object> metadata) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		Map<String, Object> request = new HashMap<>(2);
		request.put(NAME_REQUEST_FIELD, name.getName());
		if (metadata != null) {
			request.put(METADATA_REQUEST_FIELD, metadata);
		}

		return this.restClient.post()
			.uri(REGENERATE_URL_PATH)
			.body(request)
			.retrieve()
			.body(new ParameterizedTypeReference<CredentialDetails<T>>() {
			});
	}

	@Override
	public <T> CredentialDetails<T> getById(String id, Class<T> credentialType) {
		Assert.notNull(id, "credential id must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		return this.restClient.get()
			.uri(ID_URL_PATH, id)
			.retrieve()
			.body(new ParameterizedTypeReference<CredentialDetails<T>>() {
			});
	}

	@Override
	public <T> CredentialDetails<T> getByName(CredentialName name, Class<T> credentialType) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		CredentialDetailsData<T> data = this.restClient.get()
			.uri(NAME_URL_QUERY_CURRENT, name.getName())
			.retrieve()
			.body(new ParameterizedTypeReference<CredentialDetailsData<T>>() {
			});
		return data.getData().get(0);
	}

	@Override
	public <T> List<CredentialDetails<T>> getByNameWithHistory(CredentialName name, Class<T> credentialType) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		CredentialDetailsData<T> data = this.restClient.get()
			.uri(NAME_URL_QUERY, name.getName())
			.retrieve()
			.body(new ParameterizedTypeReference<CredentialDetailsData<T>>() {
			});
		return data.getData();
	}

	@Override
	public <T> List<CredentialDetails<T>> getByNameWithHistory(CredentialName name, int versions,
			Class<T> credentialType) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		CredentialDetailsData<T> data = this.restClient.get()
			.uri(NAME_URL_QUERY_VERSIONS, name.getName(), versions)
			.retrieve()
			.body(new ParameterizedTypeReference<CredentialDetailsData<T>>() {
			});
		return data.getData();
	}

	@Override
	public List<CredentialSummary> findByName(CredentialName name) {
		Assert.notNull(name, "credential name must not be null");

		return this.restClient.get()
			.uri(NAME_LIKE_URL_QUERY, name.getName())
			.retrieve()
			.body(CredentialSummaryData.class)
			.getCredentials();
	}

	@Override
	public List<CredentialSummary> findByPath(String path) {
		Assert.notNull(path, "credential path must not be null");

		return this.restClient.get()
			.uri(PATH_URL_QUERY, path)
			.retrieve()
			.body(CredentialSummaryData.class)
			.getCredentials();
	}

	@Override
	public void deleteByName(CredentialName name) {
		Assert.notNull(name, "credential name must not be null");

		this.restClient.delete().uri(NAME_URL_QUERY, name.getName()).retrieve().toBodilessEntity();
	}

}
