/*
 *
 *  * Copyright 2013-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.credhub.core;

/**
 * Properties containing information about a CredHub server.
 *
 * @author Scott Frederick
 * @author Daniel Lavoie
 */
public class CredHubProperties {
	private String url;
	private OAuth2 oauth2;

	/**
	 * Create a new instance without initializing properties.
	 */
	public CredHubProperties() {
	}

	/**
	 * Get the base URI for the CredHub server (scheme, host, and port). This value will
	 * be prepended to all requests to CredHub.
	 *
	 * @return the base URI
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Set the base URI for the CredHub server (scheme, host, and port). This value will
	 * be prepended to all requests to CredHub.
	 *
	 * @param url the base URI for the CredHub server
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Get the OAuth2 properties.
	 *
	 * @return the OAuth2 properties.
	 */
	public OAuth2 getOauth2() {
		return oauth2;
	}

	/**
	 * Set the OAuth2 properties.
	 *
	 * @param oauth2 the OAuth2 properties
	 */
	public void setOauth2(OAuth2 oauth2) {
		this.oauth2 = oauth2;
	}

	/**
	 * Properties containing OAuth2 credentials for CredHub connectivity.
	 */
	public static class OAuth2 {
		private String registrationId;

		/**
		 * Create a new instance without initializing properties.
		 */
		public OAuth2() {
		}

		/**
		 * Get the OAuth2 client registration ID used to authenticate with CredHub.
		 *
		 * @return the OAuth2 registration ID
		 */
		public String getRegistrationId() {
			return registrationId;
		}

		/**
		 * Set the OAuth2 client registration ID used to authentiate with CredHub.
		 *
		 * @param registrationId the OAuth2 client registration ID
		 */
		public void setRegistrationId(String registrationId) {
			this.registrationId = registrationId;
		}
	}
}
