
import sqlite3
import bcrypt

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
                   category TEXT,
                   
                   FOREIGN KEY (user_id) REFERENCES users(id) 
                   )
            """)

# approvals table: The status tracker for each expense (everything is pending first then its approved or denied)
cursor.execute("""
               CREATE TABLE IF NOT EXISTS approvals (
                   id INTEGER PRIMARY KEY AUTOINCREMENT, 
                   expense_id INTEGER NOT NULL,
                   status TEXT NOT NULL,
                   reviewer INTEGER,
                   comment TEXT,
                   review_date TEXT,
                   
                   FOREIGN KEY (expense_id) REFERENCES expenses(id),
                   FOREIGN KEY (reviewer) REFERENCES users(id)
                   )
            """)


# Hash passwords with bcrypt
marco_password = bcrypt.hashpw("password123".encode(), bcrypt.gensalt()).decode()
bob_password = bcrypt.hashpw("password123".encode(), bcrypt.gensalt()).decode()
vanessa_password = bcrypt.hashpw("password123".encode(), bcrypt.gensalt()).decode()

cursor.execute("""
    INSERT OR IGNORE INTO users (username, password, role) VALUES (?, ?, ?), (?, ?, ?), (?, ?, ?)
""", ('marco', marco_password, 'employee',
      'bob', bob_password, 'employee',
      'vanessa', vanessa_password, 'manager'))

## TEST DATA
cursor.execute("""
               INSERT INTO expenses (user_id, amount, category, description, date) VALUES
                ((SELECT id FROM users WHERE username = 'marco'), 135.42, 'travel', 'Airport rideshare to client site', '2026-06-01'),
                ((SELECT id FROM users WHERE username = 'marco'), 82.19, 'meals', 'Team lunch during sprint planning', '2026-06-03'),
                ((SELECT id FROM users WHERE username = 'bob'), 46.77, 'office', 'Replacement keyboard for workstation', '2026-06-02'),
                ((SELECT id FROM users WHERE username = 'bob'), 312.50, 'lodging', 'Hotel for training travel', '2026-06-04')
""")

cursor.execute("""
               INSERT INTO approvals (expense_id, status, reviewer, comment, review_date) VALUES
               (
                   (SELECT id FROM expenses WHERE description = 'Airport rideshare to client site'),
                   'approved',
                   (SELECT id FROM users WHERE username = 'vanessa'),
                   'Approved for client travel reimbursement.',
                   '2026-06-02'
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Team lunch during sprint planning'),
                   'pending', NULL, NULL, NULL
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Replacement keyboard for workstation'),
                   'denied',
                   (SELECT id FROM users WHERE username = 'vanessa'),
                   'Please attach the original approval request before resubmitting.',
                   '2026-06-03'
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Hotel for training travel'),
                   'pending', NULL, NULL, NULL
               )
""")

## ADDITIONAL TEST DATA
cursor.execute("""
               INSERT INTO expenses (user_id, amount, category, description, date) VALUES
                ((SELECT id FROM users WHERE username = 'marco'), 19.99, 'office', 'USB-C cable for docking station', '2026-06-05'),
                ((SELECT id FROM users WHERE username = 'marco'), 240.00, 'travel', 'Round-trip train ticket to regional office', '2026-06-07'),
                ((SELECT id FROM users WHERE username = 'marco'), 58.30, 'meals', 'Client dinner after demo', '2026-06-10'),
                ((SELECT id FROM users WHERE username = 'marco'), 1200.00, 'lodging', 'Conference hotel stay (3 nights)', '2026-06-12'),
                ((SELECT id FROM users WHERE username = 'bob'), 75.00, 'travel', 'Parking at downtown client garage', '2026-06-06'),
                ((SELECT id FROM users WHERE username = 'bob'), 14.25, 'meals', 'Coffee run for onboarding session', '2026-06-08'),
                ((SELECT id FROM users WHERE username = 'bob'), 499.99, 'office', 'Standing desk converter', '2026-06-09'),
                ((SELECT id FROM users WHERE username = 'bob'), 6500.00, 'travel', 'International flight for vendor summit', '2026-06-11'),
                ((SELECT id FROM users WHERE username = 'marco'), 33.10, 'meals', 'Working lunch during code review', '2026-06-13'),
                ((SELECT id FROM users WHERE username = 'bob'), 128.40, 'office', 'Ergonomic chair cushion set', '2026-06-14')
""")

cursor.execute("""
               INSERT INTO approvals (expense_id, status, reviewer, comment, review_date) VALUES
               (
                   (SELECT id FROM expenses WHERE description = 'USB-C cable for docking station'),
                   'approved',
                   (SELECT id FROM users WHERE username = 'vanessa'),
                   'Minor office supply, approved.',
                   '2026-06-06'
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Round-trip train ticket to regional office'),
                   'approved',
                   (SELECT id FROM users WHERE username = 'vanessa'),
                   'Approved for regional office visit.',
                   '2026-06-08'
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Client dinner after demo'),
                   'pending', NULL, NULL, NULL
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Conference hotel stay (3 nights)'),
                   'denied',
                   (SELECT id FROM users WHERE username = 'vanessa'),
                   'Exceeds lodging cap; please rebook within policy.',
                   '2026-06-13'
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Parking at downtown client garage'),
                   'approved',
                   (SELECT id FROM users WHERE username = 'vanessa'),
                   'Approved, client visit confirmed.',
                   '2026-06-07'
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Coffee run for onboarding session'),
                   'pending', NULL, NULL, NULL
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Standing desk converter'),
                   'pending', NULL, NULL, NULL
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'International flight for vendor summit'),
                   'denied',
                   (SELECT id FROM users WHERE username = 'vanessa'),
                   'Needs VP sign-off before booking international travel.',
                   '2026-06-12'
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Working lunch during code review'),
                   'approved',
                   (SELECT id FROM users WHERE username = 'vanessa'),
                   'Approved.',
                   '2026-06-14'
               ),
               (
                   (SELECT id FROM expenses WHERE description = 'Ergonomic chair cushion set'),
                   'pending', NULL, NULL, NULL
               )
""")

conn.commit()
conn.close()
print("Database setup complete!")
