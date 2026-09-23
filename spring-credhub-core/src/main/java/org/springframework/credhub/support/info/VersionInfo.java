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

package org.springframework.credhub.support.info;

/**
 * Version information about a CredHub server, as reported by the server's
 * {@code /version} endpoint. This reflects the server's own release version number, not
 * CredHub's URL-path API versioning ({@code /api/v1/...} vs {@code /api/v2/...}) — for
 * example, a server whose reported version is {@code 2.6.0} still serves many
 * {@code /api/v1/...} endpoints.
 *
 * @author Scott Frederick
 * @see <a href=
 * "https://docs.cloudfoundry.org/api/credhub/version/main/#_get_version">CredHub API
 * docs: Get Version</a>
 */
public class VersionInfo {

	private final String version;

	@SuppressWarnings("unused")
	VersionInfo() {
		this.version = "";
	}

	/**
	 * Create a new {@literal VersionInfo} containing the specified version string.
	 * Intended for internal use. Clients will get {@literal VersionInfo} objects
	 * populated from CredHub responses.
	 * @param version a version string
	 */
	public VersionInfo(String version) {
		this.version = version;
	}

	/**
	 * Get the value of the version string returned from the CredHub server.
	 * @return the version string
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Determine if the CredHub server's reported release version is 1.x.
	 * @return {@code true} if the server's reported release version starts with
	 * {@code 1.}; {@code false} otherwise
	 */
	public boolean isVersion1() {
		return this.version.startsWith("1.");
	}

	/**
	 * Determine if the CredHub server's reported release version is 2.x.
	 * @return {@code true} if the server's reported release version starts with
	 * {@code 2.}; {@code false} otherwise
	 */
	public boolean isVersion2() {
		return this.version.startsWith("2.");
	}

	/**
	 * Determine if the CredHub server's reported release version is 2.0.x.
	 * @return {@code true} if the server's reported release version starts with
	 * {@code 2.0}; {@code false} otherwise
	 */
	public boolean isVersion2_0() {
		return this.version.startsWith("2.0");
	}

	/**
	 * Determine if the CredHub server's reported release version is 2.1.x.
	 * @return {@code true} if the server's reported release version starts with
	 * {@code 2.1}; {@code false} otherwise
	 */
	public boolean isVersion2_1() {
		return this.version.startsWith("2.1");
	}

}
