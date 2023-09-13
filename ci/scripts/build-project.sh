#!/bin/bash

set -euo pipefail

# shellcheck source=common.sh
source "$(dirname "$0")/common.sh"
repository=$(pwd)/distribution-repository

start_docker() {
  pushd credhub-server >/dev/null
    service cgroupfs-mount start
    service docker start
    docker-compose up --detach
    trap "stop_docker" EXIT
  popd >/dev/null
}

stop_docker() {
  pushd credhub-server >/dev/null
    docker-compose stop
    service cgroupfs-mount stop
    service docker stop
  popd >/dev/null
}

main() {
  cd git-repo >/dev/null
  start_docker
  ./gradlew clean build publish -PpublicationRepository="${repository}" -PintegrationTests --no-parallel
}

main
