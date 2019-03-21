/*
 *
 *  * Copyright 2013-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.credhub.support;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SimpleCredentialNameUnitTests {
	@Test
	public void singleElementNameIsConstructed() {
		CredentialName credentialName = new SimpleCredentialName("credential-name");

		assertThat(credentialName.getName(), equalTo("credential-name"));
	}
	
	@Test
	public void singleElementNameWithLeadingSlashIsParsed() {
		CredentialName credentialName = new CredentialName("/credential-name");

		assertThat(credentialName.getName(), equalTo("credential-name"));
	}

	@Test
	public void singleElementNameWithoutLeadingSlashIsParsed() {
		CredentialName credentialName = new CredentialName("credential-name");

		assertThat(credentialName.getName(), equalTo("credential-name"));
	}

	@Test
	public void simpleNameIsConstructed() {
		CredentialName credentialName =
				new SimpleCredentialName("myorg", "example", "credential-name");

		assertThat(credentialName.getName(), equalTo("/myorg/example/credential-name"));
	}
	
	@Test
	public void simpleNameIsParsed() {
		CredentialName credentialName = new CredentialName("/myorg/example/credential-name");

		assertThat(credentialName.getName(), equalTo("/myorg/example/credential-name"));
	}

	@Test
	public void simpleNameWithoutLeadingSlashIsParsed() {
		CredentialName credentialName = new CredentialName("myorg/example/credential-name");

		assertThat(credentialName.getName(), equalTo("/myorg/example/credential-name"));
	}
}