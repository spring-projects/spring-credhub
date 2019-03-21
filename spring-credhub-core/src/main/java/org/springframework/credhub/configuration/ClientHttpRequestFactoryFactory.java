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

package org.springframework.credhub.configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.JdkSslContext;
import io.netty.handler.ssl.SslContext;
import okhttp3.OkHttpClient.Builder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import org.springframework.credhub.support.ClientOptions;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Factory for {@link ClientHttpRequestFactory} that supports Apache HTTP Components,
 * OkHttp, Netty and the JDK HTTP client (in that order). This factory configures a
 * {@link ClientHttpRequestFactory} depending on the available dependencies.
 *
 * @author Mark Paluch
 * @author Scott Frederick
 */
public class ClientHttpRequestFactoryFactory {
	private static final Log logger = LogFactory.getLog(ClientHttpRequestFactoryFactory.class);

	private static final boolean HTTP_COMPONENTS_PRESENT = ClassUtils.isPresent(
			"org.apache.http.client.HttpClient",
			ClientHttpRequestFactoryFactory.class.getClassLoader());

	private static final boolean OKHTTP3_PRESENT = ClassUtils.isPresent(
			"okhttp3.OkHttpClient",
			ClientHttpRequestFactoryFactory.class.getClassLoader());

	private static final boolean NETTY_PRESENT = ClassUtils.isPresent(
			"io.netty.channel.nio.NioEventLoopGroup",
			ClientHttpRequestFactoryFactory.class.getClassLoader());

	/**
	 * Create a {@link ClientHttpRequestFactory} for the given {@link ClientOptions}.
	 *
	 * @param options must not be {@literal null}
	 * @return a new {@link ClientHttpRequestFactory}. Lifecycle beans must be initialized
	 * after obtaining.
	 */
	public static ClientHttpRequestFactory create(ClientOptions options) {

		Assert.notNull(options, "ClientOptions must not be null");

		try {
			if (HTTP_COMPONENTS_PRESENT) {
				logger.info("Using Apache HttpComponents HttpClient for HTTP connections");
				return HttpComponents.usingHttpComponents(options);
			}

			if (OKHTTP3_PRESENT) {
				logger.info("Using OkHttp3 for HTTP connections");
				return OkHttp3.usingOkHttp3(options);
			}

			if (NETTY_PRESENT) {
				logger.info("Using Netty for HTTP connections");
				return Netty.usingNetty(options);
			}
		}
		catch (Exception e) {
			logger.warn("Exception caught while configuring HTTP connections", e);
		}

		logger.info("Defaulting to java.net.HttpUrlConnection for HTTP connections");
		return HttpURLConnection.usingJdk(options);
	}

	/**
	 * {@link ClientHttpRequestFactory} using {@link java.net.HttpURLConnection}.
	 */
	static class HttpURLConnection {
		static ClientHttpRequestFactory usingJdk(ClientOptions options) {
			SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
			
			if (options.getConnectionTimeout() != null) {
				factory.setConnectTimeout(options.getConnectionTimeout());
			}
			if (options.getReadTimeout() != null) {
				factory.setReadTimeout(options.getReadTimeout());
			}

			return factory;
		}
	}

	/**
	 * {@link ClientHttpRequestFactory} using Apache HttpComponents.
	 *
	 * @author Mark Paluch
	 * @author Scott Frederick
	 */
	static class HttpComponents {
		static ClientHttpRequestFactory usingHttpComponents(ClientOptions options)
				throws GeneralSecurityException, IOException {

			HttpClientBuilder httpClientBuilder = HttpClients.custom()
					.setSSLContext(SSLContext.getDefault())
					.useSystemProperties();

			RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
					.setAuthenticationEnabled(true);

			if (options.getConnectionTimeout() != null) {
				requestConfigBuilder.setConnectTimeout(options.getConnectionTimeout());
			}
			if (options.getReadTimeout() != null) {
				requestConfigBuilder.setSocketTimeout(options.getReadTimeout());
			}

			httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());

			return new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
		}
	}

	/**
	 * {@link ClientHttpRequestFactory} using {@link OkHttp3}.
	 *
	 * @author Mark Paluch
	 * @author Scott Frederick
	 */
	static class OkHttp3 {
		static ClientHttpRequestFactory usingOkHttp3(ClientOptions options)
				throws IOException, GeneralSecurityException {

			SSLSocketFactory socketFactory = SSLContext.getDefault().getSocketFactory();
			X509TrustManager trustManager = getTrustManager();

			Builder builder = new Builder().sslSocketFactory(socketFactory, trustManager);

			if (options.getConnectionTimeout() != null) {
				builder.connectTimeout(options.getConnectionTimeout(), TimeUnit.MILLISECONDS);
			}
			if (options.getReadTimeout() != null) {
				builder.readTimeout(options.getReadTimeout(), TimeUnit.MILLISECONDS);
			}

			return new OkHttp3ClientHttpRequestFactory(builder.build());
		}

		private static X509TrustManager getTrustManager() {
			try {
				TrustManagerFactory trustManagerFactory =
						TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				trustManagerFactory.init((KeyStore) null);

				TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
				
				for (TrustManager trustManager : trustManagers) {
					if (trustManager instanceof X509TrustManager) {
						return (X509TrustManager) trustManager;
					}
				}

				throw new IllegalStateException("Unable to setup SSL with OkHttp3; no X509TrustManager found in: " +
						Arrays.toString(trustManagers));
			} catch (GeneralSecurityException e) {
				throw new IllegalStateException("Unable to setup SSL with OkHttp3; error getting a X509TrustManager: " +
						e.getMessage(), e);
			}
		}
	}

	/**
	 * {@link ClientHttpRequestFactory} using Netty.
	 *
	 * @author Mark Paluch
	 * @author Scott Frederick
	 */
	static class Netty {

		static ClientHttpRequestFactory usingNetty(ClientOptions options)
				throws IOException, GeneralSecurityException {

			SslContext sslContext = new JdkSslContext(SSLContext.getDefault(), true, ClientAuth.REQUIRE);

			final Netty4ClientHttpRequestFactory requestFactory = new Netty4ClientHttpRequestFactory();
			requestFactory.setSslContext(sslContext);

			if (options.getConnectionTimeout() != null) {
				requestFactory.setConnectTimeout(options.getConnectionTimeout());
			}
			if (options.getReadTimeout() != null) {
				requestFactory.setReadTimeout(options.getReadTimeout());
			}

			return requestFactory;
		}
	}
}