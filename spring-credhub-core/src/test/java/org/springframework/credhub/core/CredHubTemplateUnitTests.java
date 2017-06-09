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

package org.springframework.credhub.core;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.credhub.support.JsonUtils;
import org.springframework.credhub.support.ServiceInstanceCredentialName;
import org.springframework.credhub.support.ServicesData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.CredHubTemplate.INTERPOLATE_URL_PATH;
import static org.springframework.credhub.core.CredHubTemplate.NAME_URL_QUERY;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class CredHubTemplateUnitTests extends CredHubTemplateUnitTestsBase {
	@Test
	public void deleteByName() {
		credHubTemplate.deleteByName(NAME.getName());

		verify(restTemplate).delete(NAME_URL_QUERY, NAME.getName());
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

		when(restTemplate.exchange(INTERPOLATE_URL_PATH, HttpMethod.POST,
				new HttpEntity<ServicesData>(vcapServices), ServicesData.class))
				.thenReturn(new ResponseEntity<ServicesData>(expectedResponse, OK));

		ServicesData response = credHubTemplate.interpolateServiceData(vcapServices);

		assertThat(response, equalTo(expectedResponse));
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