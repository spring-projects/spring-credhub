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

package org.springframework.credhub.core.interpolation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.ServiceInstanceCredentialName;
import org.springframework.credhub.support.ServicesData;
import org.springframework.credhub.support.utils.JsonUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.interpolation.CredHubInterpolationTemplate.INTERPOLATE_URL_PATH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class CredHubInterpolationTemplateUnitTests {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

	@Mock
	private RestTemplate restTemplate;

	private CredHubInterpolationOperations credHubTemplate;

	@Before
	public void setUp() {
		credHubTemplate = new CredHubTemplate(restTemplate).interpolation();
	}

	@Test
	public void interpolateServiceData() throws IOException {
		ServiceInstanceCredentialName credentialName = ServiceInstanceCredentialName.builder()
				.serviceBrokerName("service-broker")
				.serviceOfferingName("service-offering")
				.serviceBindingId("1111-1111-1111-111")
				.credentialName("credential_json")
				.build();

		ServicesData vcapServices = buildVcapServices(credentialName.getName());

		ServicesData expectedResponse = new ServicesData();

		when(restTemplate.exchange(INTERPOLATE_URL_PATH, POST,
				new HttpEntity<>(vcapServices), ServicesData.class))
				.thenReturn(new ResponseEntity<>(expectedResponse, OK));

		ServicesData response = credHubTemplate.interpolateServiceData(vcapServices);

		assertThat(response).isEqualTo(expectedResponse);
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