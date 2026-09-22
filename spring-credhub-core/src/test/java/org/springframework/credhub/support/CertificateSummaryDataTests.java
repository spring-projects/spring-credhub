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

import org.springframework.credhub.support.certificate.CertificateSummary;
import org.springframework.credhub.support.certificate.CertificateSummaryData;
import org.springframework.credhub.support.certificate.CertificateVersionSummary;

import static org.assertj.core.api.Assertions.assertThat;

class CertificateSummaryDataTests extends JsonParsingUnitTestsBase {

	@Test
	void deserializeWithCertificates() {
		String json = """
				{
					"certificates": [
					{
						"id": "2993f622-cb1e-4e00-a267-4b23c273bf3d",
						"name": "/example-certificate-1"
					},
					{
						"id": "b40d3d3b-2cf5-4a73-babd-9dceefa9b0db",
						"name": "/example-certificate-2"
					}
					]
				}
				""";

		CertificateSummaryData certificates = parseResponse(json, CertificateSummaryData.class);

		assertThat(certificates.getCertificates()).hasSize(2);
		assertThat(certificates.getCertificates()).extracting("id")
			.contains("2993f622-cb1e-4e00-a267-4b23c273bf3d", "b40d3d3b-2cf5-4a73-babd-9dceefa9b0db");
		assertThat(certificates.getCertificates()).extracting("name")
			.contains("/example-certificate-1", "/example-certificate-2");
	}

	@Test
	void deserializeWithNoCertificates() {
		String json = """
				{
					"certificates": []
				}
				""";

		CertificateSummaryData certificates = parseResponse(json, CertificateSummaryData.class);

		assertThat(certificates.getCertificates()).isEmpty();
	}

	@Test
	void deserializeWithVersionsSignedByAndSigns() {
		String json = """
				{
					"certificates": [
					{
						"id": "2993f622-cb1e-4e00-a267-4b23c273bf3d",
						"name": "/example-certificate-1",
						"signed_by": "/example-ca",
						"signs": ["/example-leaf-1", "/example-leaf-2"],
						"versions": [
						{
							"id": "aaaaaaaa-cb1e-4e00-a267-4b23c273bf3d",
							"expiry_date": "2020-09-03T18:30:11Z",
							"transitional": false,
							"certificate_authority": true,
							"self_signed": true,
							"generated": true
						}
						]
					}
					]
				}
				""";

		CertificateSummaryData certificates = parseResponse(json, CertificateSummaryData.class);

		CertificateSummary certificate = certificates.getCertificates().get(0);
		assertThat(certificate.getSignedBy()).isEqualTo("/example-ca");
		assertThat(certificate.getSigns()).containsExactly("/example-leaf-1", "/example-leaf-2");
		assertThat(certificate.getVersions()).hasSize(1);

		CertificateVersionSummary version = certificate.getVersions().get(0);
		assertThat(version.getId()).isEqualTo("aaaaaaaa-cb1e-4e00-a267-4b23c273bf3d");
		assertThat(version.getExpiryDate()).isEqualTo("2020-09-03T18:30:11Z");
		assertThat(version.isTransitional()).isFalse();
		assertThat(version.isCertificateAuthority()).isTrue();
		assertThat(version.isSelfSigned()).isTrue();
		assertThat(version.getGenerated()).isTrue();
	}

	@Test
	void equalsAndHashCodeWithEqualContent() {
		CertificateSummaryData one = new CertificateSummaryData(new CertificateSummary("id", "/example-certificate"));
		CertificateSummaryData two = new CertificateSummaryData(new CertificateSummary("id", "/example-certificate"));

		assertThat(one).isEqualTo(two);
		assertThat(one).hasSameHashCodeAs(two);
	}

	@Test
	void notEqualsWithDifferentContent() {
		CertificateSummaryData one = new CertificateSummaryData(new CertificateSummary("id", "/example-certificate"));
		CertificateSummaryData two = new CertificateSummaryData(
				new CertificateSummary("other-id", "/other-certificate"));

		assertThat(one).isNotEqualTo(two);
	}

}
