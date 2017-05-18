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

package org.springframework.credhub.core;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialDetailsData;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Utility methods for creating type references.
 *
 * @author Scott Frederick
 */
public class TypeUtils {
	/**
	 * Create a {@link ParameterizedTypeReference} for {@code credentialType}.
	 *
	 * @param credentialType must not be {@literal null}
	 * @return the {@link ParameterizedTypeReference} for {@code credentialType}
	 */
	static <T> ParameterizedTypeReference<CredentialDetailsData<T>> getDetailsDataReference(
			final Class<T> credentialType) {

		Assert.notNull(credentialType, "Response type must not be null");

		final Type supportType = new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] { credentialType };
			}

			@Override
			public Type getRawType() {
				return CredentialDetailsData.class;
			}

			@Override
			public Type getOwnerType() {
				return CredentialDetailsData.class;
			}
		};

		return new ParameterizedTypeReference<CredentialDetailsData<T>>() {
			@Override
			public Type getType() {
				return supportType;
			}
		};
	}

	/**
	 * Create a {@link ParameterizedTypeReference} for {@code credentialType}.
	 *
	 * @param credentialType must not be {@literal null}
	 * @return the {@link ParameterizedTypeReference} for {@code credentialType}
	 */
	static <T> ParameterizedTypeReference<CredentialDetails<T>> getDetailsReference(
			final Class<T> credentialType) {

		Assert.notNull(credentialType, "Response type must not be null");

		final Type supportType = new ParameterizedType() {
			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] { credentialType };
			}

			@Override
			public Type getRawType() {
				return CredentialDetails.class;
			}

			@Override
			public Type getOwnerType() {
				return CredentialDetails.class;
			}
		};

		return new ParameterizedTypeReference<CredentialDetails<T>>() {
			@Override
			public Type getType() {
				return supportType;
			}
		};
	}
}
