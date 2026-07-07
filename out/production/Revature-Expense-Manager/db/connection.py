"""
Where the SQLite connection is created 
so everythings imports from here 

"""
import sqlite3
import os

DB_PATH = os.path.join(os.path.dirname(__file__), '..', '..', 'database', 'expense_manager.db')

# Handles the connection
def get_connection():
    conn = sqlite3.connect(DB_PATH)
    return conn
    
    
