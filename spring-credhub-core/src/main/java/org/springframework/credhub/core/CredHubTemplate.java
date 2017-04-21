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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.CredentialSummaryData;
import org.springframework.credhub.support.WriteRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Implements the main interaction with CredHub to save, retrieve,
 * and delete credentials.
 *
 * @author Scott Frederick 
 */
public class CredHubTemplate implements CredHubOperations {
	static final String BASE_URL_PATH = "/api/v1/data";
	static final String ID_URL_PATH = BASE_URL_PATH + "/{id}";
	static final String NAME_URL_QUERY = BASE_URL_PATH + "?name={name}";
	static final String NAME_LIKE_URL_QUERY = BASE_URL_PATH + "?name-like={name}";
	static final String PATH_URL_QUERY = BASE_URL_PATH + "?path={path}";
	static final String INTERPOLATE_URL_PATH = "/api/v1/vcap";

	static final String VCAP_SERVICES_KEY = "VCAP_SERVICES";

	private final RestTemplate restTemplate;

	/**
	 * Create a new {@link CredHubTemplate} using the provided {@link RestTemplate}.
	 * Intended for internal testing only.
	 *
	 * @param restTemplate the {@link RestTemplate} to use for interactions with CredHub
	 */
	CredHubTemplate(RestTemplate restTemplate) {
		Assert.notNull(restTemplate, "restTemplate must not be null");

		this.restTemplate = restTemplate;
	}

	/**
	 * Create a new {@link CredHubTemplate} using the provided base URI and
	 * {@link ClientHttpRequestFactory}.
	 *
	 * @param apiUriBase the base URI for the CredHub server (scheme, host, and port);
	 * must not be {@literal null}
	 * @param clientHttpRequestFactory the {@link ClientHttpRequestFactory} to use when
	 * creating new connections
	 */
	public CredHubTemplate(String apiUriBase, ClientHttpRequestFactory clientHttpRequestFactory) {
		Assert.notNull(apiUriBase, "apiUriBase must not be null");
		Assert.notNull(clientHttpRequestFactory, "clientHttpRequestFactory must not be null");

		this.restTemplate = CredHubClient.createRestTemplate(apiUriBase,
				clientHttpRequestFactory);
	}

	@Override
	public CredentialDetails write(final WriteRequest writeRequest) {
		Assert.notNull(writeRequest, "writeRequest must not be null");

		return doWithRest(new RestOperationsCallback<CredentialDetails>() {
			@Override
			public CredentialDetails doWithRestOperations(
					RestOperations restOperations) {
				ResponseEntity<CredentialDetails> response = restOperations
						.exchange(BASE_URL_PATH, HttpMethod.PUT,
								new HttpEntity<WriteRequest>(writeRequest),
								CredentialDetails.class);

				throwExceptionOnError(response);

				return response.getBody();
			}
		});
	}

	@Override
	public CredentialDetails getById(final String id) {
		Assert.notNull(id, "credential id must not be null");

		return doWithRest(new RestOperationsCallback<CredentialDetails>() {
			@Override
			public CredentialDetails doWithRestOperations(
					RestOperations restOperations) {
				ResponseEntity<CredentialDetails> response = restOperations
						.getForEntity(ID_URL_PATH, CredentialDetails.class, id);

				throwExceptionOnError(response);

				return response.getBody();
			}
		});
	}

	@Override
	public List<CredentialDetails> getByName(final String name) {
		Assert.notNull(name, "credential name must not be null");

		return doWithRest(new RestOperationsCallback<List<CredentialDetails>>() {
			@Override
			public List<CredentialDetails> doWithRestOperations(
					RestOperations restOperations) {
				ResponseEntity<CredentialDetailsData> response = restOperations
						.getForEntity(NAME_URL_QUERY, CredentialDetailsData.class,
								name);

				throwExceptionOnError(response);

				return response.getBody().getData();
			}
		});
	}

	@Override
	public List<CredentialDetails> getByName(final CredentialName name) {
		Assert.notNull(name, "credential name must not be null");

		return getByName(name.getName());
	}

