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

package org.springframework.credhub.support;

import java.util.HashMap;
import java.util.Map;

/**
 * A convenience type to alias a {@link Map} that contains JSON credentials.
 *
 * @author Scott Frederick
 */
public class JsonCredential extends HashMap<String, Object> {
	/**
	 * @see HashMap#HashMap()
	 */
	public JsonCredential() {
		super();
	}

	/**
	 * @see HashMap#HashMap(int)
	 */
	public JsonCredential(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * @see HashMap#HashMap(int, float)
	 */
	public JsonCredential(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * @see HashMap#HashMap(Map)
	 */
	public JsonCredential(Map<? extends String, ?> m) {
		super(m);
	}
}
