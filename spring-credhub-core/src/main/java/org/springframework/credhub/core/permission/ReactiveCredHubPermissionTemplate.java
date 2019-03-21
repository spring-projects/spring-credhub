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

package org.springframework.credhub.core.permission;

import org.springframework.credhub.core.ExceptionUtils;
import org.springframework.credhub.core.ReactiveCredHubOperations;
import org.springframework.credhub.support.CredentialName;
import org.springframework.credhub.support.CredentialPermissions;
import org.springframework.credhub.support.permissions.Actor;
import org.springframework.credhub.support.permissions.Permission;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implements the main interaction with CredHub to add, retrieve,
 * and delete permissions.
 *
 * @author Scott Frederick
 */
public class ReactiveCredHubPermissionTemplate implements ReactiveCredHubPermissionOperations {
	private static final String PERMISSIONS_URL_PATH = "/api/v1/permissions";
	private static final String PERMISSIONS_URL_QUERY = PERMISSIONS_URL_PATH + "?credential_name={name}";
	private static final String PERMISSIONS_ACTOR_URL_QUERY = PERMISSIONS_URL_QUERY + "&actor={actor}";

	private ReactiveCredHubOperations credHubOperations;

	/**
	 * Create a new {@link ReactiveCredHubPermissionTemplate}.
	 *
	 * @param credHubOperations the {@link ReactiveCredHubOperations} to use for interactions with CredHub
	 */
	public ReactiveCredHubPermissionTemplate(ReactiveCredHubOperations credHubOperations) {
		this.credHubOperations = credHubOperations;
	}

	@Override
	public Flux<Permission> getPermissions(final CredentialName name) {
		Assert.notNull(name, "credential name must not be null");

		return credHubOperations.doWithWebClient(webClient -> webClient
				.get()
				.uri(PERMISSIONS_URL_QUERY, name.getName())
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(CredentialPermissions.class)
				.flatMapMany(data -> Flux.fromIterable(data.getPermissions())));
	}

	@Override
	public Mono<Void> addPermissions(final CredentialName name,
									 final Permission... permissions) {
		Assert.notNull(name, "credential name must not be null");

		final CredentialPermissions credentialPermissions = new CredentialPermissions(name, permissions);

		return credHubOperations.doWithWebClient(webClient -> webClient
				.post()
				.uri(PERMISSIONS_URL_PATH)
				.syncBody(credentialPermissions)
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(Void.class));
	}

	@Override
	public Mono<Void> deletePermission(final CredentialName name, final Actor actor) {
		Assert.notNull(name, "credential name must not be null");
		Assert.notNull(actor, "actor must not be null");

		return credHubOperations.doWithWebClient(webClient -> webClient
				.delete()
				.uri(PERMISSIONS_ACTOR_URL_QUERY, name.getName(), actor.getIdentity())
				.retrieve()
				.onStatus(HttpStatus::isError, ExceptionUtils::buildError)
				.bodyToMono(Void.class));
	}
}
