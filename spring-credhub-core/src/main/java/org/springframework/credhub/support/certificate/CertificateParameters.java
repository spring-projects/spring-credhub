/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.support.certificate;

import org.springframework.credhub.support.KeyLength;
import org.springframework.credhub.support.rsa.KeyParameters;
import org.springframework.util.Assert;

/**
 * Parameters for generating a new certificate credential.
 *
 * @author Scott Frederick
 */
public class CertificateParameters extends KeyParameters {
	private String commonName;
	private String[] alternativeNames;
	private String organization;
	private String organizationUnit;
	private String locality;
	private String state;
	private String country;
	private String credential;
	private Boolean isCertificateAuthority;
	private Boolean selfSign;
	private Integer duration;

	/**
	 * Create a {@link CertificateParameters} using defaults for all parameter values.
	 */
	private CertificateParameters() {
		this.commonName = null;
		this.alternativeNames = null;
		this.organization = null;
		this.organizationUnit = null;
		this.locality = null;
		this.state = null;
		this.country = null;
		this.duration = null;
		this.credential = null;
		this.isCertificateAuthority = null;
		this.selfSign = null;
	}

	private CertificateParameters(KeyLength keyLength, String commonName, String[] alternativeNames, String organization,
								 String organizationUnit, String locality, String state, String country,
								 Integer duration, String credential, Boolean isCa, Boolean selfSign) {
		super(keyLength);
		this.commonName = commonName;
		this.alternativeNames = alternativeNames;
		this.organization = organization;
		this.organizationUnit = organizationUnit;
		this.locality = locality;
		this.state = state;
		this.country = country;
		this.duration = duration;
		this.credential = credential;
		this.isCertificateAuthority = isCa;
		this.selfSign = selfSign;
	}

	public String getCommonName() {
		return commonName;
	}

	public String[] getAlternativeNames() {
		return alternativeNames;
	}

	public String getOrganization() {
		return organization;
	}

	public String getOrganizationUnit() {
		return organizationUnit;
	}

	public String getLocality() {
		return locality;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getCredential() {
		return credential;
	}

	public Boolean getIsCa() {
		return isCertificateAuthority;
	}

	public Boolean getSelfSign() {
		return selfSign;
	}

	public Integer getDuration() {
		return duration;
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link CertificateParameters}.
	 *
	 * @return a builder
	 */
	public static CertificateParametersBuilder builder() {
		return new CertificateParametersBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link CertificateParametersBuilder}s.
	 */
	public static class CertificateParametersBuilder {
		private KeyLength keyLength;
		private String commonName;
		private String[] alternativeNames;
		private String organization;
		private String organizationUnit;
		private String locality;
		private String state;
		private String country;
		private Integer duration;
		private String credential;
		private Boolean certificateAuthority;
		private Boolean selfSign;

		public CertificateParametersBuilder keyLength(KeyLength keyLength) {
			Assert.notNull(keyLength, "keyLength must not be null");
			this.keyLength = keyLength;
			return this;
		}

		public CertificateParametersBuilder commonName(String commonName) {
			Assert.notNull(commonName, "commonName must not be null");
			this.commonName = commonName;
			return this;
		}

		public CertificateParametersBuilder alternateNames(String... alternativeNames) {
			Assert.notNull(alternativeNames, "alternativeNames must not be null");
			this.alternativeNames = alternativeNames;
			return this;
		}

		public CertificateParametersBuilder organization(String organization) {
			Assert.notNull(organization, "organization must not be null");
			this.organization = organization;
			return this;
		}

		public CertificateParametersBuilder organizationUnit(String organizationUnit) {
			Assert.notNull(organizationUnit, "organizationUnit must not be null");
			this.organizationUnit = organizationUnit;
			return this;
		}

		public CertificateParametersBuilder locality(String locality) {
			Assert.notNull(locality, "locality must not be null");
			this.locality = locality;
			return this;
		}

		public CertificateParametersBuilder state(String state) {
			Assert.notNull(state, "state must not be null");
			this.state = state;
			return this;
		}

		public CertificateParametersBuilder country(String country) {
			Assert.notNull(country, "country must not be null");
			this.country = country;
			return this;
		}

		public CertificateParametersBuilder duration(int duration) {
			this.duration = duration;
			return this;
		}

		public CertificateParametersBuilder credential(String credential) {
			this.credential = credential;
			return this;
		}

		public CertificateParametersBuilder certificateAuthority(boolean certificateAuthority) {
			this.certificateAuthority = certificateAuthority;
			return this;
		}

		public CertificateParametersBuilder selfSign(boolean selfSign) {
			this.selfSign = selfSign;
			return this;
		}

		/**
		 * Create a {@link CertificateParameters} from the provided values.
		 *
		 * @return the created {@link CertificateParameters}
		 */
		public CertificateParameters build() {
			Assert.isTrue(commonName != null || organization != null || organizationUnit != null ||
					locality != null || state != null || country != null,
					"at least one subject parameter must be specified");
			Assert.isTrue(credential != null || certificateAuthority != null || selfSign != null,
					"at least one signing parameter must be specified");
			return new CertificateParameters(keyLength, commonName, alternativeNames, organization, organizationUnit,
					locality, state, country, duration, credential, certificateAuthority, selfSign);
		}
	}
}
