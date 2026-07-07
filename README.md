
# Revature Expense Manager

A console-based expense tracking system built across two applications that share a single SQLite database: a **Python Employee App** for submitting and managing expenses, and a **Java Manager App** (exposed as a REST API) for reviewing, approving, and denying those expenses.

## Overview

Employees use a Python console application to submit expense reports, track their status, and view their approval history. Managers use a Java-based REST API (tested via Postman, viewable in DBeaver) to review pending expenses, approve or deny them with comments, and generate reports by employee, category, or date.

Both applications read from and write to the same `database/expense_manager.db` SQLite file, despite being written in completely different languages and run independently.

## Architecture

Both apps follow a layered architecture:

```
UI / Controller Layer   →  collects input, returns output
Service Layer           →  validates input, business rules (Python)
DAO Layer                →  talks to the database, no business logic
Model Layer               →  plain data objects (Java only)
```

The Java side additionally exposes its Controller layer as a Javalin REST API rather than a console menu, since it's tested via Postman and demoed alongside DBeaver.

## Tech Stack

| Category | Technology |
|---|---|
| Languages | Python 3.x, Java 17 |
| Database | SQLite |
| Java Web Framework | Javalin 7 |
| Java DB Connectivity | JDBC (sqlite-jdbc) |
| JSON Serialization | Jackson |
| Password Hashing | bcrypt (Python `bcrypt`, Java `at.favre.lib.bcrypt`) |
| Build Tools | Maven (Java), pip (Python) |
| Testing Tool | Postman |
| Database Viewer | DBeaver |

## Database Schema

**`users`** — id, username, password (bcrypt hash), role (`employee` or `manager`)
**`expenses`** — id, user_id (FK), amount, category, description, date
**`approvals`** — id, expense_id (FK), status (`pending`/`approved`/`denied`), reviewer, comment, review_date

Every new expense creates a row in `expenses` and a matching `pending` row in `approvals`. Status, reviewer, and comments live separately from the expense itself so that submission data never changes once created.

## Project Structure

```
Revature-Expense-Manager/
├── database/
│   └── expense_manager.db
├── employee-app/              (Python)
│   ├── main.py
│   ├── dao/
│   ├── service/
│   ├── ui/
│   └── db/
├── manager-app/                (Java, Javalin REST API)
│   └── src/main/java/com/revature/
│       ├── Main.java
│       ├── controllers/
│       ├── DAOs/
│       ├── models/
│       └── utils/
│       └── exceptions/
└── setup_db.py                 (creates schema + seeds test data)
```

## Setup

### Database
From the repo root:
```bash
python3 setup_db.py
```
Creates `database/expense_manager.db` with all tables and seed data (test employees, a manager, and sample expenses).

### Employee App (Python)
```bash
cd employee-app
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python3 main.py
```

### Manager App (Java)
```bash
cd manager-app
mvn clean install
mvn exec:java -Dexec.mainClass="com.revature.Main" // starts main from terminal
```
Run `Main.java` from your IDE

Server starts on `http://localhost:8080`.

## API Endpoints (Manager App)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/login` | Manager login (bcrypt-verified, role-checked) |
| GET | `/expenses/pending` | All expenses awaiting review |
| GET | `/reports/employee/{userId}` | All expenses for one employee |
| GET | `/reports/category/{category}` | All expenses in one category |
| GET | `/reports/date/{date}` | All expenses on one date |
| GET | `/reports/expense/{expenseId}` | A single expense by id |

### Example login request
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username": "vanessa", "password": "password123"}'

curl http://localhost:8080/login
curl http://localhost:8080/expenses/pending
curl http://localhost:8080/expenses/approved
curl http://localhost:8080/reports/employee/1
curl http://localhost:8080/reports/category/travel
curl http://localhost:8080/reports/date/2026-06-01
curl http://localhost:8080/reports/employee/999 // invalid user
curl http://localhost:8080/reports/expense/1
```

## Employee App Features (Python Console)

- Secure login 
- Submit a new expense (amount, description, category)
- View all personal expenses with current status
- Edit a pending expense
- Delete a pending expense
- View history of approved/denied expenses

## Notes

- Passwords are hashed with bcrypt on both sides; Java uses `at.favre.lib.bcrypt` specifically because the older `jbcrypt` library doesn't support the `$2b$` hash format Python's `bcrypt` produces.
- The Java Manager App is built as a REST API (not a console app) so it can be demoed live via Postman, with database changes verified in real time through DBeaver.


