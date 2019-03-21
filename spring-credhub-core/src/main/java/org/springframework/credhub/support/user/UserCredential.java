/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.support.user;

import org.springframework.util.Assert;

/**
 * A user credential consists of an optional username and a password. When retrieved, a user credential
 * will contain a hash of the password.
 *
 * @author Scott Frederick
 */
public class UserCredential {
	private final String username;
	private final String password;
	private final String passwordHash;

	/**
	 * Create an empty {@link UserCredential}. Intended to be used internally for deserialization of responses.
	 */
	private UserCredential() {
		username = null;
		password = null;
		passwordHash = null;
	}

	/**
	 * Create a {@link UserCredential} with the specified values.
	 *
	 * @param username the name of the user; must not be {@literal null}
	 * @param password the password of the user; must not be {@literal null}
	 */
	public UserCredential(String username, String password) {
		Assert.notNull(username, "username must not be null");
		Assert.notNull(password, "password must not be null");
		this.username = username;
		this.password = password;
		this.passwordHash = null;
	}

	/**
	 * Create a {@link UserCredential} with the specified password value.
	 *
	 * @param password the password of the user; must not be {@literal null}
	 */
	public UserCredential(String password) {
		Assert.notNull(password, "password must not be null");
		this.username = null;
		this.password = password;
		this.passwordHash = null;
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
	
	/**
	 * Get the SHA-512 hash of the user password.
	 *
	 * @return the hash of the user password
	 */
	public String getPasswordHash() {
		return passwordHash;
	}
}
