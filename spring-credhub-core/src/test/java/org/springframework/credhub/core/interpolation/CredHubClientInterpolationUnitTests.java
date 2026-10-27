/*
 * Copyright 2016-2026 the original author or authors.
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

import org.junit.jupiter.api.Test;

import org.springframework.credhub.core.CredHubClient;
import org.springframework.credhub.core.CredHubClientUnitTestsBase;
import org.springframework.credhub.support.ServiceInstanceCredentialName;
import org.springframework.credhub.support.ServicesData;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit tests of the interpolation operations of {@link CredHubClient}, converted from the
 * tests for {@code CredHubInterpolationTemplate}.
 */
class CredHubClientInterpolationUnitTests extends CredHubClientUnitTestsBase {

	@Test
	void interpolateServiceData() {
		ServiceInstanceCredentialName credentialName = ServiceInstanceCredentialName.builder()
			.serviceBrokerName("service-broker")
			.serviceOfferingName("service-offering")
			.serviceBindingId("1111-1111-1111-111")
			.credentialName("credential_json")
			.build();

		ServicesData vcapServices = MAPPER.readValue(vcapServicesJson(credentialName.getName()), ServicesData.class);

		// served as raw JSON: the mapper omits empty collections such as volume_mounts
		// when it writes a fixture, which would make the response differ from the input
		String responseJson = vcapServicesJson("interpolated");
		ServicesData expectedResponse = MAPPER.readValue(responseJson, ServicesData.class);

		this.server.expect(requestTo(BASE_URL + "/api/v1/interpolate"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(jsonBody(vcapServices))
			.andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

		ServicesData response = this.client.interpolation().interpolateServiceData(vcapServices);

		assertThat(response).isEqualTo(expectedResponse);
	}

	private String vcapServicesJson(String credHubReferenceName) {
		return """
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
	}

}
