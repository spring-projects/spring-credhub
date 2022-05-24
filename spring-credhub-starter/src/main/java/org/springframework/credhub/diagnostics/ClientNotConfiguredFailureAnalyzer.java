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

package org.springframework.credhub.diagnostics;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;

/**
 * A {@link FailureAnalyzer} that detects the condition when Spring CredHub has been
 * configured with an OAuth2 client but beans required for this configuration are missing.
 *
 * @author Scott Frederick
 */
class ClientNotConfiguredFailureAnalyzer extends AbstractFailureAnalyzer<NoSuchBeanDefinitionException> {

	private final BeanFactory beanFactory;

	ClientNotConfiguredFailureAnalyzer(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	protected FailureAnalysis analyze(Throwable rootFailure, NoSuchBeanDefinitionException cause) {
		if (hasCredHubProperties() && isMissingSpringSecurityOAuth2Bean(cause.getBeanType())) {
			return new FailureAnalysis(getDescription(), getAction(), cause);
		}
		return null;
	}

	private boolean hasCredHubProperties() {
		return this.beanFactory.containsBean("credHubProperties");
	}

	private boolean isMissingSpringSecurityOAuth2Bean(Class<?> notFoundBean) {
		return notFoundBean != null && (notFoundBean.getName().contains("ClientRegistrationRepository")
				|| notFoundBean.getName().contains("OAuth2AuthorizedClientRepository"));
	}

	private String getDescription() {
		return "A CredHub OAuth2 client registration is configured "
				+ "but Spring Security is not available or the Spring Security OAuth2 "
				+ "client registration not configured correctly.";
	}

	private String getAction() {
		return "Add Spring Security to the application classpath and configure properties for the "
				+ "OAuth2 client registration under 'spring.security.oauth2.client.registration'.";
	}

}
