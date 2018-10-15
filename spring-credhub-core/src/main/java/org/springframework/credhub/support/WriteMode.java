package org.springframework.credhub.support;

/**
 * The acceptable values for the {@code mode} parameter on a set or generate request,
 * indicating the action CredHub should take when the credential being set or generated
 * already exists.
 *
 * @author Scott Frederick
 */
public enum WriteMode {
	/**
	 * Indicates that CredHub should not replace the value of a credential
	 * if the credential exists
	 */
	NO_OVERWRITE("no-overwrite"),

	/**
	 * Indicates that CredHub should replace any existing credential
	 * value with a new value
	 */
	OVERWRITE("overwrite"),

	/**
	 * Indicates that CredHub should replace any existing credential
	 * value with a new value only if generation parameters are different
	 * from the original generation parameters
	 */
	CONVERGE("converge");

	private final String mode;

	WriteMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Get the {@code mode} value as a {@code String}
	 *
	 * @return the mode value
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return mode;
	}
}
