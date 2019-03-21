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
import org.springframework.credhub.core.credential.CredHubCredentialOperations;
import org.springframework.credhub.core.interpolation.CredHubInterpolationOperations;
import org.springframework.credhub.core.permission.CredHubPermissionOperations;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialSummary;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.ServicesData;
import org.springframework.credhub.support.permissions.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CredHubDemoController {
	private static final String APP_GUID_2 = UUID.randomUUID().toString();

	private CredHubCredentialOperations credentialOperations;
	private final CredHubPermissionOperations permissionOperations;
	private final CredHubInterpolationOperations interpolationOperations;

	public CredHubDemoController(CredHubOperations credHubOperations) {
		this.credentialOperations = credHubOperations.credentials();
		this.permissionOperations = credHubOperations.permissions();
		this.interpolationOperations = credHubOperations.interpolation();
	}

	@PostMapping("/test")
	public Results runTests(@RequestBody Map<String, Object> value) {
		Results results = new Results();

		try {
			SimpleCredentialName credentialName =
					new SimpleCredentialName("spring-credhub", "demo", "credentials_json");

			CredentialDetails<JsonCredential> credentialDetails =
					writeCredentials(credentialName, value, results);

			getCredentialsById(credentialDetails.getId(), results);

			getCredentialsByName(credentialName, results);

			findCredentialsByName(credentialName, results);

			findCredentialsByPath("/spring-credhub/demo", results);

			getCredentialPermissions(credentialName, results);

			addCredentialPermissions(credentialName, results);

			getCredentialPermissions(credentialName, results);

			interpolateServiceData(credentialName, results);

			deleteCredentials(credentialName, results);
		} catch (Exception e) {
			saveResults(results, "Exception caught: " + e.getMessage());
		}

		return results;
	}

	private CredentialDetails<JsonCredential> writeCredentials(SimpleCredentialName name,
															   Map<String, Object> value,
															   Results results) {
		try {
			JsonCredentialRequest request = JsonCredentialRequest.builder()
					.name(name)
					.value(value)
					.build();

			CredentialDetails<JsonCredential> credentialDetails = credentialOperations.write(request);
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
					credentialOperations.getById(id, JsonCredential.class);
			saveResults(results, "Successfully retrieved credentials by ID: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error retrieving credentials by ID: ", e.getMessage());
		}
	}

	private void getCredentialsByName(CredentialName name, Results results) {
		try {
			CredentialDetails<JsonCredential> retrievedDetails =
					credentialOperations.getByName(name, JsonCredential.class);
			saveResults(results, "Successfully retrieved credentials by name: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error retrieving credentials by name: ", e.getMessage());
		}
	}

	private void findCredentialsByName(CredentialName name, Results results) {
		try {
			List<CredentialSummary> retrievedDetails = credentialOperations.findByName(name);
			saveResults(results, "Successfully found credentials by name: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error finding credentials by name: ", e.getMessage());
		}
	}

	private void findCredentialsByPath(String path, Results results) {
		try {
			List<CredentialSummary> retrievedDetails = credentialOperations.findByPath(path);
			saveResults(results, "Successfully found credentials by path: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error finding credentials by path: ", e.getMessage());
		}
	}

	private void getCredentialPermissions(CredentialName name, Results results) {
		try {
			List<Permission> retrievedDetails = permissionOperations.getPermissions(name);
			saveResults(results, "Successfully retrieved credential permissions: ", retrievedDetails);
		} catch (Exception e) {
			saveResults(results, "Error retrieving credential permissions: ", e.getMessage());
		}
	}

	private void addCredentialPermissions(CredentialName name, Results results) {
		try {
			Permission permission = Permission.builder()
					.app(APP_GUID_2)
					.operations(Operation.READ, Operation.WRITE, Operation.DELETE)
					.build();

			permissionOperations.addPermissions(name, permission);
			saveResults(results, "Successfully added permissions");
		} catch (Exception e) {
			saveResults(results, "Error adding permission: ", e.getMessage());
		}
	}

	private void interpolateServiceData(CredentialName name, Results results) {
		try {
			ServicesData request = buildServicesData(name.getName());
			ServicesData interpolatedServiceData = interpolationOperations.interpolateServiceData(request);
			saveResults(results, "Successfully interpolated service data: ", interpolatedServiceData);
		} catch (Exception e) {
			saveResults(results, "Error interpolating service data: ", e.getMessage());
		}
	}

	private void deleteCredentials(CredentialName name, Results results) {
		try {
			credentialOperations.deleteByName(name);
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
