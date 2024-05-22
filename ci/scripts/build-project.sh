#!/bin/bash

set -euo pipefail

readonly DOCKERHUB_MIRROR_REGISTRY="${DOCKERHUB_MIRROR_REGISTRY:?must be set}"
readonly DOCKERHUB_MIRROR_REGISTRY_USERNAME="${DOCKERHUB_MIRROR_REGISTRY_USERNAME:?must be set}"
readonly DOCKERHUB_MIRROR_REGISTRY_PASSWORD="${DOCKERHUB_MIRROR_REGISTRY_PASSWORD:?must be set}"

# shellcheck source=common.sh
source "$(dirname "$0")/common.sh"
repository=$(pwd)/distribution-repository

start_docker() {
  pushd credhub-server >/dev/null
    echo "{\"registry-mirrors\": [\"https://$DOCKERHUB_MIRROR_REGISTRY\"]}" > /etc/docker/daemon.json
    service cgroupfs-mount start
    service docker start

    # Work around https://github.com/moby/moby/issues/30880
    cat >> /etc/hosts << EOF
127.0.0.1  index.docker.io
127.0.0.1  registry-1.docker.io
127.0.0.1  docker.io
EOF
    mkdir -p "$HOME/.docker"
    jq --arg username "$DOCKERHUB_MIRROR_REGISTRY_USERNAME" \
      --arg password "$DOCKERHUB_MIRROR_REGISTRY_PASSWORD" \
      'reduce .[] as $registry ({"auths": {}}; .auths += {($registry): {"auth": [$username, $password] | join(":") | @base64}})' \
      <<< "[\"index.docker.io\", \"$DOCKERHUB_MIRROR_REGISTRY\"]" \
      > "$HOME/.docker/config.json"

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
  ./gradlew build publish -PpublicationRepository="${repository}" -PintegrationTests --no-parallel
}

main
