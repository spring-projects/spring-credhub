#!/bin/bash

if ! command -v openssl >/dev/null; then
  echo "openssl is required to generate signing keys"
  exit 1
fi

if ! command -v ytt >/dev/null; then
  echo "ytt is required to create UAA configuration YAML"
  exit 1
fi

openssl genrsa -out config/privkey.pem 2048
openssl rsa -pubout -in config/privkey.pem -out config/pubkey.pem

ytt -f config > uaa.yml