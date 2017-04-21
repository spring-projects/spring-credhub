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

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class AccessControlEntry {
	private static final String APP_ACTOR_PREFIX = "mtls-app:";
	
	@Setter(AccessLevel.PRIVATE)
	private String actor;

	@Singular
	private List<Operation> operations;

	public List<String> getOperations() {
		List<String> operationValues = new ArrayList<String>(operations.size());
		for (Operation operation : operations) {
			operationValues.add(operation.operation());
		}
		return operationValues;
	}

	public static class AccessControlEntryBuilder {
		public AccessControlEntryBuilder app(String appId) {
			this.actor = APP_ACTOR_PREFIX + appId;
			return this;
		}
	}

	public enum Operation {
		READ("read"),
		WRITE("write");

		private final String operation;

		Operation(String operation) {
			this.operation = operation;
		}

		public String operation() {
			return operation;
		}
	}
}
