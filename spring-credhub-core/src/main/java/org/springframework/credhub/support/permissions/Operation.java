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
 * The set of operations that are allowed on a credential.
 *
 * @author Scott Frederick 
 */
public enum Operation {
	/**
	 * Allows the value of a credential to be read.
	 */
	READ("read"),

	/**
	 * Allows the value of a credential to be updated.
	 */
	WRITE("write"),

	/**
	 * Allows a credential to be deleted.
	 */
	DELETE("delete"),

	/**
	 * Allows the permissions of a credential to be read.
	 */
	READ_ACL("read_acl"),

	/**
	 * Allows the permissions of a credential to be updated.
	 */
	WRITE_ACL("write_acl");

	private final String operation;

	Operation(String operation) {
		this.operation = operation;
	}

	/**
	 * Get the value of the operation.
	 *
	 * @return the value of the operation.
	 */
	public String operation() {
		return operation;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return operation;
	}
}
