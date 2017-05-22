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

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class JsonCredentialDetailsUnitTests extends JsonParsingUnitTestsBase {
	private static final String JSON_CREDENTIALS =
			"  \"type\": \"json\"," +
			"  \"value\": {" +
			"    \"client_id\": \"test-id\"," +
			"    \"client_secret\": \"test-secret\"," +
			"    \"uri\": \"https://example.com\"" +
			"  }";

	@Test
	public void deserializeDetails() throws Exception {
		CredentialDetails<JsonCredential> data = parseDetails(JSON_CREDENTIALS, JsonCredential.class);

		assertDetails(data);
	}

	@Test
	public void deserializeDetailsData() throws Exception {
		CredentialDetailsData<JsonCredential> data = parseDetailsData(JSON_CREDENTIALS, JsonCredential.class);

		assertThat(data.getData().size(), equalTo(1));
		assertDetails(data.getData().get(0));
	}

	private void assertDetails(CredentialDetails<JsonCredential> data) {
		assertCommonDetails(data);

		assertThat(data.getValueType(), equalTo(ValueType.JSON));

		JsonCredential valueMap = data.getValue();
		assertThat(valueMap.get("client_id"), CoreMatchers.<Object> equalTo("test-id"));
		assertThat(valueMap.get("client_secret"), CoreMatchers.<Object> equalTo("test-secret"));
		assertThat(valueMap.get("uri"), CoreMatchers.<Object> equalTo("https://example.com"));
	}
}
