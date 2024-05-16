#!/usr/bin/env bash

#
# Copyright 2016-2021 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -euo pipefail

readonly FLY_TARGET="${FLY_TARGET:-"spring-credhub"}"
readonly GITHUB_REPO="https://github.com/spring-projects/spring-credhub"

set_pipeline() {
	local pipeline_name pipeline_definition branch
	pipeline_name="${1:?pipeline name must be provided}"
	pipeline_definition="${2:?pipeline definition file must be provided}"
	branch="${3:?branch must be provided}"

	echo "Setting $pipeline_name $branch pipeline..."
	fly --target "$FLY_TARGET" set-pipeline \
		--pipeline "$pipeline_name" \
		--config "$pipeline_definition" \
		--load-vars-from config-concourse.yml \
		--instance-var "branch=$branch" \
		--var "github-repo=$GITHUB_REPO" \
		--var "ci-image-tag=$branch"
}

set_pipelines() {
	fly -t "$FLY_TARGET" sync
  set_pipeline spring-credhub pipeline.yml 3.2.x
  set_pipeline spring-credhub pipeline.yml 3.1.x
  set_pipeline spring-credhub pipeline.yml 3.0.x
  set_pipeline spring-credhub pipeline.yml 2.3.x
  set_pipeline spring-credhub pipeline.yml 2.2.x

  set_pipeline spring-credhub-pr pr-pipeline.yml 3.2.x
  set_pipeline spring-credhub-pr pr-pipeline.yml 3.1.x
  set_pipeline spring-credhub-pr pr-pipeline.yml 3.0.x
  set_pipeline spring-credhub-pr pr-pipeline.yml 2.3.x
  set_pipeline spring-credhub-pr pr-pipeline.yml 2.2.x
}

main() {
  pushd "$(dirname "$0")/../ci" >/dev/null

  set_pipelines

  popd >/dev/null
}

main
