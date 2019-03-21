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

package org.springframework.credhub.support.ssh;

import org.springframework.credhub.support.KeyLength;
import org.springframework.credhub.support.KeyParameters;
import org.springframework.util.Assert;

/**
 * Parameters for generating a new SSH credential. All parameters are optional; if not specified,
 * CredHub-provided defaults will be used.
 *
 * @author Scott Frederick
 */
public class SshParameters extends KeyParameters {
	private final String sshComment;

	/**
	 * Create a {@link SshParameters} using defaults for all parameter values.
	 */
	private SshParameters() {
		super();
		sshComment = null;
	}

	/**
	 * Create a {@link SshParameters} using the specified values.
	 *
	 * @param sshComment comment for the generated SSH key; must not be {@literal null}
	 */
	public SshParameters(String sshComment) {
		super();
		Assert.notNull(sshComment, "sshComment must not be null");
		this.sshComment = sshComment;
	}

	/**
	 * Create a {@link SshParameters} using the specified values.
	 *
	 * @param keyLength length of generated SSH key; must not be {@literal null}
	 */
	public SshParameters(KeyLength keyLength) {
		super(keyLength);
		Assert.notNull(keyLength, "keyLength must not be null");
		this.sshComment = null;
	}

	/**
	 * Create a {@link SshParameters} using the specified values.
	 *
	 * @param keyLength length of generated SSH key; must not be {@literal null}
	 * @param sshComment comment for the generated SSH key; must not be {@literal null}
	 */
	public SshParameters(KeyLength keyLength, String sshComment) {
		super(keyLength);
		Assert.notNull(keyLength, "keyLength must not be null");
		Assert.notNull(sshComment, "sshComment must not be null");
		this.sshComment = sshComment;
	}

	/**
	 * Get the value of the ssh comment parameter.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public String getSshComment() {
		return sshComment;
	}
}
