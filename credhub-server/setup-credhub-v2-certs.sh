#!/bin/bash

if ! command -v openssl >/dev/null; then
  echo "openssl is required to generate the CredHub v2 test server CA"
  exit 1
fi

mkdir -p credhub-v2-certs

openssl req \
  -x509 \
  -newkey rsa:2048 \
  -days 36500 \
  -sha256 \
  -nodes \
  -subj "/CN=credhub_server_ca" \
  -keyout credhub-v2-certs/server_ca_private.pem \
  -out credhub-v2-certs/server_ca_cert.pem
