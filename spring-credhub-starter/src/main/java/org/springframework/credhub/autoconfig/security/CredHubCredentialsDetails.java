package org.springframework.credhub.autoconfig.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Qualifies a {@link ClientCredentialsResourceDetails} used by Spring CredHub.
 *
 * @author Scott Frederick
 */
@Qualifier
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CredHubCredentialsDetails {
}
