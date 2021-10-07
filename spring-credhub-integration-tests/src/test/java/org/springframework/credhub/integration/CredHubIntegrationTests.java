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

package org.springframework.credhub.integration;

import org.junit.Before;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.info.VersionInfo;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestApplication.class })
@ActiveProfiles("test")
public abstract class CredHubIntegrationTests {

	@Value("${test-server-version:}")
	private String serverVersion;

	private VersionInfo versionInfo;

	@Autowired
	protected CredHubOperations operations;

	@Before
	public void setupVersionInfo() {
		if (StringUtils.hasText(this.serverVersion)) {
			this.versionInfo = new VersionInfo(this.serverVersion);
		}
		else {
			this.versionInfo = this.operations.info().version();
		}
	}

	boolean serverApiIsV1() {
		return this.versionInfo.isVersion1();
	}

	boolean serverApiIsV2() {
		return this.versionInfo.isVersion2();
	}

	void deleteCredentialIfExists(CredentialName credentialName) {
		try {
			this.operations.credentials().deleteByName(credentialName);
		}
		catch (CredHubException ex) {
			// ignore failing deletes on cleanup
		}
	}

}
