import sqlite3
import hashlib 

conn = sqlite3.connect("database/expense_manager.db")

cursor = conn.cursor()
cursor.execute("PRAGMA foreign_keys = ON")

# users table: stores everyone that can log into the app
cursor.execute("""
               CREATE TABLE IF NOT EXISTS users (
                   id INTEGER PRIMARY KEY AUTOINCREMENT, 
                   username TEXT NOT NULL UNIQUE,
                   password TEXT NOT NULL,
                   role TEXT NOT NULL
                   )
            """)

# expenses table: stores every expense an employee submits 
cursor.execute("""
               CREATE TABLE IF NOT EXISTS expenses (
                   id INTEGER PRIMARY KEY AUTOINCREMENT, 
                   user_id INTEGER NOT NULL,
                   amount REAL NOT NULL,
                   description TEXT NOT NULL,
                   date TEXT NOT NULL,
                   
                   FOREIGN KEY (user_id) REFERENCES users(id) 
                   )
            """)

# approvals table: The status tracker for each expense (everything is pending first then its approved or denied)
cursor.execute("""
               CREATE TABLE IF NOT EXISTS approvals (
                   id INTEGER PRIMARY KEY AUTOINCREMENT, 
                   expense_id INTEGER NOT NULL,
                   status TEXT NOT NULL,
                   reviewer TEXT,
                   comment TEXT,
                   review_date TEXT,
                   
                   FOREIGN KEY (expense_id) REFERENCES expenses(id)
                   )
            """)


hashed = hashlib.sha256("password123".encode()).hexdigest()
cursor.execute("INSERT OR IGNORE INTO users (username, password, role) values (?,?,?)", ('john', hashed, "employee"))
cursor.execute ("INSERT OR IGNORE INTO users (username, password, role) values (?,?,?)", ('manager1', hashed, 'manager'))

cursor.execute("INSERT OR IGNORE INTO expenses (user_id, amount, description, date) values (?,?,?,?)", (1,50,'withdrawl',"2026-08-04"))
cursor.execute("INSERT OR IGNORE INTO approvals (expense_id, status, reviewer, comment, review_date) values (?,?,?,?,?)", (1,'pending', None, None, None))
conn.commit()
conn.close()