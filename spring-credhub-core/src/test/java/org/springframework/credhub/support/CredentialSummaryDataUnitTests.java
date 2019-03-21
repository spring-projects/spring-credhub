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

import java.util.List;

import org.junit.Test;

import org.springframework.credhub.support.json.JsonCredential;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CredentialSummaryDataUnitTests extends JsonParsingUnitTestsBase {
	@Test
	public void deserializationWithCredentials() throws Exception {
		String json = "{\n" +
				"  \"credentials\": [\n" +
				"    {\n" +
				"      \"name\": \"/deploy123/example1\",\n" +
				"      \"version_created_at\": \"" + TEST_DATE_STRING + "\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"name\": \"/deploy123/example2\",\n" +
				"      \"version_created_at\": \"" + TEST_DATE_STRING + "\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"name\": \"/deploy123/example3\",\n" +
				"      \"version_created_at\": \"" + TEST_DATE_STRING + "\"\n" +
				"    }\n" +
				"  ]\n" +
				"}";

		CredentialSummaryData response = parseResponse(json, JsonCredential.class);

		assertThat(response.getCredentials().size(), equalTo(3));

		List<CredentialSummary> credentials = response.getCredentials();

		assertThat(credentials.get(0).getName().getName(),
				equalTo("/deploy123/example1"));
		assertThat(credentials.get(1).getName().getName(),
				equalTo("/deploy123/example2"));
		assertThat(credentials.get(2).getName().getName(),
				equalTo("/deploy123/example3"));

		for (CredentialSummary credential : credentials) {
			assertThat(credential.getVersionCreatedAt(), equalTo(testDate));
		}
	}

	@Test
	public void deserializationWithEmptyCredentials() throws Exception {
		String json = "{\n" +
				"  \"credentials\": [\n" +
				"  ]\n" +
				"}";

		CredentialSummaryData response = parseResponse(json, JsonCredential.class);

		assertThat(response.getCredentials(), notNullValue());
		assertThat(response.getCredentials().size(), equalTo(0));
	}

	private CredentialSummaryData parseResponse(String json, Class<JsonCredential> jsonCredentialClass)
			throws java.io.IOException {
		return objectMapper.readValue(json, CredentialSummaryData.class);
	}
}
