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

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The client-provided name of a credential stored in CredHub. Credential names are
 * constructed of segments separated by the "/" character, like Unix paths.
 *
 * @author Scott Frederick
 */
public class CredentialName {
	@JsonIgnore
	protected final String[] segments;

	/**
	 * Create a name from the provided value. The name must consist of segments
	 * separated by the "/" character.
	 *
	 * @param name the credential name; must not be {@literal null}
	 */
	CredentialName(String name) {
		Assert.notNull("name", "name must not be null");

		String[] split = name.split("/");

		Assert.isTrue(split.length > 0, "name must include at least one segment separated by '/'");

		if (split[0].length() == 0) {
			// name contains a leading "/"
			this.segments = Arrays.copyOfRange(split, 1, split.length);
		} else {
			this.segments = split;
		}
	}

	/**
	 * Create a name from the provided segments.
	 *
	 * @param segments the list of name segments; must not be {@literal null}
	 */
	CredentialName(String... segments) {
		Assert.notNull(segments, "segments must not be null");
		this.segments = segments;
	}

	/**
	 * Builds a name from the provided segments.
	 *
	 * @return the credential name
	 */
	@JsonInclude
	public String getName() {
		if (segments.length == 1) {
			return segments[0];
		} else {
			return "/" + StringUtils.arrayToDelimitedString(segments, "/");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CredentialName))
			return false;

		CredentialName that = (CredentialName) o;

		return Arrays.equals(segments, that.segments);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(segments);
	}
}
