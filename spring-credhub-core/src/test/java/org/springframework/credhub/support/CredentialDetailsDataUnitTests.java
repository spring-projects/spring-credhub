/*
 *
 *  * Copyright 2013-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.credhub.support;

import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class CredentialDetailsDataUnitTests extends JsonParsingUnitTestsBase {

	@Test
	public void deserializationWithPasswordValue() throws Exception {
		String json = "{" +
				"  \"data\": [" +
				"    {" +
				"      \"type\": \"password\"," +
				"      \"version_created_at\": \"" + testDateString + "\"," +
				"      \"id\": \"80cbb13f-7562-4e72-92de-f3ccf69eaa59\"," +
				"      \"name\": \"/c/service-broker-name/service-instance-name/binding-id/credentials-json\"," +
				"      \"value\": \"secret\"" +
				"    }" +
				"  ]" +
				"}";

		CredentialDetailsData response = parseResponse(json);

		assertThat(response.getData().size(), equalTo(1));

		CredentialDetails data = response.getData().get(0);

		assertThat(data.getValueType(), equalTo(ValueType.PASSWORD));
		assertThat(data.getVersionCreatedAt(), equalTo(testDate));
		assertThat(data.getId(), equalTo("80cbb13f-7562-4e72-92de-f3ccf69eaa59"));
		assertThat(data.getName().getName(), equalTo(
				"/c/service-broker-name/service-instance-name/binding-id/credentials-json"));

		assertThat(data.getValue(), instanceOf(String.class));
		assertThat(data.getValue(), CoreMatchers.<Object> equalTo("secret"));
	}

	@Test
	public void deserializationWithJsonValue() throws Exception {
		String json = "{" +
				"  \"data\": [" +
				"    {" +
				"      \"type\": \"json\"," +
				"      \"version_created_at\": \"" + testDateString + "\"," +
				"      \"id\": \"80cbb13f-7562-4e72-92de-f3ccf69eaa59\"," +
				"      \"name\": \"/c/service-broker-name/service-instance-name/binding-id/credentials-json\"," +
				"      \"value\": {" +
				"        \"client_id\": \"test-id\"," +
				"        \"client_secret\": \"test-secret\"," +
				"        \"uri\": \"https://example.com\"" +
				"      }" +
				"    }" +
				"  ]" +
				"}";

		CredentialDetailsData response = parseResponse(json);

		assertThat(response.getData().size(), equalTo(1));

		CredentialDetails data = response.getData().get(0);

		assertThat(data.getValueType(), equalTo(ValueType.JSON));
		assertThat(data.getVersionCreatedAt(), equalTo(testDate));
		assertThat(data.getId(), equalTo("80cbb13f-7562-4e72-92de-f3ccf69eaa59"));
		assertThat(data.getName().getName(), equalTo(
				"/c/service-broker-name/service-instance-name/binding-id/credentials-json"));

		assertThat(data.getValue(), instanceOf(Map.class));
		Map<String, Object> valueMap = (Map<String, Object>) data.getValue();
		assertThat(valueMap.get("client_id"), CoreMatchers.<Object> equalTo("test-id"));
		assertThat(valueMap.get("client_secret"),
				CoreMatchers.<Object> equalTo("test-secret"));
		assertThat(valueMap.get("uri"),
				CoreMatchers.<Object> equalTo("https://example.com"));
	}

	private CredentialDetailsData parseResponse(String json)
			throws java.io.IOException {
		return objectMapper.readValue(json, CredentialDetailsData.class);
	}
}
