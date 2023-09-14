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

package org.springframework.credhub.core.interpolation;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.ServiceInstanceCredentialName;
import org.springframework.credhub.support.ServicesData;
import org.springframework.credhub.support.utils.JsonUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class CredHubInterpolationTemplateUnitTests {

	@Mock
	private RestTemplate restTemplate;

	private CredHubInterpolationOperations credHubTemplate;

	@BeforeEach
	public void setUp() {
		this.credHubTemplate = new CredHubTemplate(this.restTemplate).interpolation();
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

		given(this.restTemplate.exchange(CredHubInterpolationTemplate.INTERPOLATE_URL_PATH, HttpMethod.POST,
				new HttpEntity<>(vcapServices), ServicesData.class))
			.willReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

		ServicesData response = this.credHubTemplate.interpolateServiceData(vcapServices);

		assertThat(response).isEqualTo(expectedResponse);
	}

	private ServicesData buildVcapServices(String credHubReferenceName) throws IOException {
		String vcapServices = """
				{
					"service-offering": [{
						"credentials": {
							"credhub-ref": "((%s))"
						},
						"label": "service-offering",
						"name": "service-instance",
						"plan": "standard",
						"tags": [ "cloud-service" ],
						"volume_mounts": []
					}]
				}
				""".formatted(credHubReferenceName);

		ObjectMapper mapper = JsonUtils.buildObjectMapper();
		return mapper.readValue(vcapServices, ServicesData.class);
	}

}
