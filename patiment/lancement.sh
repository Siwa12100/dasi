#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [[ ! -x ./mvnw ]]; then
  chmod +x ./mvnw
fi

echo "[lancement] Compilation du projet..."
./mvnw -DskipTests clean compile

echo "[lancement] Demarrage de l'application..."
./mvnw -DskipTests exec:java
