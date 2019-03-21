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

package org.springframework.credhub.core;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.web.client.RestTemplate;

public abstract class CredHubTemplateUnitTestsBase {
	protected static final SimpleCredentialName NAME = new SimpleCredentialName("example", "credential");

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

	@Mock
	protected RestTemplate restTemplate;

	protected CredHubTemplate credHubTemplate;

	@Before
	public void setUpCredHubTemplateUnitTests() {
		credHubTemplate = new CredHubTemplate(restTemplate);
	}
}
