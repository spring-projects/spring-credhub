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

package org.springframework.credhub.support;

/**
 * The acceptable values for the {@code mode} parameter on a set or generate request,
 * indicating the action CredHub should take when the credential being set or generated
 * already exists.
 *
 * @author Scott Frederick
 */
public enum WriteMode {

	/**
	 * Indicates that CredHub should not replace the value of a credential if the
	 * credential exists.
	 */
	NO_OVERWRITE("no-overwrite"),

	/**
	 * Indicates that CredHub should replace any existing credential value with a new
	 * value.
	 */
	OVERWRITE("overwrite"),

	/**
	 * Indicates that CredHub should replace any existing credential value with a new
	 * value only if generation parameters are different from the original generation
	 * parameters.
	 */
	CONVERGE("converge");

	private final String mode;

	WriteMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Get the {@code mode} value as a {@code String}.
	 * @return the mode value
	 */
	public String getMode() {
		return this.mode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.mode;
	}

}
