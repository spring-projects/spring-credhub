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

package org.springframework.credhub.support.rsa;

import org.springframework.credhub.support.KeyLength;
import org.springframework.credhub.support.KeyParameters;
import org.springframework.util.Assert;

/**
 * Parameters for generating a new RSA credential. All parameters are optional; if not specified,
 * CredHub-provided defaults will be used.
 *
 * @author Scott Frederick
 */
public class RsaParameters extends KeyParameters {
	/**
	 * Create a {@link RsaParameters} using defaults for all parameter values.
	 */
	private RsaParameters() {
		super();
	}

	/**
	 * Create a {@link RsaParameters} using the specified values.
	 *
	 * @param keyLength length of generated RSA key; must not be {@literal null}
	 */
	public RsaParameters(KeyLength keyLength) {
		super(keyLength);
		Assert.notNull(keyLength, "keyLength must not be null");
	}
}
