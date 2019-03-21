/*
 *
 * Copyright 2013-2017 the original author or authors.
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
 *
 */

package org.springframework.credhub.support.permissions;

/**
 * The types of entities that can be authorized to perform operations on CredHub credentials.
 *
 * @author Scott Frederick
 */
public enum ActorType {
	/**
	 * A Cloud Foundry application entity
	 */
	APP("mtls-app"),

	/**
	 * A UAA user entity, as can be used with a password grant
	 */
	USER("uaa-user"),

	/**
	 * A UAA client entity, as can be used with a client credentials grant
	 */
	OAUTH_CLIENT("uaa-client");

	private final String type;

	ActorType(String type) {
		this.type = type;
	}

	/**
	 * Get the entity type.
	 *
	 * @return the entity type
	 */
	public String getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return type;
	}
}
