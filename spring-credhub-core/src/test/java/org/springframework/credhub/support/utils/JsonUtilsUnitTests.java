/*
 * Copyright 2016-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.support.utils;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonUtilsUnitTests {

	@Test
	public void defaultMapperOmitsEmptyScalarsAndEmptyMapValues() {
		String json = JsonUtils.buildJsonMapper().writeValueAsString(new AllEmptySample());
		assertThat(json).isEqualTo("{}");
	}

	@Test
	public void defaultMapperStillWritesNonEmptyContent() {
		String json = JsonUtils.buildJsonMapper().writeValueAsString(new NonEmptySample());
		assertThat(json).isEqualTo("""
				{"map_with_data":{"k":"v"}}""");
	}

	private static class AllEmptySample {

		@JsonProperty
		public String emptyString = "";

		@JsonProperty
		public String nullString;

		@JsonProperty
		public Map<String, String> emptyMap = Map.of();

		@JsonProperty
		public Map<String, String> mapWithEmptyValue = Map.of("k", "");

	}

	private static class NonEmptySample {

		@JsonProperty
		public Map<String, String> mapWithData = Map.of("k", "v");

	}

}
