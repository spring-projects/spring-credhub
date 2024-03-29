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

package org.springframework.credhub.support.info;

import org.junit.jupiter.api.Test;

import org.springframework.credhub.support.JsonParsingUnitTestsBase;

import static org.assertj.core.api.Assertions.assertThat;

public class VersionInfoTests extends JsonParsingUnitTestsBase {

	@Test
	public void deserializeWithV1() {
		String json = """
				{
					"version": "1.9.0"
				}
				""";

		VersionInfo versionInfo = parseResponse(json, VersionInfo.class);

		assertThat(versionInfo.getVersion()).isEqualTo("1.9.0");
		assertThat(versionInfo.isVersion1()).isTrue();
		assertThat(versionInfo.isVersion2()).isFalse();
		assertThat(versionInfo.isVersion2_0()).isFalse();
		assertThat(versionInfo.isVersion2_1()).isFalse();
	}

	@Test
	public void deserializeWithV2_0() {
		String json = """
				{
					"version": "2.0.2"
				}
				""";

		VersionInfo versionInfo = parseResponse(json, VersionInfo.class);

		assertThat(versionInfo.getVersion()).isEqualTo("2.0.2");
		assertThat(versionInfo.isVersion1()).isFalse();
		assertThat(versionInfo.isVersion2()).isTrue();
		assertThat(versionInfo.isVersion2_0()).isTrue();
		assertThat(versionInfo.isVersion2_1()).isFalse();
	}

	@Test
	public void deserializeWithV2_1() {
		String json = """
				{
					"version": "2.1.2"
				}""";

		VersionInfo versionInfo = parseResponse(json, VersionInfo.class);

		assertThat(versionInfo.getVersion()).isEqualTo("2.1.2");
		assertThat(versionInfo.isVersion1()).isFalse();
		assertThat(versionInfo.isVersion2()).isTrue();
		assertThat(versionInfo.isVersion2_0()).isFalse();
		assertThat(versionInfo.isVersion2_1()).isTrue();
	}

}
