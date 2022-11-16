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

package org.springframework.credhub.configuration;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.JdkSslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.netty.http.client.HttpClient;

import org.springframework.credhub.support.ClientOptions;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

/**
 * Factory for {@link ClientHttpConnector} that supports
 * {@link ReactorClientHttpConnector}.
 *
 * @author Mark Paluch
 * @author Scott Frederick
 */
public final class ClientHttpConnectorFactory {

	private static final Log logger = LogFactory.getLog(ClientHttpConnectorFactory.class);

	private static final SslCertificateUtils sslCertificateUtils = new SslCertificateUtils();

	private ClientHttpConnectorFactory() {
	}

	/**
	 * Create a {@link ClientHttpConnector} for the given {@link ClientOptions}.
	 * @param options must not be {@literal null}
	 * @return a new {@link ClientHttpConnector}.
	 */
	public static ClientHttpConnector create(ClientOptions options) {
		HttpClient httpClient = HttpClient.create();

		if (usingCustomCerts(options)) {
			TrustManagerFactory trustManagerFactory = sslCertificateUtils
					.createTrustManagerFactory(options.getCaCertFiles());

			httpClient = httpClient.secure((sslContextSpec) -> {
				try {
					sslContextSpec.sslContext(SslContextBuilder.forClient().sslProvider(SslProvider.JDK)
							.trustManager(trustManagerFactory).build());
				}
				catch (SSLException ex) {
					logger.error("Error configuring HTTP connections using custom certs", ex);
					throw new RuntimeException("Error configuring HTTP connections using custom certs", ex);
				}
			});
		}
		else {
			httpClient = httpClient.secure((sslContextSpec) -> {
				try {
					sslContextSpec.sslContext(new JdkSslContext(SSLContext.getDefault(), true, null,
							IdentityCipherSuiteFilter.INSTANCE, null, ClientAuth.REQUIRE, null, false));
				}
				catch (NoSuchAlgorithmException ex) {
					logger.error("Error configuring HTTP connections", ex);
					throw new RuntimeException("Error configuring HTTP connections", ex);
				}
			});
		}

		if (options.getConnectionTimeout() != null) {
			httpClient = httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
					Math.toIntExact(options.getConnectionTimeout().toMillis()));
		}

		return new ReactorClientHttpConnector(httpClient);
	}

	private static boolean usingCustomCerts(ClientOptions options) {
		return options.getCaCertFiles() != null;
	}

}
