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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public abstract class CredHubCredentialTemplateUnitTestsBase {

	protected static final SimpleCredentialName NAME = new SimpleCredentialName("example", "credential");

	@Mock
	protected RestTemplate restTemplate;

	protected CredHubCredentialOperations credHubTemplate;

	@BeforeEach
	public void setUpCredHubTemplateUnitTests() {
		this.credHubTemplate = new CredHubTemplate(this.restTemplate).credentials();
	}

}
