from __future__ import annotations

import logging
from getpass import getpass

from .db import initialize_database
from .exceptions import ExpenseManagerError
from .models import ExpenseRecord, User
from .services import AuthService, ExpenseService


def run() -> None:
    logging.basicConfig(
        level=logging.WARNING,
        format="%(levelname)s: %(message)s",
    )
    initialize_database()

    auth_service = AuthService()
    expense_service = ExpenseService()

    while True:
        print("\nExpense Manager - Employee")
        print("1. Log in")
        print("2. Exit")
        choice = input("Choose 1 or 2: ").strip()

        if choice == "1":
            user = _login(auth_service)
            if user is not None:
                _employee_menu(user, expense_service)
        elif choice == "2":
            print("Goodbye.")
            return
        else:
            print("Please choose 1 or 2.")


def _login(auth_service: AuthService) -> User | None:
    username = input("Username: ").strip()
    password = getpass("Password: ")
    try:
        user = auth_service.login(username, password)
    except ExpenseManagerError as error:
        print(f"Login failed: {error}")
        return None

    print(f"\nWelcome, {user.username}.")
    return user


def _employee_menu(user: User, expense_service: ExpenseService) -> None:
    while True:
        print(f"\nMy Expenses - {user.username}")
        print("1. Add a new expense")
        print("2. See all my expenses")
        print("3. See pending expenses")
        print("4. Edit a pending expense")
        print("5. Remove a pending expense")
        print("6. See reviewed expenses")
        print("7. Log out")
        choice = input("Choose 1 to 7: ").strip()

        try:
            if choice == "1":
                _submit_expense(user, expense_service)
            elif choice == "2":
                _display_expenses("All my expenses", expense_service.list_user_expenses(user))
            elif choice == "3":
                _display_expenses("My pending expenses", expense_service.list_pending_expenses(user))
            elif choice == "4":
                _edit_expense(user, expense_service)
            elif choice == "5":
                _delete_expense(user, expense_service)
            elif choice == "6":
                _display_expenses("My reviewed expenses", expense_service.list_history(user))
            elif choice == "7":
                print("Logging out.")
                return
            else:
                print("Please choose a menu option from 1 to 7.")
        except ExpenseManagerError as error:
            print(f"Action failed: {error}")


def _submit_expense(user: User, expense_service: ExpenseService) -> None:
    amount = input("Amount: ").strip()
    category = input("Category: ").strip()
    description = input("Description: ").strip()
    expense_date = input("Date (YYYY-MM-DD): ").strip()
    expense_id = expense_service.submit_expense(user, amount, category, description, expense_date)
    print(f"Expense {expense_id} submitted. It is now pending review.")


def _edit_expense(user: User, expense_service: ExpenseService) -> None:
    pending_expenses = expense_service.list_pending_expenses(user)
    _display_expenses("My pending expenses", pending_expenses)
    if not pending_expenses:
        return

    expense_id = input("Type the expense ID to edit: ").strip()
    expense = expense_service.get_pending_expense(user, expense_id)

    amount = _prompt_with_default("Amount", f"{expense.amount:.2f}")
    category = _prompt_with_default("Category", expense.category)
    description = _prompt_with_default("Description", expense.description)
    expense_date = _prompt_with_default("Date (YYYY-MM-DD)", expense.expense_date)
    expense_service.update_pending_expense(user, expense_id, amount, category, description, expense_date)
    print(f"Expense {expense.id} updated.")


def _delete_expense(user: User, expense_service: ExpenseService) -> None:
    pending_expenses = expense_service.list_pending_expenses(user)
    _display_expenses("My pending expenses", pending_expenses)
    if not pending_expenses:
        return

    expense_id = input("Type the expense ID to remove: ").strip()
    expense = expense_service.get_pending_expense(user, expense_id)
    confirmation = input(f"Remove expense {expense.id}? Type yes to confirm: ").strip().lower()
    if confirmation != "yes":
        print("Remove cancelled.")
        return

    expense_service.delete_pending_expense(user, expense_id)
    print(f"Expense {expense.id} removed.")


def _display_expenses(title: str, expenses: list[ExpenseRecord]) -> None:
    print(f"\n{title}")
    if not expenses:
        print("No expenses found.")
        return

    divider = "-" * 78
    print(divider)
    for expense in expenses:
        print(
            f"[{expense.id}] {expense.expense_date} | {expense.category} | "
            f"${expense.amount:.2f} | {expense.status}"
        )
        print(f"Description: {expense.description}")
        print(f"Reviewer: {expense.reviewer_username or 'Not reviewed yet'}")
        print(f"Comment: {expense.comment or '-'}")
        if expense.review_date:
            print(f"Review date: {expense.review_date}")
        print(divider)


def _prompt_with_default(label: str, default_value: str) -> str:
    entered_value = input(f"{label} [{default_value}]: ").strip()
    return entered_value or default_value
