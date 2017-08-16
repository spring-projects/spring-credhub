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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;

import org.springframework.credhub.support.ParametersRequest.ParametersRequestBuilder;
import org.springframework.credhub.support.utils.JsonUtils;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasNoJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.isJson;

public abstract class ParametersRequestUnitTestsBase {
	private ObjectMapper mapper;

	@Before
	public void setUpGenerateCredentialRequestUnitTests() {
		mapper = JsonUtils.buildObjectMapper();
	}

	protected <T extends ParametersRequestBuilder> String serializeToJson(T requestBuilder)
			throws JsonProcessingException {
		String jsonValue = mapper.writeValueAsString(requestBuilder.build());
		assertThat(jsonValue, isJson());
		return jsonValue;
	}

	protected void assertCommonRequestFields(String jsonValue, boolean overwrite, String name, String type) {
		assertThat(jsonValue,
				allOf(hasJsonPath("$.overwrite", equalTo(overwrite)),
						hasJsonPath("$.name", equalTo(name)),
						hasJsonPath("$.type", equalTo(type))));
		assertThat(jsonValue, hasNoJsonPath("$.additional_permissions"));
	}
}
