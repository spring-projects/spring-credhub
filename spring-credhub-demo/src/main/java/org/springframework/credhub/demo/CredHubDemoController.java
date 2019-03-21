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

package org.springframework.credhub.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.CredentialPermission;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.ServicesData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.credhub.support.permissions.Operation.DELETE;
import static org.springframework.credhub.support.permissions.Operation.READ;
import static org.springframework.credhub.support.permissions.Operation.READ_ACL;
import static org.springframework.credhub.support.permissions.Operation.WRITE;
import static org.springframework.credhub.support.permissions.Operation.WRITE_ACL;

@RestController
public class CredHubDemoController {
	private static final String APP_GUID_1 = UUID.randomUUID().toString();
	private static final String APP_GUID_2 = UUID.randomUUID().toString();

	private CredHubOperations credHubTemplate;

	public CredHubDemoController(CredHubOperations credHubTemplate) {
		this.credHubTemplate = credHubTemplate;
	}

	@PostMapping("/test")
	public Results runTests(@RequestBody Map<String, Object> value) {
		Results results = new Results();

		try {
			CredentialDetails<JsonCredential> credentialDetails = writeCredentials(value, results);
			CredentialName credentialName = credentialDetails.getName();

			getCredentialsById(credentialDetails.getId(), results);

			getCredentialsByName(credentialName, results);

			findCredentialsByName(credentialName, results);

			findCredentialsByPath(credentialName.getName(), results);

			getCredentialPermissions(credentialName, results);

			addCredentialPermissions(credentialName, results);

			deleteCredentialPermission(credentialName, results);

			getCredentialPermissions(credentialName, results);

			interpolateServiceData(credentialName, results);

			deleteCredentials(credentialName, results);
		} catch (Exception e) {
			saveResults(results, "Exception caught: " + e.getMessage());
		}

		return results;
	}

	private CredentialDetails<JsonCredential> writeCredentials(Map<String, Object> value, Results results) {
		try {
			JsonCredentialRequest request = JsonCredentialRequest.builder()
					.overwrite(true)
					.name(new SimpleCredentialName("spring-credhub", "demo", "credentials_json"))
					.value(value)
					.permission(CredentialPermission.builder()
							.app(APP_GUID_1)
							.operation(READ)
							.operation(WRITE)
							.build())
					.build();

			CredentialDetails<JsonCredential> credentialDetails = credHubTemplate.write(request);
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
			CredentialDetails<JsonCredential> retrievedDetails =
					credHubTemplate.getById(id, JsonCredential.class);
			saveResults(results, "Successfully retrieved credentials by ID: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error retrieving credentials by ID: ", e.getMessage());
		}
	}

	private void getCredentialsByName(CredentialName name, Results results) {
		try {
			CredentialDetails<JsonCredential> retrievedDetails =
					credHubTemplate.getByName(name, JsonCredential.class);
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

	private void getCredentialPermissions(CredentialName name, Results results) {
		try {
			List<CredentialPermission> retrievedDetails = credHubTemplate.getPermissions(name);
			saveResults(results, "Successfully retrieved credential permissions: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error retrieving credential permissions: ", e.getMessage());
		}
	}

	private void addCredentialPermissions(CredentialName name, Results results) {
		try {
			CredentialPermission permission = CredentialPermission.builder()
					.app(APP_GUID_2)
					.operations(READ_ACL, WRITE_ACL, DELETE)
					.build();

			List<CredentialPermission> permissions = credHubTemplate.addPermissions(name, permission);
			saveResults(results, "Successfully added permissions: ", permissions);
		} catch (Exception e) {
			saveResults(results, "Error adding permission: ", e.getMessage());
		}	}

	private void deleteCredentialPermission(CredentialName name, Results results) {
		try {
			credHubTemplate.deletePermission(name, Actor.app(APP_GUID_1));
			saveResults(results, "Successfully deleted permission");
		} catch (Exception e) {
			saveResults(results, "Error deleting permission: ", e.getMessage());
		}
	}

	private void interpolateServiceData(CredentialName name, Results results) {
		try {
			ServicesData request = buildServicesData(name.getName());
			ServicesData interpolatedServiceData = credHubTemplate.interpolateServiceData(request);
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

	private ServicesData buildServicesData(String credHubReferenceName) throws IOException {
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
		return mapper.readValue(vcapServices, ServicesData.class);
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
