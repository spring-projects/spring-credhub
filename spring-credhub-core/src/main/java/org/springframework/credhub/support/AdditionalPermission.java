/*
 *
 *  * Copyright 2013-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.credhub.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Permissions applied to a credential in CredHub. If provided when a
 * credential is written, these values will control what actors can access update
 * or retrieve the credential.
 *
 * Objects of this type are constructed by the application and passed
 * as part of a {@link CredentialRequest}.
 *
 * @author Scott Frederick
 */
public class AdditionalPermission {
	private static final String APP_ACTOR_PREFIX = "mtls-app:";

	private String actor;
	private List<Operation> operations;

	/**
	 * Create a set of permissions. Intended to be used internally for testing.
	 * Clients should use {@link #builder()} to construct instances of this class.
	 *
	 * @param actor the ID of the entity that will be allowed to access the credential
	 * @param operations the operations that the actor will be allowed to perform on the
	 * credential
	 */
	AdditionalPermission(String actor, List<Operation> operations) {
		this.actor = actor;
		this.operations = operations;
	}

	/**
	 * Get the ID of the entity that will be allowed to access the credential.
	 *
	 * @return the ID
	 */
	public String getActor() {
		return this.actor;
	}

	/**
	 * Get the set of operations that the actor will be allowed to perform on
	 * the credential.
	 *
	 * @return the operations
	 */
	public List<String> getOperations() {
		List<String> operationValues = new ArrayList<String>(operations.size());
		for (Operation operation : operations) {
			operationValues.add(operation.operation());
		}
		return operationValues;
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link AdditionalPermission}.
	 *
	 * @return a builder
	 */
	public static AdditionalPermissionBuilder builder() {
		return new AdditionalPermissionBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof AdditionalPermission))
			return false;

		AdditionalPermission that = (AdditionalPermission) o;

		if (actor != null ? !actor.equals(that.actor) : that.actor != null)
			return false;
		return operations != null ? operations.equals(that.operations)
				: that.operations == null;
	}

	@Override
	public int hashCode() {
		int result = actor != null ? actor.hashCode() : 0;
		result = 31 * result + (operations != null ? operations.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "AdditionalPermission{"
				+ "actor='" + actor + '\''
				+ ", operations=" + operations
				+ '}';
	}

	/**
	 * A builder that provides a fluent API for constructing {@link AdditionalPermission}
	 * instances.
	 */
	public static class AdditionalPermissionBuilder {
		private String actor;
		private ArrayList<Operation> operations;

		AdditionalPermissionBuilder() {
		}

		/**
		 * Set the ID of an application that will be allowed to access a credential.
		 * This will often be a Cloud Foundry application GUID.
		 *
		 * @param appId application ID
		 * @return the builder
		 */
		public AdditionalPermissionBuilder app(String appId) {
			this.actor = APP_ACTOR_PREFIX + appId;
			return this;
		}

		/**
		 * Set the name of an actor that will be allowed to access the credential.
		 *
		 * @param actor actor name
		 * @return the builder
		 */
		public AdditionalPermissionBuilder actor(String actor) {
			this.actor = actor;
			return this;
		}

		/**
		 * Set an {@link Operation} that the actor will be allowed to perform on
		 * the credential. Multiple operations can be provided with consecutive calls to
		 * this method.
		 *
		 * @param operation the {@link Operation}
		 * @return the builder
		 */
		public AdditionalPermissionBuilder operation(Operation operation) {
			initOperations();
			this.operations.add(operation);
			return this;
		}

		/**
		 * Specify a set of {@link Operation}s that the actor will be allowed to perform
		 * on the credential.
		 *
		 * @param operations the {@link Operation}s
		 * @return the builder
		 */
		public AdditionalPermissionBuilder operations(Collection<? extends Operation> operations) {
			initOperations();
			this.operations.addAll(operations);
			return this;
		}

		private void initOperations() {
			if (this.operations == null) this.operations = new ArrayList<Operation>();
		}

		/**
		 * Construct an {@link AdditionalPermission} with the provided values.
		 *
		 * @return an {@link AdditionalPermission}
		 */
		public AdditionalPermission build() {
			List<Operation> operations;
			switch (this.operations == null ? 0 : this.operations.size()) {
				case 0:
					operations = java.util.Collections.emptyList();
					break;
				case 1:
					operations = java.util.Collections.singletonList(this.operations.get(0));
					break;
				default:
					operations = java.util.Collections.unmodifiableList(new ArrayList<Operation>(this.operations));
			}

			return new AdditionalPermission(actor, operations);
		}
	}

	/**
	 * The set of operations that are allowed on a credential.
	 */
	public enum Operation {
		READ("read"),
		WRITE("write");

		private final String operation;

		Operation(String operation) {
			this.operation = operation;
		}

		public String operation() {
			return operation;
		}
	}
}
