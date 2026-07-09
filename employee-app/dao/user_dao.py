"""
dao layer: only talks to the database (no logic)

Find a user by username 

"""
from db.connection import get_connection
from logger import get_logger

logger = get_logger(__name__)

# returns (1, "vanessa", "hashedpassword", "manager")
def find_user_by_username(username):
    conn = get_connection()
    try:
        cur = conn.cursor()
       
        cur.execute(" Select * from users WHERE username = ?", (username,))
        result = cur.fetchone()
        logger.info(f"Found user with username: {username}")
        return result
    except Exception as e:
        logger.error(f"Error finding the user: {e}")
        return None
    finally:
        conn.close()

    
    

