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

package org.springframework.credhub.support;

/**
 * Details of a {@literal user} credential.
 *
 * @author Scott Frederick
 */
public class UserCredential {
	private final String username;
	private final String password;

	/**
	 * Create an empty {@link UserCredential}. Intended to be used internally for deserialization of responses.
	 */
	public UserCredential() {
		username = null;
		password = null;
	}

	/**
	 * Create a {@link UserCredential} with the specified values.
	 *
	 * @param username the name of the user
	 * @param password the password of the user
	 */
	public UserCredential(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Get the user name.
	 *
	 * @return the user name
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Get the user password.
	 *
	 * @return the user password
	 */
	public String getPassword() {
		return password;
	}
}
