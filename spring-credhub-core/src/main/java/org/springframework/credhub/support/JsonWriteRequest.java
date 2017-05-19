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

import org.springframework.util.Assert;

import java.util.Map;

import static org.springframework.credhub.support.ValueType.JSON;

/**
 * The details of a request to write a new or update an existing JSON credential in CredHub.
 *
 * @author Scott Frederick
 */
public class JsonWriteRequest extends WriteRequest<JsonCredential> {
	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link JsonWriteRequest}.
	 *
	 * @return a builder
	 */
	public static JsonWriteRequestBuilder builder() {
		return new JsonWriteRequestBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link JsonWriteRequest}s.
	 */
	public static class JsonWriteRequestBuilder extends
			WriteRequestBuilder<JsonCredential, JsonWriteRequest, JsonWriteRequestBuilder> {

		@Override
		protected JsonWriteRequest createTarget() {
			return new JsonWriteRequest();
		}

		@Override
		protected JsonWriteRequestBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the value of a JSON credential. A JSON credential consists of
		 * one or more fields in a JSON document. The provided {@literal Map} parameter.
		 * will be converted to a JSON document before sending to CredHub. The type of
		 * the credential is set to {@link ValueType#JSON}.
		 *
		 * @param value the credential value; must not be {@literal null}
		 * @return the builder
		 */
		public JsonWriteRequestBuilder value(JsonCredential value) {
			Assert.notNull(value, "value must not be null");
			targetObj.setType(JSON);
			targetObj.setValue(value);
			return this;
		}

		public JsonWriteRequestBuilder value(Map<String, Object> value) {
			value(new JsonCredential(value));
			return this;
		}
	}

}
