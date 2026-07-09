"""
dao layer: only talks to the database (no logic)

Find a user by username 

"""
from db.connection import get_connection


def find_user_by_username(username):
    conn = get_connection()
    try:
        cur = conn.cursor()
       
        cur.execute(" Select * from users WHERE username = ?", (username,))
        result = cur.fetchone()
        return result
    except Exception as e:
        print(f"Error finding the user: {e}")
        return None
    finally:
        conn.close()

    
    

