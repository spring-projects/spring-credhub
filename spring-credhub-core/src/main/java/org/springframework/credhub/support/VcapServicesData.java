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

package org.springframework.credhub.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service data parsed from the {@literal VCAP_SERVICES} environment variable provided to applications
 * running on Cloud Foundry.
 *
 * If the {@literal VCAP_SERVICES} environment variable for an application contains the following:
 *
 * <pre>
 * {@code
 * "VCAP_SERVICES": {
 *   "mysql": [
 *     {
 *       "label": "mysql",
 *       "name": "mysql-db",
 *       "plan": "100mb",
 *       "tags": [ "mysql", "relational" ],
 *       "credentials": {
 *         "jdbcUrl": "jdbc:mysql://mysql-broker:3306/db?user=username\u0026password=password",
 *         "uri": "mysql://username:password@mysql-broker:3306/db?reconnect=true",
 *       }
 *     }
 *   ],
 *   "rabbitmq": [
 *     {
 *       "label": "rabbitmq",
 *       "name": "rabbit-queue",
 *       "plan": "standard",
 *       "tags": [ "rabbitmq", "messaging" ],
 *       "credentials": {
 *         "http_api_uri": "http://username:password@rabbitmq-broker:12345/api",
 *         "uri": "amqp://username:password@rabbitmq-broker/vhost",
 *       }
 *     }
 *   ]
 * }
 * }
 * </pre>
 *
 * Then the {@link VcapServicesData} data structure would expect to the equivalent of this JSON document:
 *
 * <pre>
 * {@code
 * {
 *   "mysql": [
 *     {
 *       "label": "mysql",
 *       "name": "mysql-db",
 *       "plan": "100mb",
 *       "tags": [ "mysql", "relational" ],
 *       "credentials": {
 *         "jdbcUrl": "jdbc:mysql://mysql-broker:3306/db?user=username\u0026password=password",
 *         "uri": "mysql://username:password@mysql-broker:3306/db?reconnect=true",
 *       }
 *     }
 *   ]
 *   "rabbitmq": [
 *     {
 *       "label": "rabbitmq",
 *       "name": "rabbit-queue",
 *       "plan": "standard",
 *       "tags": [ "rabbitmq", "messaging" ],
 *       "credentials": {
 *         "http_api_uri": "http://username:password@rabbitmq-broker:12345/api",
 *         "uri": "amqp://username:password@rabbitmq-broker/vhost",
 *       }
 *     }
 *   ]
 * }
 * }
 * </pre>
 *
 */
public class VcapServicesData extends HashMap<String, List<Map<String, Object>>> {
	public VcapServicesData() {
	}

	/**
	 * Initialize with the provided {@link HashMap}.
	 *
	 * @param vcapServices a {@literal HashMap} to initialize this data structure from
	 */
	public VcapServicesData(HashMap<String, List<Map<String, Object>>> vcapServices) {
		super(vcapServices);
	}
}
