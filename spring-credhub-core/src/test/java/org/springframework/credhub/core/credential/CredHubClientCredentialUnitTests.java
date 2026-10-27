/*
 * Copyright 2016-2026 the original author or authors.
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

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpMethod;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;

class CredHubClientCredentialUnitTests extends CredHubClientCredentialUnitTestsBase {

	@Test
	void deleteByName() {
		this.server.expect(requestTo(BASE_URL + "/api/v1/data?name=" + encodedName()))
			.andExpect(method(HttpMethod.DELETE))
			.andRespond(withNoContent());

		this.credentials.deleteByName(NAME);
	}

}
