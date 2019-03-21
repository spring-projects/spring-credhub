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

package org.springframework.credhub.core.credential;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.credhub.support.CredentialPath;
import org.springframework.credhub.support.CredentialPathData;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.credhub.core.credential.CredHubCredentialTemplate.NAME_URL_QUERY;
import static org.springframework.credhub.core.credential.CredHubCredentialTemplate.SHOW_ALL_URL_QUERY;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class CredHubCredentialTemplateUnitTests extends CredHubCredentialTemplateUnitTestsBase {
	@Test
	@SuppressWarnings("deprecation")
	public void getAllPaths() {
		when(restTemplate.getForEntity(SHOW_ALL_URL_QUERY, CredentialPathData.class))
				.thenReturn(new ResponseEntity<>(new CredentialPathData(new CredentialPath("/path1"), new CredentialPath("/path2")), OK));

		List<CredentialPath> paths = credHubTemplate.getAllPaths();

		assertThat(paths.size()).isEqualTo(2);
		assertThat(paths).extracting("path").contains("/path1", "/path2");
	}

	@Test
	public void deleteByName() {
		credHubTemplate.deleteByName(NAME);

		verify(restTemplate).delete(NAME_URL_QUERY, NAME.getName());
	}
}