	@Override
	public List<CredentialSummary> findByName(final String name) {
		Assert.notNull(name, "credential name must not be null");

		return doWithRest(new RestOperationsCallback<List<CredentialSummary>>() {
			@Override
			public List<CredentialSummary> doWithRestOperations(
					RestOperations restOperations) {
				ResponseEntity<CredentialSummaryData> response = restOperations
						.getForEntity(NAME_LIKE_URL_QUERY,
								CredentialSummaryData.class, name);

				throwExceptionOnError(response);

				return response.getBody().getCredentials();
			}
		});
	}

	@Override
	public List<CredentialSummary> findByName(final CredentialName name) {
		Assert.notNull(name, "credential name must not be null");

		return findByName(name.getName());
	}

	@Override
	public List<CredentialSummary> findByPath(final String path) {
		Assert.notNull(path, "credential path must not be null");

		return doWithRest(new RestOperationsCallback<List<CredentialSummary>>() {
			@Override
			public List<CredentialSummary> doWithRestOperations(
					RestOperations restOperations) {
				ResponseEntity<CredentialSummaryData> response = restOperations
						.getForEntity(PATH_URL_QUERY, CredentialSummaryData.class,
								path);

				throwExceptionOnError(response);

				return response.getBody().getCredentials();
			}
		});
	}

	@Override
	public void deleteByName(final String name) {
		Assert.notNull(name, "credential name must not be null");

		doWithRest(new RestOperationsCallback<Void>() {
			@Override
			public Void doWithRestOperations(RestOperations restOperations) {
				restOperations.delete(NAME_URL_QUERY, name);
				return null;
			}
		});
	}

	@Override
	public void deleteByName(final CredentialName name) {
		Assert.notNull(name, "credential name must not be null");

		deleteByName(name.getName());
	}

	@Override
	public Map<String, Object> interpolateServiceData(final Map<String, Object> serviceData) {
		Assert.notNull(serviceData, "serviceData must not be null");

		return doWithRest(new RestOperationsCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doWithRestOperations(RestOperations restOperations) {
				Map<String, Map<String, Object>> wrappedServiceData = wrapServiceDataRequest(serviceData);

				ResponseEntity<Map<String, Map<String, Object>>> response = restOperations
						.exchange(INTERPOLATE_URL_PATH, HttpMethod.POST,
								new HttpEntity<Map<String, Map<String, Object>>>(wrappedServiceData), mapType());

				throwExceptionOnError(response);

				return response.getBody().get(VCAP_SERVICES_KEY);
			}
		});
	}

	@Override
	public <T> T doWithRest(RestOperationsCallback<T> callback) {
		Assert.notNull(callback, "callback must not be null");

		try {
			return callback.doWithRestOperations(restTemplate);
		}
		catch (HttpStatusCodeException e) {
			throw new CredHubException(e);
		}
	}

	/**
	 * Wrap the service data structure with the "VCAP_SERVICES" key as required by the
	 * CredHub interpolation API.
	 *
	 * @param serviceData a {@literal Map} of services details
	 * @return the provided {@literal serviceData} structure wrapped with the "VCAP_SERVICES" key
	 */
	private Map<String, Map<String, Object>> wrapServiceDataRequest(Map<String, Object> serviceData) {
		Map<String, Map<String, Object>> wrappedServiceData = new HashMap<String, Map<String, Object>>();
		wrappedServiceData.put(VCAP_SERVICES_KEY, serviceData);
		return wrappedServiceData;
	}

	/**
	 * Helper method to create a type reference for use by {@link RestTemplate}.
	 *
	 * @return the type reference for a {@literal Map} type
	 */
	private ParameterizedTypeReference<Map<String, Map<String, Object>>> mapType() {
		return new ParameterizedTypeReference<Map<String, Map<String, Object>>>() {};
	}

	/**
	 * Helper method to throw an appropriate exception if a request to CredHub
	 * returns with an error code.
	 *
	 * @param response a {@link ResponseEntity} returned from {@link RestTemplate}
	 */
	private void throwExceptionOnError(ResponseEntity<?> response) {
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new CredHubException(response.getStatusCode());
		}
	}
}
