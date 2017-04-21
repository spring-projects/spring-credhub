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

package org.springframework.credhub.core;

import org.springframework.beans.factory.annotation.Value;

public class CloudFoundryAppInstanceProperties {
	@Value("${CF_INSTANCE_CERT}")
	private String instanceCertLocation;

	@Value("${CF_INSTANCE_KEY}")
	private String instanceKeyLocation;

	public CloudFoundryAppInstanceProperties() {
	}

	public CloudFoundryAppInstanceProperties(String instanceCertLocation, String instanceKeyLocation) {
		this.instanceCertLocation = instanceCertLocation;
		this.instanceKeyLocation = instanceKeyLocation;
	}

	public String getInstanceCertLocation() {
		return instanceCertLocation;
	}

	public String getInstanceKeyLocation() {
		return instanceKeyLocation;
	}
}
