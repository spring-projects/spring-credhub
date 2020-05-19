/*
 * Copyright 2016-2020 the original author or authors.
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

package org.springframework.credhub.support;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CredentialPathDataTests extends JsonParsingUnitTestsBase {

	@Test
	public void deserializeWithPaths() {
		// @formatter:off
		String json = "{\n" +
				"  \"paths\": [\n" +
				"    {\n" +
				"      \"path\": \"/\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"path\": \"/director-name/\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"path\": \"/director-name/deploy1/\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"path\": \"/director-name/deploy2/\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"path\": \"/director2/\"\n" +
				"    }\n" +
				"  ]\n" +
				"}";
		// @formatter:on

		CredentialPathData paths = parseResponse(json, CredentialPathData.class);

		assertThat(paths.getPaths().size()).isEqualTo(5);
		assertThat(paths.getPaths()).extracting("path").contains("/", "/director-name/", "/director-name/deploy1/",
				"/director-name/deploy2/", "/director2/");
	}

	@Test
	public void deserializeWithNoPaths() {
		String json = "{\n" + "  \"paths\": []" + "}";

		CredentialPathData paths = parseResponse(json, CredentialPathData.class);

		assertThat(paths.getPaths().size()).isEqualTo(0);
	}

}
