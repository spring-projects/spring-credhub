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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.credhub.core.permission.CredHubPermissionOperations;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.util.Assert;

/**
 * Fields common to all types of CredHub requests.
 *
 * @param <T> the type of CredHub credential
 * @author Scott Frederick
 */
@SuppressWarnings("WeakerAccess")
public class CredHubRequest<T> {

	protected Boolean overwrite;

	protected WriteMode mode;

	protected CredentialName name;

	protected CredentialType credentialType;

	protected List<Permission> additionalPermissions;

	protected T details;

	public CredHubRequest() {
		this.additionalPermissions = new ArrayList<>();
	}

	/**
	 * Get the value of the {@literal boolean} flag indicating whether the CredHub should
	 * create a new credential or update an existing credential.
	 * @return the {@literal boolean} overwrite value
	 * @deprecated as of CredHub 1.6, use {@link #mode}
	 */
	@Deprecated
	public Boolean isOverwrite() {
		return this.overwrite;
	}

	void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * Get the value of the write mode indicator.
	 * @return the write mode
	 */
	public WriteMode getMode() {
		return this.mode;
	}

	void setMode(WriteMode mode) {
		this.mode = mode;
	}

	/**
	 * Get the {@link CredentialName} of the credential.
	 * @return the name of the credential
	 */
	@JsonInclude
	public String getName() {
		return (this.name == null) ? null : this.name.getName();
	}

	void setName(CredentialName name) {
		this.name = name;
	}

	/**
	 * Get the {@link CredentialType} of the credential.
	 * @return the type of the credential
	 */
	public String getType() {
		return this.credentialType.getValueType();
	}

	void setType(CredentialType credentialType) {
		this.credentialType = credentialType;
	}

	void setDetails(T details) {
		this.details = details;
	}

	/**
	 * Get the set of {@link Permission} to assign to the credential.
	 * @return the set of {@link Permission}
	 */
	public List<Permission> getAdditionalPermissions() {
		return this.additionalPermissions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CredHubRequest)) {
			return false;
		}

		CredHubRequest that = (CredHubRequest) o;

		if (this.overwrite != that.overwrite) {
			return false;
		}
		if ((this.name != null) ? !this.name.equals(that.name) : (that.name != null)) {
			return false;
		}
		if (this.credentialType != that.credentialType) {
			return false;
		}
		if ((this.additionalPermissions != null) ? !this.additionalPermissions.equals(that.additionalPermissions)
				: (that.additionalPermissions == null)) {
			return false;
		}
		if ((this.details != null) ? !this.details.equals(that.details) : (that.details != null)) {
			return false;
		}
		if ((this.mode != null) ? !this.mode.equals(that.mode) : (that.mode != null)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.overwrite, this.name, this.credentialType, this.additionalPermissions, this.details,
				this.mode);
	}

	@Override
	public String toString() {
		return "CredHubRequest{" + "overwrite=" + this.overwrite + ", name=" + this.name + ", credentialType="
				+ this.credentialType + ", additionalPermissions=" + this.additionalPermissions + ", details="
				+ this.details + '}';
	}

	/**
	 * A builder that provides a fluent API for constructing {@link CredHubRequest}s.
	 *
	 * @param <T> the type of CredHub credential
	 * @param <R> the type of the concrete {@link CredHubRequest}
	 * @param <B> the type of the concrete builder
	 */
	protected abstract static class CredHubRequestBuilder<T, R extends CredHubRequest<T>, B extends CredHubRequestBuilder<T, R, B>> {

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
		 * @return the target object
		 */
		protected abstract R createTarget();

		/**
		 * Provide the concrete builder.
		 * @return the builder
		 */
		protected abstract B createBuilder();

		/**
		 * Set the {@link CredentialName} for the credential.
		 * @param name the credential name; must not be {@literal null}
		 * @return the builder
		 */
		public B name(CredentialName name) {
			Assert.notNull(name, "name must not be null");
			this.targetObj.setName(name);
			return this.thisObj;
		}

		/**
		 * Sets a {@literal boolean} value indicating whether CredHub should create a new
		 * credential or update and existing credential.
		 * @param overwrite {@literal false} to create a new credential, or
		 * {@literal true} to update and existing credential
		 * @return the builder
		 * @deprecated as of CredHub 1.6, use {@link #mode(WriteMode)}
		 */
		@Deprecated
		public B overwrite(boolean overwrite) {
			this.targetObj.setOverwrite(overwrite);
			return this.thisObj;
		}

		/**
		 * Sets a value indicating the action CredHub should take when a credential being
		 * written or generated already exists.
		 *
		 * As of CredHub 2.0, this value must not be set on write requests (write requests
		 * always overwrite the credential that already exists) but may be set on generate
		 * requests.
		 * @param mode the {@link WriteMode} to use when a credential exists
		 * @return the builder
		 */
		public B mode(WriteMode mode) {
			this.targetObj.setMode(mode);
			return this.thisObj;
		}

		/**
		 * Add an {@link Permission} to the permissions that will be assigned to the
		 * credential.
		 * @param permission a {@link Permission} to assign to the credential
		 * @return the builder
		 * @deprecated as of CredHub 2.0, use {@link CredHubPermissionOperations} to
		 * assign permissions to a credential after it is created
		 */
		@Deprecated
		public B permission(Permission permission) {
			this.targetObj.getAdditionalPermissions().add(permission);
			return this.thisObj;
		}

		/**
		 * Add a collection of {@link Permission}s to the controls that will be assigned
		 * to the credential.
		 * @param permissions a collection of {@link Permission}s to assign to the
		 * credential
		 * @return the builder
		 * @deprecated as of CredHub 2.0, use {@link CredHubPermissionOperations} to
		 * assign permissions to a credential after it is created
		 */
		@Deprecated
		public B permissions(Collection<? extends Permission> permissions) {
			this.targetObj.getAdditionalPermissions().addAll(permissions);
			return this.thisObj;
		}

		/**
		 * Add a collection of {@link Permission}s to the controls that will be assigned
		 * to the credential.
		 * @param permissions a collection of {@link Permission}s to assign to the
		 * credential
		 * @return the builder
		 * @deprecated as of CredHub 2.0, use {@link CredHubPermissionOperations} to
		 * assign permissions to a credential after it is created
		 */
		@Deprecated
		public B permissions(Permission... permissions) {
			this.targetObj.getAdditionalPermissions().addAll(Arrays.asList(permissions));
			return this.thisObj;
		}

		/**
		 * Create a {@link CredHubRequest} from the provided values.
		 * @return a {@link CredHubRequest}
		 */
		public R build() {
			return this.targetObj;
		}

	}

}
