from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parents[2]
DB_PATH = PROJECT_ROOT / "data" / "expense_manager.db"
SCHEMA_PATH = PROJECT_ROOT / "shared" / "schema.sql"
DATE_FORMAT = "%Y-%m-%d"
