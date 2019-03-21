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

package org.springframework.credhub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.credhub.core.credential.CredHubCredentialOperations;
import org.springframework.credhub.core.interpolation.CredHubInterpolationOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.ServicesData;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.credhub.support.utils.JsonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class InterpolationIntegrationTests extends CredHubIntegrationTests {
	private static final SimpleCredentialName CREDENTIAL_NAME =
			new SimpleCredentialName("spring-credhub", "integration-test", "interpolation-credential");

	private CredHubInterpolationOperations interpolation;
	private CredHubCredentialOperations credentials;

	@Before
	public void setUp() {
		this.interpolation = operations.interpolation();
		this.credentials = operations.credentials();
	}

	@After
	public void tearDown() {
		deleteCredentialIfExists(CREDENTIAL_NAME);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void interpolate() throws IOException {
		Map<String, Object> json = new HashMap<String, Object>() {{
			put("url", "https://example.com");
			put("username", "user");
			put("password", "secret");
		}};

		CredentialDetails<JsonCredential> written = credentials.write(JsonCredentialRequest.builder()
				.name(CREDENTIAL_NAME)
				.value(json)
				.build());
		assertThat(written.getName().getName()).isEqualTo(CREDENTIAL_NAME.getName());
		assertThat(written.getValue()).isEqualTo(json);
		assertThat(written.getCredentialType()).isEqualTo(CredentialType.JSON);
		assertThat(written.getId()).isNotNull();

		ServicesData servicesData =
				interpolation.interpolateServiceData(buildVcapServices(CREDENTIAL_NAME.getName()));
		assertThat(servicesData).containsKey("service-offering");
		assertThat(servicesData.get("service-offering")).hasSize(1);
		assertThat(servicesData.get("service-offering").get(0)).containsKey("credentials");

		Map<String, Object> credentials =
				(Map<String, Object>) servicesData.get("service-offering").get(0).get("credentials");
		assertThat(credentials)
				.containsEntry("url", "https://example.com")
				.containsEntry("username", "user")
				.containsEntry("password", "secret");
	}

	private ServicesData buildVcapServices(String credHubReferenceName) throws IOException {
		String vcapServices = "{" +
				"  \"service-offering\": [" +
				"   {" +
				"    \"credentials\": {" +
				"      \"credhub-ref\": \"((" + credHubReferenceName + "))\"" +
				"    }," +
				"    \"label\": \"service-offering\"," +
				"    \"name\": \"service-instance\"," +
				"    \"plan\": \"standard\"," +
				"    \"tags\": [" +
				"     \"cloud-service\"" +
				"    ]," +
				"    \"volume_mounts\": []" +
				"   }" +
				"  ]" +
				"}";

		ObjectMapper mapper = JsonUtils.buildObjectMapper();
		return mapper.readValue(vcapServices, ServicesData.class);
	}
}
