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

package org.springframework.credhub.support.value;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.credhub.support.StringCredential;

/**
 * A password credential consists of a single string value.
 *
 * @author Scott Frederick
 */
public class ValueCredential extends StringCredential {
	/**
	 * Create a {@link ValueCredential} containing the specified string value.
	 *
	 * @param value the value
	 */
	public ValueCredential(String value) {
		super(value);
	}

	/**
	 * Get the credential value.
	 *
	 * @return the credential value
	 */
	@JsonValue
	public String getValue() {
		return value;
	}
}
