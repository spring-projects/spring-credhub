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

package org.springframework.credhub.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.AccessControlEntry;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.credhub.support.AccessControlEntry.Operation.READ;

@RestController
public class CredHubDemoController {
	@Value("${vcap.application.application_id:}")
	private String appId;

	private CredHubTemplate credHubTemplate;

	public CredHubDemoController(CredHubTemplate credHubTemplate) {
		this.credHubTemplate = credHubTemplate;
	}

	@PostMapping("/test")
	public Results runTests(@RequestBody Map<String, Object> value) {
		Results results = new Results();

		CredentialDetails credentialDetails = writeCredentials(value, results);
		CredentialName credentialName = credentialDetails.getName();

		getCredentialsById(credentialDetails.getId(), results);

		getCredentialsByName(credentialName, results);

		findCredentialsByName(credentialName, results);

		findCredentialsByPath(credentialName.getName(), results);

		interpolateServiceData(credentialName, results);

		deleteCredentials(credentialName, results);

		return results;
	}

	@SuppressWarnings("unchecked")
	private <T> CredentialDetails writeCredentials(T value, Results results) {
		try {
			WriteRequest.WriteRequestBuilder requestBuilder = WriteRequest.builder()
					.overwrite(true)
					.name(new SimpleCredentialName("spring-credhub", "demo", "credentials_json"))
					.jsonValue((Map<String, Object>) value);

			if (StringUtils.hasText(appId)) {
				requestBuilder.accessControlEntry(
						AccessControlEntry.builder().app(appId).operation(READ).build());
			}

			WriteRequest request = requestBuilder.build();

			CredentialDetails credentialDetails = credHubTemplate.write(request);
			saveResults(results, "Successfully wrote credentials: ", credentialDetails);

			return credentialDetails;
		}
		catch (Exception e) {
			saveResults(results, "Error writing credentials: ", e.getMessage());
			return null;
		}
	}

	private void getCredentialsById(String id, Results results) {
		try {
			CredentialDetails retrievedDetails = credHubTemplate.getById(id);
			saveResults(results, "Successfully retrieved credentials by ID: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error retrieving credentials by ID: ", e.getMessage());
		}
	}

	private void getCredentialsByName(CredentialName name, Results results) {
		try {
			List<CredentialDetails> retrievedDetails = credHubTemplate.getByName(name);
			saveResults(results, "Successfully retrieved credentials by name: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error retrieving credentials by name: ", e.getMessage());
		}
	}

	private void findCredentialsByName(CredentialName name, Results results) {
		try {
			List<CredentialSummary> retrievedDetails = credHubTemplate.findByName(name);
			saveResults(results, "Successfully found credentials by name: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error finding credentials by name: ", e.getMessage());
		}
	}

	private void findCredentialsByPath(String path, Results results) {
		try {
			List<CredentialSummary> retrievedDetails = credHubTemplate.findByPath(path);
			saveResults(results, "Successfully found credentials by path: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error finding credentials by path: ", e.getMessage());
		}
	}

	private void interpolateServiceData(CredentialName name, Results results) {
		try {
			Map<String, Object> request = buildVcapServicesData(name.getName());
			Map<String, Object> interpolatedServiceData = credHubTemplate.interpolateServiceData(request);
			saveResults(results, "Successfully interpolated service data: ", interpolatedServiceData);
		} catch (Exception e) {
			saveResults(results, "Error interpolating service data: ", e.getMessage());
		}
	}

	private void deleteCredentials(CredentialName name, Results results) {
		try {
			credHubTemplate.deleteByName(name);
			saveResults(results, "Successfully deleted credentials");
		} catch (Exception e) {
			saveResults(results, "Error deleting credentials by name: ", e.getMessage());
		}
	}

	private Map<String, Object> buildVcapServicesData(String credHubReferenceName) throws IOException {
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

		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(vcapServices, new TypeReference<Map<String, Object>>() {});
	}

	private void saveResults(Results results, String message) {
		saveResults(results, message, null);
	}

	private void saveResults(Results results, String message, Object details) {
		results.add(Collections.singletonMap(message, details));
	}

	private class Results extends ArrayList<Map<String, Object>> {
	}
}
