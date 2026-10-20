#!/usr/bin/env sh
DIR="$(cd "$(dirname "$0")/backend" && pwd)"
exec "$DIR/mvnw" "$@"
