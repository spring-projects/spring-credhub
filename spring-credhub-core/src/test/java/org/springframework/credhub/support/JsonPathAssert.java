/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.support;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.PathNotFoundException;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;

public class JsonPathAssert extends AbstractAssert<JsonPathAssert, DocumentContext> {
	public JsonPathAssert(DocumentContext actual) {
		super(actual, JsonPathAssert.class);
	}

	public static JsonPathAssert assertThat(DocumentContext jsonPathDocument) {
		return new JsonPathAssert(jsonPathDocument);
	}

	public JsonPathAssert hasNoPath(String jsonPath) {
		try {
			Object value = actual.read(jsonPath);
			failWithMessage("The path '" + jsonPath + "' was not expected but evaluated to " + value);
			return null;
		} catch (JsonPathException e) {
			return this;
		}
	}

	public AbstractObjectAssert<?, Object> hasPath(String path) {
		try {
			return Assertions.assertThat(actual.read(path, Object.class));
		} catch (PathNotFoundException e) {
			failWithMessage("The JSON " + actual.jsonString() + " does not contain the path '" + path + "'");
			return null;
		}
	}
}
