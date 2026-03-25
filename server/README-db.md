# Database setup (P0-02)

This guide initializes and verifies the `MaListe` MySQL schema used by the server.

## 1) Prerequisites

- MySQL 8+
- `mysql` CLI available in PATH

## 2) Schema migration file

The versioned schema lives in:

- `server/src/main/resources/db/migration/V1__maliste_schema.sql`

## 3) Initialize database

From repository root:

```zsh
chmod +x server/scripts/init_schema.sh server/scripts/verify_schema.sh
DB_HOST=127.0.0.1 DB_PORT=3306 DB_USER=root DB_PASSWORD=your_password DB_NAME=MaListe ./server/scripts/init_schema.sh
```

## 4) Verify schema

```zsh
DB_HOST=127.0.0.1 DB_PORT=3306 DB_USER=root DB_PASSWORD=your_password DB_NAME=MaListe ./server/scripts/verify_schema.sh
```

Expected output ends with:

- `[success] Schema verification passed.`

## 5) Reset (optional)

```zsh
mysql -h 127.0.0.1 -P 3306 -u root -pyour_password -e "DROP DATABASE IF EXISTS MaListe;"
DB_HOST=127.0.0.1 DB_PORT=3306 DB_USER=root DB_PASSWORD=your_password DB_NAME=MaListe ./server/scripts/init_schema.sh
```

## Done criteria for US P0-02

- Schema creates all required tables and constraints
- Required index for rayon ordering exists (`idx_cfg_magasin_position`)
- Initialization and verification are reproducible from scripts

