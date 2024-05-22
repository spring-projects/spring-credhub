#!/bin/sh

set -eu

export TERM="xterm-256color"

readonly DOCKER_CONFIG_OUTPUT="${DOCKER_CONFIG_OUTPUT:?must be set}"

printf "%s" "$REGISTRY_PASSWORD" | docker login "$REGISTRY" --username "$REGISTRY_USERNAME" --password-stdin
cp -v ~/.docker/config.json "$DOCKER_CONFIG_OUTPUT/"
