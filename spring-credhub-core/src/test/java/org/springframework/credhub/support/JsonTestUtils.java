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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.springframework.credhub.support.utils.JsonUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.fail;

public final class JsonTestUtils {
	private JsonTestUtils() {
	}

	public static String toJson(Object object) {
		try {
			ObjectMapper mapper = JsonUtils.buildObjectMapper();
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			fail("Error creating JSON string from object: " + e);
			throw new IllegalStateException(e);
		}
	}

	public static <T> T fromJson(String json, Class<T> type) {
		try {
			ObjectMapper mapper = JsonUtils.buildObjectMapper();
			return mapper.readValue(json, type);
		} catch (IOException e) {
			fail("Error parsing JSON string to object: " + e);
			throw new IllegalStateException(e);
		}
	}

	public static DocumentContext toJsonPath(Object object) {
		Configuration configuration = Configuration.builder()
				.jsonProvider(new JacksonJsonProvider())
				.mappingProvider(new JacksonMappingProvider())
				.build();

		return JsonPath.parse(toJson(object), configuration);
	}
}
