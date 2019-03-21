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

/**
 * The client-provided name of a credential. The name consists of one or more segments.
 * When the value of each segment are combined the full name of the credential will be of
 * the form {@literal /segment1/segment2/segment3}.
 *
 * Objects of this type are created by clients and included as part of requests.
 *
 * @author Scott Frederick
 */
public class SimpleCredentialName extends CredentialName {
	/**
	 * Create a {@link SimpleCredentialName} from the provided segments.
	 *
	 * @param segments the credential name segments; must not be {@literal null} and must
	 * contain at least one segment
	 */
	public SimpleCredentialName(String... segments) {
		super(segments);
	}

	@Override
	public String toString() {
		return "SimpleCredentialName{"
				+ "segments=" + Arrays.toString(segments)
				+ "}";
	}
}
