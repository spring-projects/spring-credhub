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

package org.springframework.credhub.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.credhub.support.permissions.CredentialPermission;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Fields common to all types of CredHub requests.
 *
 * @author Scott Frederick
 */
public class CredHubRequest<T> {
	protected boolean overwrite;
	protected CredentialName name;
	protected CredentialType credentialType;
	protected List<CredentialPermission> additionalPermissions;
	protected T details;

	public CredHubRequest() {
		additionalPermissions = new ArrayList<CredentialPermission>();
	}

	/**
	 * Get the value of the {@literal boolean} flag indicating whether the CredHub
	 * should create a new credential or update an existing credential.
	 *
	 * @return the {@literal boolean} overwrite value
	 */
	public boolean isOverwrite() {
		return this.overwrite;
	}

	void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * Get the {@link CredentialName} of the credential.
	 *
	 * @return the name of the credential
	 */
	@JsonInclude
	public String getName() {
		return name == null ? null : name.getName();
	}

	void setName(CredentialName name) {
		this.name = name;
	}

	/**
	 * Get the {@link CredentialType} of the credential.
	 *
 	 * @return the type of the credential
	 */
	public String getType() {
		return credentialType.getValueType();
	}

	void setType(CredentialType credentialType) {
		this.credentialType = credentialType;
	}

	void setDetails(T details) {
		this.details = details;
	}

	/**
	 * Get the set of {@link CredentialPermission} to assign to the credential.
	 *
	 * @return the set of {@link CredentialPermission}
	 */
	public List<CredentialPermission> getAdditionalPermissions() {
		return this.additionalPermissions;
	}

	@Override
	public String toString() {
		return "CredHubRequest{" +
				"overwrite=" + overwrite +
				", name=" + name +
				", credentialType=" + credentialType +
				", additionalPermissions=" + additionalPermissions +
				", details=" + details +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CredHubRequest)) return false;

		CredHubRequest that = (CredHubRequest) o;

		if (overwrite != that.overwrite) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (credentialType != that.credentialType) return false;
		if (additionalPermissions != null ?
				additionalPermissions.equals(that.additionalPermissions) : that.additionalPermissions == null) return false;
		if (details != null ? !details.equals(that.details) : that.details != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (overwrite ? 1 : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (credentialType != null ? credentialType.hashCode() : 0);
		result = 31 * result + (additionalPermissions != null ? additionalPermissions.hashCode() : 0);
		result = 31 * result + (details != null ? details.hashCode() : 0);
		return result;
	}

	/**
	 * A builder that provides a fluent API for constructing {@link CredHubRequest}s.
	 */
	@SuppressWarnings("unchecked")
	protected static abstract class CredHubRequestBuilder<T, R extends CredHubRequest<T>, B extends CredHubRequestBuilder<T, R, B>> {
		private final B thisObj;
		protected final R targetObj;

		/**
		 * Create a {@link CredHubRequestBuilder}. Intended for internal use.
		 */
		protected CredHubRequestBuilder() {
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
		 * Add an {@link CredentialPermission} to the permissions that will be assigned to the
		 * credential.
		 *
		 * @param permission a {@link CredentialPermission} to assign to the
		 * credential
		 * @return the builder
		 */
		public B permission(CredentialPermission permission) {
			targetObj.getAdditionalPermissions().add(permission);
			return thisObj;
		}

		/**
		 * Add a collection of {@link CredentialPermission}s to the controls that will be
		 * assigned to the credential.
		 *
		 * @param permissions a collection of {@link CredentialPermission}s to
		 * assign to the credential
		 * @return the builder
		 */
		public B permissions(Collection<? extends CredentialPermission> permissions) {
			targetObj.getAdditionalPermissions().addAll(permissions);
			return thisObj;
		}

		/**
		 * Add a collection of {@link CredentialPermission}s to the controls that will be
		 * assigned to the credential.
		 *
		 * @param permissions a collection of {@link CredentialPermission}s to
		 * assign to the credential
		 * @return the builder
		 */
		public B permissions(CredentialPermission... permissions) {
			targetObj.getAdditionalPermissions().addAll(Arrays.asList(permissions));
			return thisObj;
		}

		/**
		 * Create a {@link CredHubRequest} from the provided values.
		 *
		 * @return a {@link CredHubRequest}
		 */
		public R build() {
			return targetObj;
		}
	}

}
