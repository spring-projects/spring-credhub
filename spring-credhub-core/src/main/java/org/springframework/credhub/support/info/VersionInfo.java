/*
 * Copyright 2016-2020 the original author or authors.
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
 * Version information about a CredHub server.
 *
 * @author Scott Frederick
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
	 * Determine if the CredHub server implements the v1 API.
	 * @return {@code true} if the server implements the CredHub v1 API; {@code false}
	 * otherwise
	 */
	public boolean isVersion1() {
		return this.version.startsWith("1.");
	}

	/**
	 * Determine if the CredHub server implements the v2 API.
	 * @return {@code true} if the server implements the CredHub v2 API; {@code false}
	 * otherwise
	 */
	public boolean isVersion2() {
		return this.version.startsWith("2.");
	}

	/**
	 * Determine if the CredHub server implements the v2.0 API.
	 * @return {@code true} if the server implements the CredHub v2.0 API; {@code false}
	 * otherwise
	 */
	public boolean isVersion2_0() {
		return this.version.startsWith("2.0");
	}

	/**
	 * Determine if the CredHub server implements the v2.1 API.
	 * @return {@code true} if the server implements the CredHub v2.1 API; {@code false}
	 * otherwise
	 */
	public boolean isVersion2_1() {
		return this.version.startsWith("2.1");
	}

}
