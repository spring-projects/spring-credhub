/*
 * Copyright 2016-2026 the original author or authors.
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

package org.springframework.credhub.support.utils;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.util.StdDateFormat;

import org.springframework.credhub.support.CredentialType;

/**
 * Utility methods for configuring JSON serialization and deserialization.
 *
 * @author Scott Frederick
 */
public final class JsonUtils {

	private JsonUtils() {
	}

	/**
	 * Create and configure the {@link JsonMapper} used for serializing and deserializing
	 * JSON requests and responses.
	 * @return a configured {@link JsonMapper}
	 */
	public static JsonMapper buildJsonMapper() {
		return JsonMapper.builder()
			.defaultDateFormat(new StdDateFormat())
			.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
			.changeDefaultPropertyInclusion((incl) -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY)
				.withContentInclusion(JsonInclude.Include.NON_EMPTY))
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(EnumFeature.READ_ENUMS_USING_TO_STRING, true)
			.configure(EnumFeature.WRITE_ENUMS_USING_TO_STRING, true)
			.configure(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS, true)
			.registerSubtypes(credentialDetailTypeMappings())
			.build();
	}

	/**
	 * Return type mappings for the {@literal value} field in the
	 * {@literal CredentialDetails} objects.
	 * @return array of {@link NamedType} for {@literal CredentialDetails}.
	 */
	private static NamedType[] credentialDetailTypeMappings() {
		return Arrays.stream(CredentialType.values())
			.map((type) -> new NamedType(type.getModelClass(), type.getValueType()))
			.toArray(NamedType[]::new);
	}

}
