#!/usr/bin/env zsh
set -euo pipefail

# Usage:
#   DB_HOST=127.0.0.1 DB_PORT=3306 DB_USER=root DB_PASSWORD=secret DB_NAME=MaListe ./server/scripts/verify_schema.sh

DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-}"
DB_NAME="${DB_NAME:-MaListe}"

MYSQL_BASE=(mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER")
if [[ -n "$DB_PASSWORD" ]]; then
  MYSQL_BASE+=("-p$DB_PASSWORD")
fi

query_scalar() {
  local sql="$1"
  "${MYSQL_BASE[@]}" -N -B -e "$sql"
}

required_tables=(Categories Produits Magasins Configuration_Rayons Listes Listes_Produits)

echo "[verify] Database: $DB_NAME@$DB_HOST:$DB_PORT"
for table in "${required_tables[@]}"; do
  count=$(query_scalar "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$DB_NAME' AND table_name='$table';")
  if [[ "$count" != "1" ]]; then
    echo "[error] Missing table: $table"
    exit 1
  fi
  echo "[ok] table $table"
done

idx_count=$(query_scalar "SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema='$DB_NAME' AND table_name='Configuration_Rayons' AND index_name='idx_cfg_magasin_position';")
if [[ "$idx_count" -lt "1" ]]; then
  echo "[error] Missing index idx_cfg_magasin_position"
  exit 1
fi
echo "[ok] index idx_cfg_magasin_position"

echo "[success] Schema verification passed."

