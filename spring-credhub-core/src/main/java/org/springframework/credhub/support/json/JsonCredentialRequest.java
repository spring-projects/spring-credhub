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

package org.springframework.credhub.support.json;

import org.springframework.credhub.support.CredentialRequest;
import org.springframework.util.Assert;

import java.util.Map;

import static org.springframework.credhub.support.CredentialType.JSON;

/**
 * The details of a request to write a new or update an existing {@link JsonCredential} in CredHub.
 *
 * @author Scott Frederick
 */
public class JsonCredentialRequest extends CredentialRequest<JsonCredential> {
	/**
	 * Initialize a {@link CredentialRequest}.
	 */
	JsonCredentialRequest() {
		super(JSON);
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link JsonCredentialRequest}.
	 *
	 * @return a builder
	 */
	public static JsonCredentialRequestBuilder builder() {
		return new JsonCredentialRequestBuilder();
	}

	/**
	 * A builder that provides a fluent API for constructing {@link JsonCredentialRequest}s.
	 */
	public static class JsonCredentialRequestBuilder extends
			CredHubRequestBuilder<JsonCredential, JsonCredentialRequest, JsonCredentialRequestBuilder> {

		@Override
		protected JsonCredentialRequest createTarget() {
			return new JsonCredentialRequest();
		}

		@Override
		protected JsonCredentialRequestBuilder createBuilder() {
			return this;
		}

		/**
		 * Set the value of a JSON credential.
		 *
		 * @param value the credential value; must not be {@literal null}
		 * @return the builder
		 */
		public JsonCredentialRequestBuilder value(JsonCredential value) {
			Assert.notNull(value, "value must not be null");
			targetObj.setValue(value);
			return this;
		}

		public JsonCredentialRequestBuilder value(Map<String, Object> value) {
			value(new JsonCredential(value));
			return this;
		}
	}

}
