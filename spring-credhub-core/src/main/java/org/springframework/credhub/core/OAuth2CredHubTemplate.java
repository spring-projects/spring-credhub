package org.springframework.credhub.core;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.web.client.RestTemplate;

/**
 * Superclass of {@link CredHubTemplate}. Provides a pre configured
 * {@link OAuth2RestTemplate} for CredHub.
 * 
 * @author Daniel Lavoie
 * 
 */
public class OAuth2CredHubTemplate extends CredHubTemplate {

	public OAuth2CredHubTemplate(OAuth2ProtectedResourceDetails resource,
			String apiUriBase, ClientHttpRequestFactory clientHttpRequestFactory) {
		super(buildRestTemplate(resource, apiUriBase, clientHttpRequestFactory));
	}

	private static RestTemplate buildRestTemplate(OAuth2ProtectedResourceDetails resource,
			String apiUriBase, ClientHttpRequestFactory clientHttpRequestFactory) {
		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resource);

		CredHubClient.configureRestTemplate(restTemplate, apiUriBase,
				clientHttpRequestFactory);

		return restTemplate;
	}

}
