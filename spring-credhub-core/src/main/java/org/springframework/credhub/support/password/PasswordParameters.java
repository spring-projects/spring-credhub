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

package org.springframework.credhub.support.password;

/**
 * Parameters for generating a new password credential. All parameters are optional; if not specified,
 * CredHub-provided defaults will be used.
 *
 * @author Scott Frederick
 */
public class PasswordParameters {
	private final Integer length;
	private final Boolean excludeUpper;
	private final Boolean excludeLower;
	private final Boolean excludeNumber;
	private final Boolean includeSpecial;

	/**
	 * Create a {@link PasswordParameters} using defaults for all parameter values.
	 */
	public PasswordParameters() {
		length = null;
		excludeUpper = null;
		excludeLower = null;
		excludeNumber = null;
		includeSpecial = null;
	}

	/**
	 * Create a {@link PasswordParameters} using the specified values.
	 *
	 * @param length length of generated password value
	 * @param excludeUpper {@literal true} to exclude upper case alpha characters from generated credential value
	 * @param excludeLower {@literal true} to exclude lower case alpha characters from generated credential value
	 * @param excludeNumber {@literal true} to exclude numeric characters from generated credential value
	 * @param includeSpecial {@literal true} to include non-alphanumeric characters in generated credential value
	 */
	public PasswordParameters(int length, boolean excludeUpper, boolean excludeLower,
							  boolean excludeNumber, boolean includeSpecial) {
		this.length = length;
		this.excludeUpper = excludeUpper;
		this.excludeLower = excludeLower;
		this.excludeNumber = excludeNumber;
		this.includeSpecial = includeSpecial;
	}

	/**
	 * Get the value of the length parameter.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * Get the value of the exclude upper case characters parameter.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public Boolean getExcludeUpper() {
		return excludeUpper;
	}

	/**
	 * Get the value of the exclude lower case characters parameter.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public Boolean getExcludeLower() {
		return excludeLower;
	}

	/**
	 * Get the value of the exclude numeric characters parameter.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public Boolean getExcludeNumber() {
		return excludeNumber;
	}

	/**
	 * Get the value of the include non-alphanumeric characters parameter.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public Boolean getIncludeSpecial() {
		return includeSpecial;
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link PasswordParameters}.
	 *
	 * @return a builder
	 */
	public static PasswordParametersBuilder builder() {
		return new PasswordParametersBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link PasswordParametersBuilder}s.
	 */
	public static class PasswordParametersBuilder {
		private Integer length = null;
		private Boolean excludeUpper = null;
		private Boolean excludeLower = null;
		private Boolean excludeNumber = null;
		private Boolean includeSpecial = null;

		/**
		 * Set the value of the password length parameter.
		 *
		 * @param length the parameter value
		 * @return the builder
		 */
		public PasswordParametersBuilder length(int length) {
			this.length = length;
			return this;
		}

		/**
		 * Set the value of the exclude upper case characters parameter.
		 *
		 * @param exclude {@literal true} to exclude upper case alpha characters from generated credential value
		 * @return the builder
		 */
		public PasswordParametersBuilder excludeUpper(boolean exclude) {
			this.excludeUpper = exclude;
			return this;
		}

		/**
		 * Set the value of the exclude lower case characters parameter.
		 *
		 * @param exclude {@literal true} to exclude lower case alpha characters from generated credential value
		 * @return the builder
		 */
		public PasswordParametersBuilder excludeLower(boolean exclude) {
			this.excludeLower = exclude;
			return this;
		}

		/**
		 * Set the value of the exclude numeric characters parameter.
		 *
		 * @param exclude {@literal true} to exclude numeric characters from generated credential value
		 * @return the builder
		 */
		public PasswordParametersBuilder excludeNumber(boolean exclude) {
			this.excludeNumber = exclude;
			return this;
		}

		/**
		 * Set the value of the include special characters parameter.
		 *
		 * @param include {@literal true} to include non-alphanumeric characters in generated credential value
		 * @return the builder
		 */
		public PasswordParametersBuilder includeSpecial(boolean include) {
			this.includeSpecial = include;
			return this;
		}

		/**
		 * Create a {@link PasswordParameters} from the provided values.
		 *
		 * @return the created {@link PasswordParameters}
		 */
		public PasswordParameters build() {
			return new PasswordParameters(length, excludeUpper, excludeLower, excludeNumber, includeSpecial);
		}
	}
}
