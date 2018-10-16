package org.springframework.credhub.support.certificate;

/**
 * The types of key usage extensions that can be assigned to a generated
 * certificate.
 *
 * @author Scott Frederick
 */
public enum KeyUsage {
	DIGITAL_SIGNATURE("digital_signature"),
	NON_REPUDIATION("non_repudiation"),
	KEY_ENCIPHERMENT("key_encipherment"),
	DATA_ENCIPHERMENT("data_encipherment"),
	KEY_AGREEMENT("key_agreement"),
	KEY_CERT_SIGN("key_cert_sign"),
	CRL_SIGN("crl_sign"),
	ENCIPHER_ONLY("encipher_only"),
	DECIPHER_ONLY("decipher_only");
	
	private final String value;

	KeyUsage(String value) {
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
