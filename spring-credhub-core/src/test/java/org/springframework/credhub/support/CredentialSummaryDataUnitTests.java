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

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CredentialSummaryDataUnitTests extends JsonParsingUnitTestsBase {

	@Test
	public void deserializationWithCredentials() {
		String json = """
				{
					"credentials": [
					{
						"name": "/deploy123/example1",
						"version_created_at": "%s"
					},
					{
						"name": "/deploy123/example2",
						"version_created_at": "%s"
					},
					{
						"name": "/deploy123/example3",
						"version_created_at": "%s"
					}
					]
				}
				""".formatted(TEST_DATE_STRING, TEST_DATE_STRING, TEST_DATE_STRING);

		CredentialSummaryData response = parseResponse(json, CredentialSummaryData.class);

		assertThat(response.getCredentials().size()).isEqualTo(3);

		List<CredentialSummary> credentials = response.getCredentials();

		assertThat(credentials.get(0).getName().getName()).isEqualTo("/deploy123/example1");
		assertThat(credentials.get(1).getName().getName()).isEqualTo("/deploy123/example2");
		assertThat(credentials.get(2).getName().getName()).isEqualTo("/deploy123/example3");

		for (CredentialSummary credential : credentials) {
			assertThat(credential.getVersionCreatedAt()).isEqualTo(this.testDate);
		}
	}

	@Test
	public void deserializationWithEmptyCredentials() {
		String json = """
				{
					"credentials": []
				}
				""";

		CredentialSummaryData response = parseResponse(json, CredentialSummaryData.class);

		assertThat(response.getCredentials()).isNotNull();
		assertThat(response.getCredentials()).hasSize(0);
	}

}
