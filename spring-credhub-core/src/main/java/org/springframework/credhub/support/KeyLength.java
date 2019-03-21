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

import com.fasterxml.jackson.annotation.JsonCreator;

public enum KeyLength {
	LENGTH_2048(2048),
	LENGTH_3072(3072),
	LENGTH_4096(4096);

	private final int length;

	KeyLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	/**
	 * Convert an integer value to its enum value.
	 *
	 * @param length the integer value to convert to enum value
	 * @return the enum value
	 */
	@JsonCreator
	public static KeyLength getTypeByString(int length) {
		for (KeyLength value : KeyLength.values()) {
			if (value.getLength() == length) {
				return value;
			}
		}
		return null;
	}
}
