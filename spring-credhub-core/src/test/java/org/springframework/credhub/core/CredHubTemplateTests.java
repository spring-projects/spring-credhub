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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.credhub.support.CredentialDataResponse;
import org.springframework.credhub.support.CredentialData;
import org.springframework.credhub.support.FindResponse;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.WriteRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.CredHubTemplate.BASE_URL_PATH;
import static org.springframework.credhub.core.CredHubTemplate.NAME_LIKE_URL_QUERY;
import static org.springframework.credhub.core.CredHubTemplate.NAME_URL_QUERY;
import static org.springframework.credhub.core.CredHubTemplate.PATH_URL_QUERY;
import static org.springframework.credhub.core.CredHubTemplate.ID_URL_PATH;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RunWith(MockitoJUnitRunner.class)
public class CredHubTemplateTests {
	private static final String CREDENTIAL_ID = "1111-1111-1111-1111";
	private static final String CREDENTIAL_VALUE = "secret";

	@Mock
	private RestTemplate restTemplate;

	private CredHubTemplate credHubTemplate;

	private SimpleCredentialName name;

	private CredentialDataResponse dataResponse;
	private CredentialDataResponse dataResponseWithError;

	private FindResponse findResponse;
	private FindResponse findResponseWithError;

	@Before
	public void setUp() {
		credHubTemplate = new CredHubTemplate(restTemplate);

		name = SimpleCredentialName.builder().segments("example", "credential").build();

		dataResponse = CredentialDataResponse.builder()
				.datum(CredentialData.builder()
						.name(name)
						.id(CREDENTIAL_ID)
						.passwordValue(CREDENTIAL_VALUE)
						.build())
				.build();

		dataResponseWithError = CredentialDataResponse.builder()
				.errorMessage("errorMessage message")
				.build();

		findResponse = FindResponse.builder()
				.foundCredential(FindResponse.FoundCredential.builder()
						.name(name)
						.versionCreatedAt("")
						.build())
				.build();

		findResponseWithError = FindResponse.builder()
				.errorMessage("errorMessage message")
				.build();
	}

	@Test
	public void writeWithSuccess() {
		WriteRequest request = WriteRequest.builder()
				.name(name)
				.passwordValue("secret")
				.build();

		when(restTemplate.exchange(BASE_URL_PATH, PUT, new HttpEntity<WriteRequest>(request), CredentialDataResponse.class))
				.thenReturn(new ResponseEntity<CredentialDataResponse>(dataResponse, OK));

		CredentialData response = credHubTemplate.write(request);

		assertThat(response, notNullValue());
		assertThat(response, equalTo(dataResponse.getData().get(0)));
	}

	@Test
	public void writeWithErrorResponse() {
		WriteRequest request = WriteRequest.builder()
				.name(name)
				.passwordValue("secret")
				.build();

		when(restTemplate.exchange(BASE_URL_PATH, PUT, new HttpEntity<WriteRequest>(request), CredentialDataResponse.class))
				.thenReturn(new ResponseEntity<CredentialDataResponse>(dataResponseWithError, OK));

		try {
			credHubTemplate.write(request);
			fail("Exception should have been thrown");
		} catch (CredHubException e) {
			assertThat(e.getMessage(), containsString(": errorMessage message"));
		}
	}

	@Test
	public void writeWithHttpError() {
		WriteRequest request = WriteRequest.builder()
				.name(name)
				.passwordValue("secret")
				.build();

		when(restTemplate.exchange(BASE_URL_PATH, PUT, new HttpEntity<WriteRequest>(request), CredentialDataResponse.class))
				.thenReturn(new ResponseEntity<CredentialDataResponse>(dataResponseWithError, HttpStatus.UNAUTHORIZED));

		try {
			credHubTemplate.write(request);
			fail("Exception should have been thrown");
		} catch (CredHubException e) {
			assertThat(e.getMessage(), containsString(": " + HttpStatus.UNAUTHORIZED.toString()));
		}
	}

	@Test
	public void getByIdWithSuccess() {
		when(restTemplate.getForEntity(ID_URL_PATH, CredentialDataResponse.class, CREDENTIAL_ID))
				.thenReturn(new ResponseEntity<CredentialDataResponse>(dataResponse, OK));

		CredentialData response = credHubTemplate.getById(CREDENTIAL_ID);

		assertThat(response, notNullValue());
		assertThat(response, equalTo(dataResponse.getData().get(0)));
	}

