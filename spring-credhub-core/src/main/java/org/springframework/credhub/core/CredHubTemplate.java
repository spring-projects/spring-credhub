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
import org.springframework.credhub.support.ParametersRequest;
import org.springframework.credhub.support.VcapServicesData;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static org.springframework.credhub.core.TypeUtils.getDetailsDataReference;
import static org.springframework.credhub.core.TypeUtils.getDetailsReference;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

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
	@SuppressWarnings("unchecked")
	public <T> CredentialDetails<T> write(final CredentialRequest<T> credentialRequest) {
		Assert.notNull(credentialRequest, "credentialRequest must not be null");

		Class<T> credentialType = (Class<T>) credentialRequest.getValue().getClass();
		final ParameterizedTypeReference<CredentialDetails<T>> ref = getDetailsReference(credentialType);

		return doWithRest(new RestOperationsCallback<CredentialDetails<T>>() {
			@Override
			public CredentialDetails<T> doWithRestOperations(RestOperations restOperations) {
				ResponseEntity<CredentialDetails<T>> response =
						restOperations.exchange(BASE_URL_PATH, PUT,
								new HttpEntity<CredentialRequest<T>>(credentialRequest), ref);

				throwExceptionOnError(response);

				return response.getBody();
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T, P> CredentialDetails<T> generate(final ParametersRequest<P> parametersRequest) {
		Assert.notNull(parametersRequest, "generateRequest must not be null");

		Class<T> credentialType = (Class<T>) parametersRequest.getParameters().getClass();
		final ParameterizedTypeReference<CredentialDetails<T>> ref = getDetailsReference(credentialType);

		return doWithRest(new RestOperationsCallback<CredentialDetails<T>>() {
			@Override
			public CredentialDetails<T> doWithRestOperations(RestOperations restOperations) {
				ResponseEntity<CredentialDetails<T>> response =
						restOperations.exchange(BASE_URL_PATH, POST,
								new HttpEntity<ParametersRequest<P>>(parametersRequest), ref);

				throwExceptionOnError(response);

				return response.getBody();
			}
		});
	}

	@Override
	public <T> CredentialDetails<T> getById(final String id, Class<T> credentialType) {
		Assert.notNull(id, "credential id must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		final ParameterizedTypeReference<CredentialDetails<T>> ref = getDetailsReference(credentialType);

		return doWithRest(new RestOperationsCallback<CredentialDetails<T>>() {
			@Override
			public CredentialDetails<T> doWithRestOperations(RestOperations restOperations) {
				ResponseEntity<CredentialDetails<T>> response =
						restOperations.exchange(ID_URL_PATH, GET, null, ref, id);

				throwExceptionOnError(response);

				return response.getBody();
			}
		});
	}

	@Override
	public <T> List<CredentialDetails<T>> getByName(final String name, Class<T> credentialType) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(credentialType, "credential type must not be null");

		final ParameterizedTypeReference<CredentialDetailsData<T>> ref = getDetailsDataReference(credentialType);

		return doWithRest(new RestOperationsCallback<List<CredentialDetails<T>>>() {
			@Override
			public List<CredentialDetails<T>> doWithRestOperations(RestOperations restOperations) {
				ResponseEntity<CredentialDetailsData<T>> response =
						restOperations.exchange(NAME_URL_QUERY, GET, null, ref, name);

				throwExceptionOnError(response);

				return response.getBody().getData();
			}
		});
	}

	@Override
	public <T> List<CredentialDetails<T>> getByName(final CredentialName name, Class<T> credentialType) {
		Assert.notNull(name, "credential name must not be null");

		return getByName(name.getName(), credentialType);
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
	public VcapServicesData interpolateServiceData(final VcapServicesData serviceData) {
		Assert.notNull(serviceData, "serviceData must not be null");

		return doWithRest(new RestOperationsCallback<VcapServicesData>() {
			@Override
			public VcapServicesData doWithRestOperations(RestOperations restOperations) {
				Map<String, VcapServicesData> wrappedServiceData = wrapServiceDataRequest(serviceData);

				ResponseEntity<Map<String, VcapServicesData>> response = restOperations
						.exchange(INTERPOLATE_URL_PATH, POST,
								new HttpEntity<Map<String, VcapServicesData>>(wrappedServiceData), mapType());

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
	private Map<String, VcapServicesData> wrapServiceDataRequest(VcapServicesData serviceData) {
		Map<String, VcapServicesData> wrappedServiceData = new HashMap<String, VcapServicesData>();
		wrappedServiceData.put(VCAP_SERVICES_KEY, serviceData);
		return wrappedServiceData;
	}

	/**
	 * Helper method to create a type reference for use by {@link RestTemplate}.
	 *
	 * @return the type reference for a {@literal Map} type
	 */
	private ParameterizedTypeReference<Map<String, VcapServicesData>> mapType() {
		return new ParameterizedTypeReference<Map<String, VcapServicesData>>() {};
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
