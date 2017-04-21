package org.springframework.credhub.core;

import org.springframework.credhub.support.CredHubResponse;
import org.springframework.credhub.support.CredentialDataResponse;
import org.springframework.credhub.support.CredentialData;
import org.springframework.credhub.support.FindResponse;
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

public class CredHubTemplate {
	static final String BASE_URL_PATH = "/api/v1/data";
	static final String ID_URL_PATH = BASE_URL_PATH + "/{id}";
	static final String NAME_URL_QUERY = BASE_URL_PATH + "?name={name}";
	static final String NAME_LIKE_URL_QUERY = BASE_URL_PATH + "?name-like={name}";
	static final String PATH_URL_QUERY = BASE_URL_PATH + "?path={path}";

	private final RestTemplate restTemplate;

	CredHubTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public CredHubTemplate(String apiUriBase, ClientHttpRequestFactory clientHttpRequestFactory) {
		this.restTemplate = CredHubClient.createRestTemplate(apiUriBase, clientHttpRequestFactory);
	}

	public CredentialData write(final WriteRequest writeRequest) {
		return doWithRest(new RestOperationsCallback<CredentialData>() {
			@Override
			public CredentialData doWithRestOperations(RestOperations restOperations) {
				ResponseEntity<CredentialDataResponse> response =
						restOperations.exchange(BASE_URL_PATH, HttpMethod.PUT,
								new HttpEntity<WriteRequest>(writeRequest),
								CredentialDataResponse.class);

				throwExceptionOnError(response);

				return response.getBody().getData().get(0);
			}
		});
	}

	public CredentialData getById(final String id) {
		return doWithRest(new RestOperationsCallback<CredentialData>() {
			@Override
			public CredentialData doWithRestOperations(RestOperations restOperations) {
				ResponseEntity<CredentialDataResponse> response =
						restOperations.getForEntity(ID_URL_PATH, CredentialDataResponse.class, id);

				throwExceptionOnError(response);

				return response.getBody().getData().get(0);
			}
		});
	}

	public FindResponse findByName(final String name) {
		return doWithRest(new RestOperationsCallback<FindResponse>() {
			@Override
			public FindResponse doWithRestOperations(RestOperations restOperations) {
				ResponseEntity<FindResponse> response =
						restOperations.getForEntity(NAME_LIKE_URL_QUERY, FindResponse.class, name);

				throwExceptionOnError(response);

				return response.getBody();
			}
		});
	}

	public FindResponse findByPath(final String path) {
		return doWithRest(new RestOperationsCallback<FindResponse>() {
			@Override
			public FindResponse doWithRestOperations(RestOperations restOperations) {
				ResponseEntity<FindResponse> response =
						restOperations.getForEntity(PATH_URL_QUERY, FindResponse.class, path);

				throwExceptionOnError(response);

				return response.getBody();
			}
		});
	}

	public void deleteByName(final String name) {
		doWithRest(new RestOperationsCallback<Void>() {
			@Override
			public Void doWithRestOperations(RestOperations restOperations) {
				restOperations.delete(NAME_URL_QUERY, name);
				return null;
			}
		});
	}

	public <T> T doWithRest(RestOperationsCallback<T> callback) {
		Assert.notNull(callback, "Callback must not be null");

		try {
			return callback.doWithRestOperations(restTemplate);
		} catch (HttpStatusCodeException e) {
			throw new CredHubException(e);
		}
	}

	private void throwExceptionOnError(ResponseEntity<? extends CredHubResponse> response) {
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new CredHubException(response.getStatusCode());
		}

		if (response.getBody().getErrorMessage() != null) {
			throw new CredHubException(response.getBody().getErrorMessage());
		}
	}
}
