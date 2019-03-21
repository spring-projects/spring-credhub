/*
 * Copyright 2017-2018 the original author or authors.
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

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;

/**
 * Factory for {@link ClientHttpConnector} that supports {@link ReactorClientHttpConnector}.
 *
 * @author Mark Paluch
 * @author Scott Frederick
 */
public class ClientHttpConnectorFactory {

	private static SslCertificateUtils sslCertificateUtils = new SslCertificateUtils();

	/**
	 * Create a {@link ClientHttpConnector} for the given {@link ClientOptions}.
	 *
	 * @param options must not be {@literal null}
	 * @return a new {@link ClientHttpConnector}.
	 */
	public static ClientHttpConnector create(ClientOptions options) {
		HttpClient httpClient = HttpClient.create();

		if (usingCustomCerts(options)) {
			TrustManagerFactory trustManagerFactory =
					sslCertificateUtils.createTrustManagerFactory(options.getCaCertFiles());

			httpClient = httpClient.secure(sslContextSpec -> sslContextSpec
					.sslContext(SslContextBuilder.forClient()
							.sslProvider(SslProvider.JDK)
							.trustManager(trustManagerFactory)));
		} else {
			httpClient = httpClient.secure(sslContextSpec -> sslContextSpec
					.sslContext(SslContextBuilder.forClient()
							.sslProvider(SslProvider.JDK)));
		}

		if (options.getConnectionTimeout() != null) {
			httpClient = httpClient.tcpConfiguration(tcpClient ->
					tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
							Math.toIntExact(options.getConnectionTimeout().toMillis())));
		}

		return new ReactorClientHttpConnector(httpClient);
	}

	private static boolean usingCustomCerts(ClientOptions options) {
		return options.getCaCertFiles() != null;
	}
}
