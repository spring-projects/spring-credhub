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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonInclude(NON_EMPTY)
public class CredentialDataResponse extends CredHubResponse {
	private List<CredentialData> data;

	public CredentialDataResponse() {
	}

	CredentialDataResponse(String errorMessage) {
		super(errorMessage);
	}

	CredentialDataResponse(List<CredentialData> data) {
		super(null);
		this.data = data;
	}

	CredentialDataResponse(String errorMessage, List<CredentialData> data) {
		super(errorMessage);
		this.data = data;
	}

	public static CredentialDataResponseBuilder builder() {
		return new CredentialDataResponseBuilder();
	}

	public List<CredentialData> getData() {
		return this.data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CredentialDataResponse)) return false;
		if (!super.equals(o)) return false;

		CredentialDataResponse that = (CredentialDataResponse) o;

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
		return "CredentialDataResponse{" +
				"errorMessage=" + errorMessage +
				", data=" + data +
				'}';
	}

	public static class CredentialDataResponseBuilder {
		private String errorMessage;
		private ArrayList<CredentialData> data;

		CredentialDataResponseBuilder() {
		}

		public CredentialDataResponse.CredentialDataResponseBuilder errorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
			return this;
		}

		public CredentialDataResponse.CredentialDataResponseBuilder datum(CredentialData datum) {
			initData();
			this.data.add(datum);
			return this;
		}

		public CredentialDataResponse.CredentialDataResponseBuilder data(Collection<? extends CredentialData> data) {
			initData();
			this.data.addAll(data);
			return this;
		}

		private void initData() {
			if (this.data == null) {
				this.data = new ArrayList<CredentialData>();
			}
		}

		public CredentialDataResponse build() {
			List<CredentialData> data;
			switch (this.data == null ? 0 : this.data.size()) {
				case 0:
					data = java.util.Collections.emptyList();
					break;
				case 1:
					data = java.util.Collections.singletonList(this.data.get(0));
					break;
				default:
					data = java.util.Collections.unmodifiableList(new ArrayList<CredentialData>(this.data));
			}

			return new CredentialDataResponse(errorMessage, data);
		}

		@Override
		public String toString() {
			return "CredentialDataResponseBuilder{" +
					"errorMessage=" + errorMessage + "," +
					"data=" + data +
					'}';
		}
	}
}
