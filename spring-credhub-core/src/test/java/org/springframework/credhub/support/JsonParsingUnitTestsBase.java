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

import java.text.DateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.junit.Before;

public abstract class JsonParsingUnitTestsBase {
	protected ObjectMapper objectMapper;
	protected Date testDate;
	protected String testDateString;

	@Before
	public void setUpJsonParsing() throws Exception {
		DateFormat dateFormat = new ISO8601DateFormat();

		objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(dateFormat);

		testDateString = "2017-01-31T11:22:33Z";
		testDate = dateFormat.parse(testDateString);
	}
}
