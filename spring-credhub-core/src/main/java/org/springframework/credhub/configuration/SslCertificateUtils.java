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

package org.springframework.credhub.configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods for building custom trust material for HTTP connections.
 *
 * @author Scott Frederick
 */
class SslCertificateUtils {
	X509TrustManager getDefaultX509TrustManager() {
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

			throw new IllegalStateException("Unable to setup SSL; no X509TrustManager found in: " +
					Arrays.toString(trustManagers));
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Unable to setup SSL; error getting a X509TrustManager: " +
					e.getMessage(), e);
		}
	}

	SSLContext getSSLContext(String[] caCertFiles) {
		try {
			KeyManagerFactory keyManagerFactory = createKeyManagerFactory(caCertFiles);
			TrustManagerFactory trustManagerFactory = createTrustManagerFactory(caCertFiles);

			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(),
					trustManagerFactory.getTrustManagers(),
					null);

			return sslContext;
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Error creating SSLContext: " + e.getMessage(), e);
		}
	}

	TrustManagerFactory createTrustManagerFactory(String[] caCertFiles) {
		try {
			KeyStore trustStore = loadCertificateStore(caCertFiles);

			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(trustStore);

			return trustManagerFactory;
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Error creating KeyManagerFactory: " + e.getMessage(), e);
		}
	}

	X509TrustManager createTrustManager(String[] caCertFiles) {
		TrustManager[] trustManagers = createTrustManagerFactory(caCertFiles)
				.getTrustManagers();

		if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
			throw new IllegalStateException("Unexpected default trust managers:"
					+ Arrays.toString(trustManagers));
		}

		return (X509TrustManager) trustManagers[0];
	}

	private KeyManagerFactory createKeyManagerFactory(String[] caCertFiles) {
		try {
			KeyStore keyStore = loadCertificateStore(caCertFiles);

			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, new char[0]);

			return keyManagerFactory;
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Error creating KeyManagerFactory: " + e.getMessage(), e);
		}
	}

	private KeyStore loadCertificateStore(String[] caCertFiles) {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null);

			X509Certificate[] certificates = readCertsFromFiles(caCertFiles);
			addCertsToCertificateStore(keyStore, certificates);

			return keyStore;
		} catch (GeneralSecurityException | IOException e) {
			throw new IllegalStateException("Error creating new truststore: " + e.getMessage(), e);
		}
	}

	private X509Certificate[] readCertsFromFiles(String[] caCertFiles) {
		List<X509Certificate> certs = new ArrayList<>();
		for (String fileName : caCertFiles) {
			BufferedInputStream bufferedStream = getFileStream(fileName);
			certs.add(generateCertificate(fileName, bufferedStream));
		}
		return certs.toArray(new X509Certificate[0]);
	}

	private BufferedInputStream getFileStream(String fileName) {
		try {
			FileInputStream fileStream = new FileInputStream(new File(fileName));
			return new BufferedInputStream(fileStream);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("CA cert file not found: " + fileName, e);
		}
	}

	private X509Certificate generateCertificate(String fileName, InputStream inputStream) {
		try {
			return (X509Certificate) CertificateFactory.getInstance("X.509")
					.generateCertificate(inputStream);
		} catch (CertificateException e) {
			throw new IllegalStateException("Error reading certificate from file "
					+ fileName + ": " + e.getMessage(), e);
		}
	}

	private void addCertsToCertificateStore(KeyStore keyStore, X509Certificate[] certs) {
		try {
			int count = 0;
			for (X509Certificate cert : certs) {
				keyStore.setCertificateEntry("" + count++, cert);
			}
		} catch (KeyStoreException e) {
			throw new IllegalStateException("Error creating new truststore: " + e.getMessage(), e);
		}
	}
}
