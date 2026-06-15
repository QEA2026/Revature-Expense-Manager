from __future__ import annotations

from dataclasses import dataclass
from sqlite3 import Row


@dataclass(slots=True)
class User:
    id: int
    username: str
    role: str


@dataclass(slots=True)
class ExpenseRecord:
    id: int
    employee_username: str
    amount: float
    category: str
    description: str
    expense_date: str
    status: str
    comment: str
    review_date: str
    reviewer_username: str

    @classmethod
    def from_row(cls, row: Row) -> "ExpenseRecord":
        return cls(
            id=row["id"],
            employee_username=row["employee_username"],
            amount=float(row["amount"]),
            category=row["category"],
            description=row["description"],
            expense_date=row["expense_date"],
            status=row["status"],
            comment=row["comment"] or "",
            review_date=row["review_date"] or "",
            reviewer_username=row["reviewer_username"] or "",
        )
