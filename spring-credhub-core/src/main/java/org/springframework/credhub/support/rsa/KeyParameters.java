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

package org.springframework.credhub.support.rsa;

import org.springframework.credhub.support.KeyLength;

public class KeyParameters {
	protected final KeyLength keyLength;

	protected KeyParameters() {
		this.keyLength = null;
	}

	protected KeyParameters(KeyLength keyLength) {
		this.keyLength = keyLength;
	}

	/**
	 * Get the value of the key length parameter.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public Integer getKeyLength() {
		return keyLength == null ? null : keyLength.getLength();
	}
}
