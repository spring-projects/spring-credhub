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

package org.springframework.credhub.cloud;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.cloud.cloudfoundry.CloudFoundryRawServiceData;
import org.springframework.credhub.core.CredHubException;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.VcapServicesData;
import org.springframework.http.HttpStatus;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CredHubInterpolationServiceDataPostProcessorTests {
	@Mock
	private CredHubOperations credHubOperations;

	@Test
	public void processServiceData() {
		CloudFoundryRawServiceData rawServiceData = buildRawServiceData();
		VcapServicesData interpolatedServiceData = buildInterpolatedServiceData();

		when(credHubOperations.interpolateServiceData(argThat(matchesContent(rawServiceData))))
				.thenReturn(interpolatedServiceData);

		CredHubInterpolationServiceDataPostProcessor processor =
				new CredHubInterpolationServiceDataPostProcessor(credHubOperations);

		CloudFoundryRawServiceData actual = processor.process(rawServiceData);
		assertThat(actual, matchesContent(interpolatedServiceData));
	}

	@Test
	public void processServiceDataWithCredHubError() {
		CloudFoundryRawServiceData rawServiceData = buildRawServiceData();

		when(credHubOperations.interpolateServiceData(argThat(matchesContent(rawServiceData))))
				.thenThrow(new CredHubException(HttpStatus.UNAUTHORIZED));

		CredHubInterpolationServiceDataPostProcessor processor =
				new CredHubInterpolationServiceDataPostProcessor(credHubOperations);

		CloudFoundryRawServiceData actual = processor.process(rawServiceData);
		assertThat(actual, equalTo(rawServiceData));
	}

	@Test
	public void processServiceDataWithInitializationError() {
		CredHubInterpolationServiceDataPostProcessor processor =
				new CredHubInterpolationServiceDataPostProcessor(null);

		processor.process(new CloudFoundryRawServiceData());

		verifyZeroInteractions(credHubOperations);
	}

	private ArgumentMatcher<VcapServicesData> matchesContent(final CloudFoundryRawServiceData expected) {
		return new ArgumentMatcher<VcapServicesData>() {
			@Override
			public boolean matches(VcapServicesData actual) {
				return mapsAreEquivalent(actual, expected);
			}
		};
	}

	private Matcher<CloudFoundryRawServiceData> matchesContent(final VcapServicesData expected) {
		return new BaseMatcher<CloudFoundryRawServiceData>() {
			@Override
			@SuppressWarnings("unchecked")
			public boolean matches(Object actual) {
				return mapsAreEquivalent((Map<String, ?>) actual, expected);
			}

			@Override
			public void describeMismatch(Object item, Description mismatchDescription) {
			}

			@Override
			public void describeTo(Description description) {
			}
		};
	}

	private boolean mapsAreEquivalent(Map<String, ?> actual, Map<String, ?> expected) {
		return expected.equals(actual);
	}

	private CloudFoundryRawServiceData buildRawServiceData() {
		HashMap<String, String> credentials = new HashMap<String, String>() {
			{
				put("credhub-ref",
						"((/c/service-broker/service-offering/1111-2222-3333-4444/credentials))");
			}
		};

		HashMap<String, List<Map<String, Object>>> vcapServices = buildVcapServices(credentials);

		return new CloudFoundryRawServiceData(vcapServices);
	}

	private VcapServicesData buildInterpolatedServiceData() {
		HashMap<String, String> credentials = new HashMap<String, String>() {
			{
				put("uri", "https://example.com");
				put("username", "user");
				put("password", "secret");
			}
		};

		HashMap<String, List<Map<String, Object>>> vcapServices = buildVcapServices(credentials);

		return new VcapServicesData(vcapServices);
	}

	private HashMap<String, List<Map<String, Object>>> buildVcapServices(final HashMap<String, String> credentials) {
		return new HashMap<String, List<Map<String, Object>>>() {
				{
					put("service-offering", Collections.<Map<String, Object>> singletonList(
							new HashMap<String, Object>() {
								{
									put("credentials", credentials);
									put("label", "service-offering");
									put("name", "service-instance");
									put("plan", "standard");
								}
							}));
				}
			};
	}

}