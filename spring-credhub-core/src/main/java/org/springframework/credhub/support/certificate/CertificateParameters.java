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

package org.springframework.credhub.support.certificate;

import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.KeyLength;
import org.springframework.credhub.support.KeyParameters;
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
	private String certificateAuthorityCredential;
	private Boolean certificateAuthority;
	private Boolean selfSign;
	private Integer duration;
	private KeyUsage[] keyUsage;
	private ExtendedKeyUsage[] extendedKeyUsage;

	/**
	 * Create a {@link CertificateParameters} using defaults for all parameter values. Intended for internal use.
	 */
	@SuppressWarnings("unused")
	private CertificateParameters() {
		this.commonName = null;
		this.alternativeNames = null;
		this.organization = null;
		this.organizationUnit = null;
		this.locality = null;
		this.state = null;
		this.country = null;
		this.duration = null;
		this.certificateAuthorityCredential = null;
		this.certificateAuthority = null;
		this.selfSign = null;
		this.keyUsage = null;
		this.extendedKeyUsage = null;
	}

	/**
	 * Create a {@link CertificateParameters} using the specified parameter values. Intended for internal use.
	 */
	private CertificateParameters(KeyLength keyLength, String commonName, String[] alternativeNames, String organization,
								  String organizationUnit, String locality, String state, String country,
								  Integer duration, String certificateAuthorityCredential,
								  Boolean certificateAuthority, Boolean selfSign,
								  KeyUsage[] keyUsage, ExtendedKeyUsage[] extendedKeyUsage) {
		super(keyLength);
		this.commonName = commonName;
		this.alternativeNames = alternativeNames;
		this.organization = organization;
		this.organizationUnit = organizationUnit;
		this.locality = locality;
		this.state = state;
		this.country = country;
		this.duration = duration;
		this.certificateAuthorityCredential = certificateAuthorityCredential;
		this.certificateAuthority = certificateAuthority;
		this.selfSign = selfSign;
		this.keyUsage = keyUsage;
		this.extendedKeyUsage = extendedKeyUsage;
	}

	/**
	 * Get the value of the common name parameter that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public String getCommonName() {
		return commonName;
	}

	/**
	 * Get the value of the alternative names parameter that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public String[] getAlternativeNames() {
		return alternativeNames;
	}

	/**
	 * Get the value of the organization parameter that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * Get the value of the organization unit parameter that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public String getOrganizationUnit() {
		return organizationUnit;
	}

	/**
	 * Get the value of the locality parameter that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public String getLocality() {
		return locality;
	}

	/**
	 * Get the value of the state parameter that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public String getState() {
		return state;
	}

	/**
	 * Get the value of the country parameter that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Get the value of the certificate authority parameter that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public String getCa() {
		return certificateAuthorityCredential;
	}

	/**
	 * Get the value of the flag that indicates whether the generated certificate is a certificate authority.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public Boolean getIsCa() {
		return certificateAuthority;
	}

	/**
	 * Get the value of the flag that indicates whether the generated certificate is self-signed.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public Boolean getSelfSign() {
		return selfSign;
	}

	/**
	 * Get the value of the duration (in days) parameter that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public Integer getDuration() {
		return duration;
	}

	/**
	 * Get the value of the key usage extensions that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public KeyUsage[] getKeyUsage() {
		return keyUsage;
	}

	/**
	 * Get the value of the extended key usage extensions that will be used when generating the certificate.
	 *
	 * @return the value of the parameter; will be {@literal null} if not explicitly set
	 */
	public ExtendedKeyUsage[] getExtendedKeyUsage() {
		return extendedKeyUsage;
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
		private String certificateAuthorityCredential;
		private Boolean certificateAuthority;
		private Boolean selfSign;
		private KeyUsage[] keyUsage;
		private ExtendedKeyUsage[] extendedKeyUsage;

		/**
		 * Set the length of the key for the generated certificate.
		 *
		 * @param keyLength the parameter value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersBuilder keyLength(KeyLength keyLength) {
			Assert.notNull(keyLength, "keyLength must not be null");
			this.keyLength = keyLength;
			return this;
		}

		/**
		 * Set the Common Name (CN) field to be used for the generated certificate.
		 *
		 * @param commonName the parameter value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersBuilder commonName(String commonName) {
			Assert.notNull(commonName, "commonName must not be null");
			this.commonName = commonName;
			return this;
		}

		/**
		 * Set the Alternative Names (SAN) field to be used for the generated certificate.
		 *
		 * @param alternativeNames the parameter value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersBuilder alternateNames(String... alternativeNames) {
			Assert.notNull(alternativeNames, "alternativeNames must not be null");
			this.alternativeNames = alternativeNames;
			return this;
		}

		/**
		 * Set the Organization (O) field to be used for the generated certificate.
		 *
		 * @param organization the parameter value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersBuilder organization(String organization) {
			Assert.notNull(organization, "organization must not be null");
			this.organization = organization;
			return this;
		}

		/**
		 * Set the Organization Unit (OU) field to be used for the generated certificate.
		 *
		 * @param organizationUnit the parameter value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersBuilder organizationUnit(String organizationUnit) {
			Assert.notNull(organizationUnit, "organizationUnit must not be null");
			this.organizationUnit = organizationUnit;
			return this;
		}

		/**
		 * Set the Locality (L) field to be used for the generated certificate.
		 *
		 * @param locality the parameter value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersBuilder locality(String locality) {
			Assert.notNull(locality, "locality must not be null");
			this.locality = locality;
			return this;
		}

		/**
		 * Set the State (S) field to be used for the generated certificate.
		 *
		 * @param state the parameter value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersBuilder state(String state) {
			Assert.notNull(state, "state must not be null");
			this.state = state;
			return this;
		}

		/**
		 * Set the Country (C) field to be used for the generated certificate.
		 *
		 * @param country the parameter value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersBuilder country(String country) {
			Assert.notNull(country, "country must not be null");
			this.country = country;
			return this;
		}

		/**
		 * Set the duration in days that the generated certificate should be valid.
		 *
		 * @param duration the parameter value
		 * @return the builder
		 */
		public CertificateParametersBuilder duration(int duration) {
			this.duration = duration;
			return this;
		}

		/**
		 * Set the name of a certificate authority credential in CredHub to sign the generated certificate with.
		 *
		 * @param certificateAuthorityCredential the parameter value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersBuilder certificateAuthorityCredential(String certificateAuthorityCredential) {
			Assert.notNull(certificateAuthorityCredential, "certificateAuthorityCredential must not be null");
			this.certificateAuthorityCredential = certificateAuthorityCredential;
			return this;
		}

		/**
		 * Set the name of a certificate authority credential in CredHub to sign the generated certificate with.
		 *
		 * @param certificateAuthorityCredential the parameter value; must not be {@literal null}
		 * @return the builder
		 */
		public CertificateParametersBuilder certificateAuthorityCredential(CredentialName certificateAuthorityCredential) {
			Assert.notNull(certificateAuthorityCredential, "certificateAuthorityCredential must not be null");
			this.certificateAuthorityCredential = certificateAuthorityCredential.getName();
			return this;
		}

		/**
		 * Set the value of the flag that indicates whether the generated certificate is a
		 * certificate authority.
		 *
		 * @param certificateAuthority the parameter value
		 * @return the builder
		 */
		public CertificateParametersBuilder certificateAuthority(boolean certificateAuthority) {
			this.certificateAuthority = certificateAuthority;
			return this;
		}

		/**
		 * Set the value of the flag that indicates whether the generated certificate should be
		 * self-signed.
		 *
		 * @param selfSign the parameter value
		 * @return the builder
		 */
		public CertificateParametersBuilder selfSign(boolean selfSign) {
			this.selfSign = selfSign;
			return this;
		}

		/**
		 * Set the value of the key usage extensions for the generated certificate.
		 *
		 * @param keyUsage one or more parameter values
		 * @return the builder
		 */
		public CertificateParametersBuilder keyUsage(KeyUsage... keyUsage) {
			this.keyUsage = keyUsage;
			return this;
		}

		/**
		 * Set the value of the extended key usage extensions for the generated certificate.
		 *
		 * @param extendedKeyUsage one or more parameter values
		 * @return the builder
		 */
		public CertificateParametersBuilder extendedKeyUsage(ExtendedKeyUsage... extendedKeyUsage) {
			this.extendedKeyUsage = extendedKeyUsage;
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
			Assert.isTrue(certificateAuthorityCredential != null || certificateAuthority != null || selfSign != null,
					"at least one signing parameter must be specified");
			return new CertificateParameters(keyLength, commonName, alternativeNames, organization, organizationUnit,
					locality, state, country, duration, certificateAuthorityCredential, certificateAuthority, selfSign,
					keyUsage, extendedKeyUsage);
		}
	}
}
