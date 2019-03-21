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

package org.springframework.credhub.support;

import org.junit.Test;
import org.springframework.credhub.support.certificate.CertificateSummaryData;

import static org.assertj.core.api.Assertions.assertThat;

public class CertificateSummaryDataTests extends JsonParsingUnitTestsBase {
	@Test
	public void deserializeWithCertificates() {
		String json = "{\n" +
				"  \"certificates\": [\n" +
				"    {\n" +
				"      \"id\": \"2993f622-cb1e-4e00-a267-4b23c273bf3d\",\n" +
				"      \"name\": \"/example-certificate-1\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"id\": \"b40d3d3b-2cf5-4a73-babd-9dceefa9b0db\",\n" +
				"      \"name\": \"/example-certificate-2\"\n" +
				"    }\n" +
				"  ]\n" +
				"}";

		CertificateSummaryData certificates = parseResponse(json, CertificateSummaryData.class);

		assertThat(certificates.getCertificates().size()).isEqualTo(2);
		assertThat(certificates.getCertificates()).extracting("id")
				.contains("2993f622-cb1e-4e00-a267-4b23c273bf3d", "b40d3d3b-2cf5-4a73-babd-9dceefa9b0db");
		assertThat(certificates.getCertificates()).extracting("name")
				.contains("/example-certificate-1", "/example-certificate-2");
	}

	@Test
	public void deserializeWithNoCertificates() {
		String json = "{\n" +
				"  \"certificates\": []\n" +
				"}";

		CertificateSummaryData certificates = parseResponse(json, CertificateSummaryData.class);

		assertThat(certificates.getCertificates().size()).isEqualTo(0);
	}
}