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

package org.springframework.credhub.core.credential;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.credhub.support.CredentialPath;
import org.springframework.credhub.support.CredentialPathData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CredHubCredentialTemplateUnitTests extends CredHubCredentialTemplateUnitTestsBase {

	@Test
	@SuppressWarnings("deprecation")
	public void getAllPaths() {
		given(this.restTemplate.getForEntity(CredHubCredentialTemplate.SHOW_ALL_URL_QUERY, CredentialPathData.class))
				.willReturn(new ResponseEntity<>(
						new CredentialPathData(new CredentialPath("/path1"), new CredentialPath("/path2")),
						HttpStatus.OK));

		List<CredentialPath> paths = this.credHubTemplate.getAllPaths();

		assertThat(paths.size()).isEqualTo(2);
		assertThat(paths).extracting("path").contains("/path1", "/path2");
	}

	@Test
	public void deleteByName() {
		this.credHubTemplate.deleteByName(NAME);

		verify(this.restTemplate).delete(CredHubCredentialTemplate.NAME_URL_QUERY, NAME.getName());
	}

}
