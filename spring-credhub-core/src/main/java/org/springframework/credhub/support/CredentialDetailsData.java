/*
 *
 * Copyright 2013-2017 the original author or authors.
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
 *
 */

package org.springframework.credhub.support;

import java.util.Arrays;
import java.util.List;

/**
 * A collection of {@link CredentialDetails}. Clients don't typically instantiate
 * objects of this type, but will receive them in response to write and retrieve
 * requests.
 *
 * @author Scott Frederick
 */
public class CredentialDetailsData<T> {
	private List<CredentialDetails<T>> data;

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
	@SafeVarargs
	public CredentialDetailsData(CredentialDetails<T>... data) {
		this.data = Arrays.asList(data);
	}

	/**
	 * Get the collection of {@link CredentialDetails}.
	 *
	 * @return the collection of {@link CredentialDetails}
	 */
	public List<CredentialDetails<T>> getData() {
		return this.data;
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
		return "CredentialDetailData{"
				+ "data=" + data
				+ '}';
	}
}
