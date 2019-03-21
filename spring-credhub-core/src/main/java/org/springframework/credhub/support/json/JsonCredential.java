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

package org.springframework.credhub.support.json;

import java.util.HashMap;
import java.util.Map;

/**
 * A JSON credential consists of one or more fields in a JSON document. The JSON document is represented as a
 * {@literal Map} object, which will be converted to a JSON document before sending to CredHub.
 *
 * @author Scott Frederick
 */
@SuppressWarnings("javadoc")
public class JsonCredential extends HashMap<String, Object> {
	/**
	 * @see HashMap#HashMap()
	 */
	public JsonCredential() {
		super();
	}

	/**
	 * @see HashMap#HashMap(int)
	 *
	 * @param  initialCapacity the initial capacity
	 */
	public JsonCredential(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * @see HashMap#HashMap(int, float)
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor      the load factor
	 */
	public JsonCredential(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * @see HashMap#HashMap(Map)
	 *
	 * @param m the map whose mappings are to be placed in this map
	 */
	public JsonCredential(Map<? extends String, ?> m) {
		super(m);
	}
}
