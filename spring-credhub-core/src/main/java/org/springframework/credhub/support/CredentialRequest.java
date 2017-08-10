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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.util.Assert;

/**
 * The details of a request to write a new or update an existing credential in CredHub.
 *
 * @author Scott Frederick
 */
public class CredentialRequest<T> extends CredHubRequest {
	private T value;
	private List<AdditionalPermission> additionalPermissions;

	/**
	 * Initialize a {@link CredentialRequest}.
	 *
	 * @param type the credential implementation type
	 */
	protected CredentialRequest(CredentialType type) {
		this.credentialType = type;
		additionalPermissions = new ArrayList<AdditionalPermission>();
	}

	/**
	 * Get the value of the credential.
	 *
	 * @return the value of the credential
	 */
	public T getValue() {
		return this.value;
	}

	protected void setValue(T value) {
		this.value = value;
	}

	/**
	 * Get the set of {@link AdditionalPermission} to assign to the credential.
	 *
	 * @return the set of {@link AdditionalPermission}
	 */
	public List<AdditionalPermission> getAdditionalPermissions() {
		return this.additionalPermissions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CredentialRequest))
			return false;

		CredentialRequest that = (CredentialRequest) o;

		if (overwrite != that.overwrite)
			return false;
		if (!name.equals(that.name))
			return false;
		if (credentialType != that.credentialType)
			return false;
		if (!value.equals(that.value))
			return false;
		return additionalPermissions.equals(that.additionalPermissions);
	}

	@Override
	public int hashCode() {
		int result = (overwrite ? 1 : 0);
		result = 31 * result + name.hashCode();
		result = 31 * result + credentialType.hashCode();
		result = 31 * result + value.hashCode();
		result = 31 * result + additionalPermissions.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "CredentialRequest{"
				+ "overwrite=" + overwrite
				+ ", name=" + name
				+ ", credentialType=" + credentialType
				+ ", value=" + value
				+ ", additionalPermissions=" + additionalPermissions
				+ '}';
	}

	/**
	 * A builder that provides a fluent API for constructing {@link CredentialRequest}s.
	 */
	@SuppressWarnings("unchecked")
	protected static abstract class CredentialRequestBuilder<T, R extends CredentialRequest<T>, B extends CredentialRequestBuilder<T, R, B>> {
		private final B thisObj;
		protected final R targetObj;

		/**
		 * Create a {@link CredentialRequestBuilder}. Intended for internal use.
		 */
		protected CredentialRequestBuilder() {
			this.thisObj = createBuilder();
			this.targetObj = createTarget();
		}

		/**
		 * Provide the concrete object to build.
		 *
		 * @return the target object
		 */
		protected abstract R createTarget();

		/**
		 * Provide the concrete builder.
		 *
		 * @return the builder
		 */
		protected abstract B createBuilder();

		/**
		 * Set the value of a credential.
		 *
		 * @param value the credential value; must not be {@literal null}
		 * @return the builder
		 */
		public abstract B value(T value);

		/**
		 * Set the {@link CredentialName} for the credential.
		 *
		 * @param name the credential name; must not be {@literal null}
		 * @return the builder
		 */
		public B name(CredentialName name) {
			Assert.notNull(name, "name must not be null");
			targetObj.setName(name);
			return thisObj;
		}

		/**
		 * Sets a {@literal boolean} value indicating whether CredHub should create a new
		 * credential or update and existing credential.
		 *
		 * @param overwrite {@literal false} to create a new credential, or
		 * {@literal true} to update and existing credential
		 * @return the builder 
		 */
		public B overwrite(boolean overwrite) {
			targetObj.setOverwrite(overwrite);
			return thisObj;
		}

		/**
		 * Add an {@link AdditionalPermission} to the permissions that will be assigned to the
		 * credential.
		 *
		 * @param permission an {@link AdditionalPermission} to assign to the
		 * credential
		 * @return the builder
		 */
		public B additionalPermission(AdditionalPermission permission) {
			targetObj.getAdditionalPermissions().add(permission);
			return thisObj;
		}

		/**
		 * Add a collection of {@link AdditionalPermission}s to the controls that will be
		 * assigned to the credential.
		 *
		 * @param permissions an collection of {@link AdditionalPermission}s to
		 * assign to the credential
		 * @return the builder
		 */
		public B additionalPermissions(Collection<? extends AdditionalPermission> permissions) {
			targetObj.getAdditionalPermissions().addAll(permissions);
			return thisObj;
		}

		/**
		 * Add a collection of {@link AdditionalPermission}s to the controls that will be
		 * assigned to the credential.
		 *
		 * @param permissions an collection of {@link AdditionalPermission}s to
		 * assign to the credential
		 * @return the builder
		 */
		public B additionalPermissions(AdditionalPermission... permissions) {
			targetObj.getAdditionalPermissions().addAll(Arrays.asList(permissions));
			return thisObj;
		}

		/**
		 * Create a {@link CredentialRequest} from the provided values.
		 *
		 * @return a {@link CredentialRequest}
		 */
		public R build() {
			return targetObj;
		}
	}

}
