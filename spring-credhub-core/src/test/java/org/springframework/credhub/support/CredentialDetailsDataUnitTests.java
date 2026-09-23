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

package org.springframework.credhub.support;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CredentialDetailsDataUnitTests {

	@Test
	void equalsAndHashCodeWithEqualContent() {
		CredentialDetails<String> detail = new CredentialDetails<>("id",
				new SimpleCredentialName("example", "credential"), CredentialType.VALUE, "value");

		CredentialDetailsData<String> one = new CredentialDetailsData<>(detail);
		CredentialDetailsData<String> two = new CredentialDetailsData<>(detail);

		assertThat(one).isEqualTo(two);
		assertThat(one).hasSameHashCodeAs(two);
	}

	@Test
	void notEqualsWithDifferentContent() {
		CredentialDetailsData<String> one = new CredentialDetailsData<>(new CredentialDetails<>("id",
				new SimpleCredentialName("example", "credential"), CredentialType.VALUE, "value"));
		CredentialDetailsData<String> two = new CredentialDetailsData<>(new CredentialDetails<>("id",
				new SimpleCredentialName("other", "credential"), CredentialType.VALUE, "value"));

		assertThat(one).isNotEqualTo(two);
	}

	@Test
	void toStringUsesCorrectClassName() {
		CredentialDetailsData<String> data = new CredentialDetailsData<>();

		assertThat(data.toString()).startsWith("CredentialDetailsData{");
	}

}
