/*
 *
 *  * Copyright 2013-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.credhub.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.util.Assert;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * A collection of {@link CredentialDetails}. Clients don't typically instantiate
 * objects of this type, but will receive them in response to write and retrieve
 * requests.
 *
 * @author Scott Frederick
 */
@JsonInclude(NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CredentialDetailsData {
	private List<CredentialDetails> data;

	/**
	 * Create a {@link CredentialDetailsData}.
	 */
	public CredentialDetailsData() {
	}

	/**
	 * Create a {@link CredentialDetailsData} from the provided parameters. Intended for internal
	 * use. Clients will get {@link CredentialDetailsData} objects populated from
	 * CredHub responses.
	 *
	 * @param data a collection of {@link CredentialDetails}
	 */
	CredentialDetailsData(List<CredentialDetails> data) {
		this.data = data;
	}

	/**
	 * Get the collection of {@link CredentialDetails}.
	 *
	 * @return the collection of {@link CredentialDetails}
	 */
	public List<CredentialDetails> getData() {
		return this.data;
	}

	/**
	 * Create a builder for a {@link CredentialDetailsData} object. Intended for internal
	 * use. Clients will get {@link CredentialDetailsData} objects populated from
	 * CredHub responses.
	 *
	 * @return the builder
	 */
	public static CredentialDetailsDataBuilder builder() {
		return new CredentialDetailsDataBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CredentialDetailsData))
			return false;
		if (!super.equals(o))
			return false;

		CredentialDetailsData that = (CredentialDetailsData) o;

		return data != null ? data.equals(that.data) : that.data == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (data != null ? data.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "CredentialDetailResponse{"
				+ "data=" + data
				+ '}';
	}

	/**
	 * A builder that provides a fluent API for constructing {@link CredentialDetailsData}
	 * instances. Intended to be used internally for testing.
	 */
	public static class CredentialDetailsDataBuilder {
		private List<CredentialDetails> data;

		/**
		 * Create a {@link CredentialDetailsDataBuilder}.
		 */
		CredentialDetailsDataBuilder() {
		}

		/**
		 * Add a {@link CredentialDetails} to the collection of details.
		 *
		 * @param datum a {@link CredentialDetails} to add; must not be
		 * {@literal null}
		 * @return the builder
		 */
		public CredentialDetailsDataBuilder datum(CredentialDetails datum) {
			Assert.notNull(datum, "datum must not be null");
			initData();
			this.data.add(datum);
			return this;
		}

		/**
		 * Add a collection of {@link CredentialDetails} to the collection of details.
		 *
		 * @param data a collection of {@link CredentialDetails} to add;
		 * must not be {@literal null}
		 * @return the builder
		 */
		public CredentialDetailsDataBuilder data(Collection<? extends CredentialDetails> data) {
			Assert.notNull(data, "data must not be null");
			initData();
			this.data.addAll(data);
			return this;
		}

		private void initData() {
			if (this.data == null) {
				this.data = new ArrayList<CredentialDetails>();
			}
		}

		/**
		 * Construct a {@link CredentialDetailsData} from the provided values.
		 *
		 * @return a {@link CredentialDetailsData}
		 */
		public CredentialDetailsData build() {
			List<CredentialDetails> data;
			switch (this.data == null ? 0 : this.data.size()) {
			case 0:
				data = java.util.Collections.emptyList();
				break;
			case 1:
				data = java.util.Collections.singletonList(this.data.get(0));
				break;
			default:
				data = java.util.Collections
						.unmodifiableList(new ArrayList<CredentialDetails>(this.data));
			}

			return new CredentialDetailsData(data);
		}
	}
}
