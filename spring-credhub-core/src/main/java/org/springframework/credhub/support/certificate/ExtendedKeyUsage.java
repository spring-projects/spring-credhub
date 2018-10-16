package org.springframework.credhub.support.certificate;

/**
 * The types of extended key usage extensions that can be assigned to a generated
 * certificate.
 *
 * @author Scott Frederick
 */
public enum ExtendedKeyUsage {
	CLIENT_AUTH("client_auth"),
	SERVER_AUTH("server_auth"),
	CODE_SIGNING("code_signing"),
	EMAIL_PROTECTION("email_protection"),
	TIMESTAMPING("timestamping");

	private final String value;

	ExtendedKeyUsage(String value) {
		this.value = value;
	}

	/**
	 * Get the value as a {@code String}
	 *
	 * @return the mode value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return value;
	}
}
