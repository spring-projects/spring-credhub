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

package org.springframework.credhub.integration;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import reactor.blockhound.BlockingOperationError;
import reactor.core.scheduler.Schedulers;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Verifies that BlockHound is actually installed for this module's tests.
 *
 * BlockHound is installed automatically by a JUnit Platform {@code TestExecutionListener}
 * registered via {@code ServiceLoader}. If that installation silently fails, the reactive
 * integration tests would still pass while no longer detecting blocking calls on
 * reactor-netty event loop threads, so this test asserts the instrumentation is live.
 *
 * Unlike the other tests in this module, this one needs no running CredHub server.
 */
class BlockHoundInstallationTests {

	@Test
	void blockingCallOnNonBlockingThreadIsDetected() {
		FutureTask<String> task = new FutureTask<>(() -> {
			Thread.sleep(0);
			return "";
		});
		Schedulers.parallel().schedule(task);

		assertThatExceptionOfType(ExecutionException.class).isThrownBy(() -> task.get(10, TimeUnit.SECONDS))
			.withCauseInstanceOf(BlockingOperationError.class);
	}

}
