from __future__ import annotations

import sqlite3
from contextlib import contextmanager
from typing import Iterator

from .config import DB_PATH, SCHEMA_PATH


def _database_ready() -> bool:
    if not DB_PATH.exists():
        return False

    with sqlite3.connect(DB_PATH) as connection:
        result = connection.execute(
            "SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'users'"
        ).fetchone()
    return result is not None


def initialize_database() -> None:
    DB_PATH.parent.mkdir(parents=True, exist_ok=True)
    if _database_ready():
        return

    schema_sql = SCHEMA_PATH.read_text(encoding="utf-8")
    with sqlite3.connect(DB_PATH) as connection:
        connection.execute("PRAGMA foreign_keys = ON")
        connection.executescript(schema_sql)


def get_connection() -> sqlite3.Connection:
    initialize_database()
    connection = sqlite3.connect(DB_PATH)
    connection.row_factory = sqlite3.Row
    connection.execute("PRAGMA foreign_keys = ON")
    return connection


@contextmanager
def managed_connection() -> Iterator[sqlite3.Connection]:
    connection = get_connection()
    try:
        yield connection
        connection.commit()
    except Exception:
        connection.rollback()
        raise
    finally:
        connection.close()