	@Test
	public void getByIdWithError() {
		when(restTemplate.getForEntity(ID_URL_PATH, CredentialDataResponse.class, CREDENTIAL_ID))
				.thenReturn(new ResponseEntity<CredentialDataResponse>(dataResponseWithError, OK));

		try {
			credHubTemplate.getById(CREDENTIAL_ID);
			fail("Exception should have been thrown");
		} catch (CredHubException e) {
			assertThat(e.getMessage(), containsString(": errorMessage message"));
		}
	}

	@Test
	public void getByIdWithHttpError() {
		when(restTemplate.getForEntity(ID_URL_PATH, CredentialDataResponse.class, CREDENTIAL_ID))
				.thenReturn(new ResponseEntity<CredentialDataResponse>(dataResponseWithError, UNAUTHORIZED));

		try {
			credHubTemplate.getById(CREDENTIAL_ID);
			fail("Exception should have been thrown");
		} catch (CredHubException e) {
			assertThat(e.getMessage(), containsString(": " + HttpStatus.UNAUTHORIZED.toString()));
		}
	}

	@Test
	public void findByNameWithSuccess() {
		when(restTemplate.getForEntity(NAME_LIKE_URL_QUERY, FindResponse.class, name.getName()))
				.thenReturn(new ResponseEntity<FindResponse>(findResponse, OK));

		FindResponse response = credHubTemplate.findByName(name.getName());

		assertThat(response, notNullValue());
		assertThat(response.getFoundCredentials().size(), equalTo(1));
		assertThat(response.getFoundCredentials(), equalTo(findResponse.getFoundCredentials()));
	}

	@Test
	public void findByNameWithError() {
		when(restTemplate.getForEntity(NAME_LIKE_URL_QUERY, FindResponse.class, name.getName()))
				.thenReturn(new ResponseEntity<FindResponse>(findResponseWithError, OK));

		try {
			credHubTemplate.findByName(name.getName());
			fail("Exception should have been thrown");
		} catch (CredHubException e) {
			assertThat(e.getMessage(), containsString(": errorMessage message"));
		}
	}

	@Test
	public void findByNameWithHttpError() {
		when(restTemplate.getForEntity(NAME_LIKE_URL_QUERY, FindResponse.class, name.getName()))
				.thenReturn(new ResponseEntity<FindResponse>(findResponseWithError, UNAUTHORIZED));

		try {
			credHubTemplate.findByName(name.getName());
			fail("Exception should have been thrown");
		} catch (CredHubException e) {
			assertThat(e.getMessage(), containsString(": " + HttpStatus.UNAUTHORIZED.toString()));
		}
	}

	@Test
	public void findByPathWithSuccess() {
		when(restTemplate.getForEntity(PATH_URL_QUERY, FindResponse.class, name.getName()))
				.thenReturn(new ResponseEntity<FindResponse>(findResponse, OK));

		FindResponse response = credHubTemplate.findByPath(name.getName());

		assertThat(response, notNullValue());
		assertThat(response.getFoundCredentials().size(), equalTo(1));
		assertThat(response.getFoundCredentials(), equalTo(findResponse.getFoundCredentials()));
	}

	@Test
	public void findByPathWithError() {
		when(restTemplate.getForEntity(PATH_URL_QUERY, FindResponse.class, name.getName()))
				.thenReturn(new ResponseEntity<FindResponse>(findResponseWithError, OK));

		try {
			credHubTemplate.findByPath(name.getName());
			fail("Exception should have been thrown");
		} catch (CredHubException e) {
			assertThat(e.getMessage(), containsString(": errorMessage message"));
		}
	}

	@Test
	public void findByPathWithHttpError() {
		when(restTemplate.getForEntity(PATH_URL_QUERY, FindResponse.class, name.getName()))
				.thenReturn(new ResponseEntity<FindResponse>(findResponseWithError, UNAUTHORIZED));

		try {
			credHubTemplate.findByPath(name.getName());
			fail("Exception should have been thrown");
		} catch (CredHubException e) {
			assertThat(e.getMessage(), containsString(": " + HttpStatus.UNAUTHORIZED.toString()));
		}
	}

	@Test
	public void deleteByName() {
		credHubTemplate.deleteByName(name.getName());

		verify(restTemplate).delete(NAME_URL_QUERY, name.getName());
	}
}