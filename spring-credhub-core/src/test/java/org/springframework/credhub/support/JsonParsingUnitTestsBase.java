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

import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.junit.Before;
import org.springframework.credhub.support.utils.JsonUtils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public abstract class JsonParsingUnitTestsBase {
	protected static final String TEST_DATE_STRING = "2017-01-31T11:22:33Z";

	protected final String CREDENTIAL_DETAIL_TEMPLATE =  "{" +
			"  \"version_created_at\": \"" + TEST_DATE_STRING + "\"," +
			"  \"id\": \"80cbb13f-7562-4e72-92de-f3ccf69eaa59\"," +
			"  \"name\": \"/service-broker-name/service-instance-name/binding-id/credentials-json\"," +
			" %s" +
			"}";

	protected final String CREDENTIAL_DETAILS_DATA_TEMPLATE = "{" +
			"  \"data\": [" +
			CREDENTIAL_DETAIL_TEMPLATE +
			"  ]" +
			"}";

	protected ObjectMapper objectMapper;
	protected Date testDate;

	@Before
	public void setUpJsonParsing() throws Exception {
		objectMapper = JsonUtils.buildObjectMapper();

		testDate = new ISO8601DateFormat().parse(TEST_DATE_STRING);
	}


	@SuppressWarnings("unchecked")
	protected <T> CredentialDetails<T> parseDetails(String credentials) throws java.io.IOException {
		String json = buildDetails(credentials);
		return (CredentialDetails<T>) objectMapper.readValue(json, CredentialDetails.class);
	}

	@SuppressWarnings("unchecked")
	protected <T> CredentialDetailsData<T> parseDetailsData(String credentials) throws java.io.IOException {
		String json = buildDetailsData(credentials);
		return (CredentialDetailsData<T>) objectMapper.readValue(json, CredentialDetailsData.class);
	}

	private String buildDetails(String credentials) {
		return String.format(CREDENTIAL_DETAIL_TEMPLATE, credentials);
	}

	private String buildDetailsData(String credentials) {
		return String.format(CREDENTIAL_DETAILS_DATA_TEMPLATE, credentials);
	}

	protected void assertCommonDetails(CredentialDetails<?> data) {
		assertThat(data.getVersionCreatedAt(), equalTo(testDate));
		assertThat(data.getId(), equalTo("80cbb13f-7562-4e72-92de-f3ccf69eaa59"));
		assertThat(data.getName().getName(), equalTo(
				"/service-broker-name/service-instance-name/binding-id/credentials-json"));
	}
}
