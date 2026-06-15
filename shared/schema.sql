PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('employee', 'manager'))
);

CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    amount REAL NOT NULL CHECK (amount > 0),
    category TEXT NOT NULL,
    description TEXT NOT NULL,
    date TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS approvals (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    expense_id INTEGER NOT NULL UNIQUE,
    status TEXT NOT NULL CHECK (status IN ('pending', 'approved', 'denied')),
    reviewer INTEGER,
    comment TEXT,
    review_date TEXT,
    FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer) REFERENCES users(id) ON DELETE SET NULL
);

INSERT INTO users (username, password, role) VALUES
    ('marco', 'pbkdf2_sha256$120000$be2108e2b1c835d67ddb60cab07203f0$9107032bbb059cf21e8a74ac99868164b90a2822d631848b2772d6d684c8a263', 'employee'),
    ('bob', 'pbkdf2_sha256$120000$663e80f2d6094fff3122fdf0ee9a70b2$ff9efb57204805b8e90551043d668ccc45a509e7672b616e55117cabcdd62a72', 'employee'),
    ('vanessa', 'pbkdf2_sha256$120000$0aca28a501c6a558fc8a1751e8219e54$2638c461c187d08afb93234414f9938c3017f1626bbcebf3d7dc257bffbbc7b0', 'manager');

INSERT INTO expenses (user_id, amount, category, description, date) VALUES
    ((SELECT id FROM users WHERE username = 'marco'), 135.42, 'travel', 'Airport rideshare to client site', '2026-06-01'),
    ((SELECT id FROM users WHERE username = 'marco'), 82.19, 'meals', 'Team lunch during sprint planning', '2026-06-03'),
    ((SELECT id FROM users WHERE username = 'bob'), 46.77, 'office', 'Replacement keyboard for workstation', '2026-06-02'),
    ((SELECT id FROM users WHERE username = 'bob'), 312.50, 'lodging', 'Hotel for training travel', '2026-06-04');

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
        'pending',
        NULL,
        NULL,
        NULL
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
        'pending',
        NULL,
        NULL,
        NULL
    );
