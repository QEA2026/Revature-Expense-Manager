from __future__ import annotations

import logging
from datetime import datetime

from .config import DATE_FORMAT
from .db import get_connection, managed_connection
from .exceptions import (
    AuthenticationError,
    AuthorizationError,
    RecordNotFoundError,
    ValidationError,
)
from .models import ExpenseRecord, User
from .security import verify_password

logger = logging.getLogger(__name__)


class AuthService:
    def login(self, username: str, password: str) -> User:
        clean_username = username.strip()
        if not clean_username or not password:
            raise AuthenticationError("Username and password are required.")

        with get_connection() as connection:
            row = connection.execute(
                "SELECT id, username, password, role FROM users WHERE username = ?",
                (clean_username,),
            ).fetchone()

        if row is None or not verify_password(password, row["password"]):
            raise AuthenticationError("Invalid username or password.")

        if row["role"] != "employee":
            raise AuthorizationError("This account belongs to a manager. Use the Java manager app.")

        logger.info("Employee %s logged in.", clean_username)
        return User(id=row["id"], username=row["username"], role=row["role"])


class ExpenseService:
    def submit_expense(
        self,
        user: User,
        amount_text: str,
        category: str,
        description: str,
        expense_date: str,
    ) -> int:
        amount = self._parse_amount(amount_text)
        clean_category = self._require_text(category, "Category")
        clean_description = self._require_text(description, "Description")
        clean_date = self._parse_date(expense_date)

        with managed_connection() as connection:
            cursor = connection.execute(
                """
                INSERT INTO expenses (user_id, amount, category, description, date, updated_at)
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
                """,
                (user.id, amount, clean_category, clean_description, clean_date),
            )
            expense_id = cursor.lastrowid
            connection.execute(
                """
                INSERT INTO approvals (expense_id, status, reviewer, comment, review_date)
                VALUES (?, 'pending', NULL, NULL, NULL)
                """,
                (expense_id,),
            )

        logger.info("Employee %s submitted expense %s.", user.username, expense_id)
        return int(expense_id)

    def list_user_expenses(self, user: User) -> list[ExpenseRecord]:
        return self._fetch_expenses(
            """
            WHERE e.user_id = ?
            ORDER BY e.date DESC, e.id DESC
            """,
            (user.id,),
        )

    def list_pending_expenses(self, user: User) -> list[ExpenseRecord]:
        return self._fetch_expenses(
            """
            WHERE e.user_id = ? AND a.status = 'pending'
            ORDER BY e.date DESC, e.id DESC
            """,
            (user.id,),
        )

    def list_history(self, user: User) -> list[ExpenseRecord]:
        return self._fetch_expenses(
            """
            WHERE e.user_id = ? AND a.status IN ('approved', 'denied')
            ORDER BY e.date DESC, e.id DESC
            """,
            (user.id,),
        )

    def get_pending_expense(self, user: User, expense_id_text: str) -> ExpenseRecord:
        expense_id = self._parse_expense_id(expense_id_text)
        records = self._fetch_expenses(
            """
            WHERE e.user_id = ? AND e.id = ? AND a.status = 'pending'
            """,
            (user.id, expense_id),
        )
        if not records:
            raise RecordNotFoundError("Pending expense not found for your account.")
        return records[0]

    def update_pending_expense(
        self,
        user: User,
        expense_id_text: str,
        amount_text: str,
        category: str,
        description: str,
        expense_date: str,
    ) -> None:
        expense = self.get_pending_expense(user, expense_id_text)
        amount = self._parse_amount(amount_text)
        clean_category = self._require_text(category, "Category")
        clean_description = self._require_text(description, "Description")
        clean_date = self._parse_date(expense_date)

        with managed_connection() as connection:
            connection.execute(
                """
                UPDATE expenses
                SET amount = ?, category = ?, description = ?, date = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """,
                (amount, clean_category, clean_description, clean_date, expense.id),
            )

        logger.info("Employee %s updated expense %s.", user.username, expense.id)

    def delete_pending_expense(self, user: User, expense_id_text: str) -> None:
        expense = self.get_pending_expense(user, expense_id_text)
        with managed_connection() as connection:
            connection.execute("DELETE FROM expenses WHERE id = ?", (expense.id,))
        logger.info("Employee %s deleted expense %s.", user.username, expense.id)

    def _fetch_expenses(self, where_clause: str, params: tuple[object, ...]) -> list[ExpenseRecord]:
        query = f"""
            SELECT
                e.id,
                employee.username AS employee_username,
                e.amount,
                e.category,
                e.description,
                e.date AS expense_date,
                a.status,
                COALESCE(a.comment, '') AS comment,
                COALESCE(a.review_date, '') AS review_date,
                COALESCE(reviewer.username, '') AS reviewer_username
            FROM expenses e
            INNER JOIN users employee ON employee.id = e.user_id
            INNER JOIN approvals a ON a.expense_id = e.id
            LEFT JOIN users reviewer ON reviewer.id = a.reviewer
            {where_clause}
        """
        with get_connection() as connection:
            rows = connection.execute(query, params).fetchall()
        return [ExpenseRecord.from_row(row) for row in rows]

    @staticmethod
    def _parse_amount(amount_text: str) -> float:
        try:
            amount = float(amount_text)
        except ValueError as error:
            raise ValidationError("Amount must be a valid number.") from error

        if amount <= 0:
            raise ValidationError("Amount must be greater than zero.")
        return round(amount, 2)

    @staticmethod
    def _require_text(value: str, field_name: str) -> str:
        clean_value = value.strip()
        if not clean_value:
            raise ValidationError(f"{field_name} is required.")
        return clean_value

    @staticmethod
    def _parse_date(expense_date: str) -> str:
        try:
            parsed_date = datetime.strptime(expense_date.strip(), DATE_FORMAT)
        except ValueError as error:
            raise ValidationError(f"Date must use the format {DATE_FORMAT}.") from error
        return parsed_date.strftime(DATE_FORMAT)

    @staticmethod
    def _parse_expense_id(expense_id_text: str) -> int:
        try:
            expense_id = int(expense_id_text)
        except ValueError as error:
            raise ValidationError("Expense ID must be a whole number.") from error

        if expense_id <= 0:
            raise ValidationError("Expense ID must be greater than zero.")
        return expense_id
