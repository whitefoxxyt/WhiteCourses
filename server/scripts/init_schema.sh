#!/usr/bin/env zsh
set -euo pipefail

# Usage:
#   DB_HOST=127.0.0.1 DB_PORT=3306 DB_USER=root DB_PASSWORD=secret DB_NAME=MaListe ./server/scripts/init_schema.sh

DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_NAME="${DB_NAME:-MaListe}"
MIGRATION_FILE="${MIGRATION_FILE:-server/src/main/resources/db/migration/V1__maliste_schema.sql}"

if [[ ! -f "$MIGRATION_FILE" ]]; then
  echo "[error] Migration file not found: $MIGRATION_FILE"
  exit 1
fi

MYSQL_BASE=(mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER")
if [[ -n "$DB_PASSWORD" ]]; then
  MYSQL_BASE+=("-p$DB_PASSWORD")
fi

echo "[init] Creating database if needed: $DB_NAME"
"${MYSQL_BASE[@]}" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME;"

echo "[init] Applying migration: $MIGRATION_FILE"
"${MYSQL_BASE[@]}" "$DB_NAME" < "$MIGRATION_FILE"

echo "[success] Schema initialized for $DB_NAME"

