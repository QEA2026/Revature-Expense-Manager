Revature Expense Manager

Description

This project is a console expense manager.

It has two apps:

Python employee app
Java manager app

Both apps use the same SQLite database.

Folder Layout

python_employee_app
Employee side

java_manager_app
Manager side

shared
Shared database schema

data
SQLite database file

scripts
Run scripts

Login Info

Employees
marco / Employee123!
bob / Employee123!

Manager
vanessa / Manager123!

How to Run

Go to the project folder
/Users/marcocastro/Desktop/P0_RevatureExpenseManager

Run the employee app
./scripts/run_employee.sh

Compile the manager app
./scripts/compile_manager.sh

Run the manager app
./scripts/run_manager.sh

Basic Flow

1. Log in as an employee
2. Add an expense
3. Log in as the manager
4. Review the pending expense
5. Log back in as the employee
6. Check the updated status

Notes

The database is SQLite.
Both apps use the same database file.
Expense categories are included because the manager side has category reports.
Passwords are stored as hashes.
